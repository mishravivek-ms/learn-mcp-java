package org.acme.client;

import static java.lang.System.err;
import static java.lang.System.out;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;

public class ToolsService {

    private final List<McpClient> mcpClients = new ArrayList<>();
    private McpToolProvider toolProvider;

    public ToolsService() {
        out.println("→ Initializing MCP tools service...");
        try {
            registerMCPServers();
            initializeToolProvider();
            out.println("✓ MCP tools service initialized successfully");
        } catch (Exception e) {
            err.printf("✗ Failed to initialize MCP tools service: %s%n", e.getMessage());
        }
    }

    private void registerMCPServers() {
        var inputStream = getMcpConfigStream();

        if (inputStream == null) {
            out.println("⚠ mcp.json configuration file not found in working directory or resources");
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try (var is = inputStream) {
            var config = objectMapper.readTree(is);
            var servers = config.get("servers");

            if (servers == null || !servers.isObject()) {
                out.println("⚠ No servers configuration found in mcp.json");
                return;
            }

            servers.fieldNames().forEachRemaining(serverName -> {
                registerServer(serverName, servers.get(serverName));
            });

            out.printf("✓ MCP server registration completed. %d server%s registered%n",
                    mcpClients.size(), mcpClients.size() == 1 ? "" : "s");
        } catch (IOException e) {
            err.printf("✗ Failed to read mcp.json configuration: %s%n", e.getMessage());
        }
    }

    private InputStream getMcpConfigStream() {
        var workingDirConfig = Paths.get("mcp.json");
        if (Files.exists(workingDirConfig)) {
            try {
                out.printf("→ Loading mcp.json from working directory: %s%n", workingDirConfig.toAbsolutePath());
                return Files.newInputStream(workingDirConfig);
            } catch (IOException e) {
                err.printf("✗ Failed to read mcp.json from working directory, falling back to bundled resource: %s%n",
                        e.getMessage());
            }
        }

        var resourceStream = getClass().getClassLoader().getResourceAsStream("mcp.json");
        if (resourceStream != null) {
            out.println("→ Loading mcp.json from bundled resource");
        }
        return resourceStream;
    }

    private void registerServer(String serverName, JsonNode serverConfig) {
        try {
            var url = serverConfig.get("url").asText();
            var type = serverConfig.get("type").asText();

            if (!"sse".equals(type)) {
                err.printf("✗ Unsupported transport type '%s' for server: %s%n", type, serverName);
                return;
            }

            out.printf("→ Registering MCP server: %s with URL: %s%n", serverName, url);

            var mcpTransport = new HttpMcpTransport.Builder()
                    .sseUrl(url)
                    .logRequests(false) // Disable logging for better performance
                    .logResponses(false)
                    .build();

            var mcpClient = new DefaultMcpClient.Builder()
                    .key(serverName)
                    .transport(mcpTransport)
                    .build();

            mcpClients.add(mcpClient);
            out.printf("✓ Successfully registered MCP server: %s%n", serverName);

        } catch (Exception e) {
            err.printf("✗ Failed to register MCP server: %s%n", serverName);
        }
    }

    private void initializeToolProvider() {
        if (mcpClients.isEmpty()) {
            out.println("⚠ No MCP clients registered, tool provider will be empty");
            return;
        }

        toolProvider = McpToolProvider.builder()
                .mcpClients(mcpClients.toArray(new McpClient[0]))
                .build();

        out.printf("✓ Tool provider initialized with %d MCP client%s%n",
                mcpClients.size(), mcpClients.size() == 1 ? "" : "s");
    }

    public List<ToolSpecification> getAvailableTools() {
        var allTools = new ArrayList<ToolSpecification>();

        for (var client : mcpClients) {
            var clientTools = client.listTools();
            allTools.addAll(clientTools);
            out.printf("✓ Retrieved %d tool%s from MCP server: %s%n",
                    clientTools.size(), clientTools.size() == 1 ? "" : "s", client.key());
        }

        out.printf("→ Retrieved %d total tool(s) from %d MCP server(s)%n", allTools.size(), mcpClients.size());
        return allTools;
    }

    public McpToolProvider getToolProvider() {
        return toolProvider;
    }

}
