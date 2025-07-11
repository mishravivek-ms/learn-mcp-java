package org.acme.client;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import dev.langchain4j.mcp.McpToolProvider;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Service for managing MCP (Model Context Protocol) tools.
 * 
 * This service handles the registration of MCP servers from configuration
 * and provides access to available tools from those servers.
 */
@ApplicationScoped
public class ToolsService {

    private static final Logger LOG = LoggerFactory.getLogger(ToolsService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<McpClient> mcpClients = new ArrayList<>();
    private McpToolProvider toolProvider;

    /**
     * Initializes the MCP clients and tool provider on application startup.
     */
    @PostConstruct
    public void init() {
        LOG.info("Initializing MCP tools service...");
        try {
            registerMCPServers();
            initializeToolProvider();
            LOG.info("MCP tools service initialized successfully");
        } catch (Exception e) {
            LOG.error("Failed to initialize MCP tools service", e);
        }
    }

    /**
     * Registers MCP servers from the mcp.json configuration file.
     * Prioritizes loading from the working directory over bundled resource.
     */
    private void registerMCPServers() {
        InputStream inputStream = getMcpConfigStream();
        
        if (inputStream == null) {
            LOG.warn("mcp.json configuration file not found in working directory or resources");
            return;
        }
        
        try (InputStream is = inputStream) {
            JsonNode config = objectMapper.readTree(is);
            JsonNode servers = config.get("servers");

            if (servers == null || !servers.isObject()) {
                LOG.warn("No servers configuration found in mcp.json");
                return;
            }

            servers.fieldNames().forEachRemaining(serverName -> {
                try {
                    registerServer(serverName, servers.get(serverName));
                } catch (Exception e) {
                    LOG.error("Failed to register MCP server: " + serverName, e);
                }
            });

            LOG.info("MCP server registration completed. {} servers registered", mcpClients.size());

        } catch (IOException e) {
            LOG.error("Failed to read mcp.json configuration", e);
        }
    }
    
    /**
     * Gets the MCP configuration stream with prioritized loading:
     * 1. Check working directory for mcp.json
     * 2. Fall back to bundled resource
     * 
     * @return InputStream for mcp.json or null if not found
     */
    private InputStream getMcpConfigStream() {
        // First, try to load from working directory
        Path workingDirConfig = Paths.get("mcp.json");
        if (Files.exists(workingDirConfig)) {
            try {
                LOG.info("Loading mcp.json from working directory: {}", workingDirConfig.toAbsolutePath());
                return Files.newInputStream(workingDirConfig);
            } catch (IOException e) {
                LOG.warn("Failed to read mcp.json from working directory, falling back to bundled resource", e);
            }
        }
        
        // Fall back to bundled resource
        InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("mcp.json");
        if (resourceStream != null) {
            LOG.info("Loading mcp.json from bundled resource");
        }
        return resourceStream;
    }

    /**
     * Registers a single MCP server from configuration.
     */
    private void registerServer(String serverName, JsonNode serverConfig) {
        try {
            String url = serverConfig.get("url").asText();
            String type = serverConfig.get("type").asText();

            if (!"sse".equals(type)) {
                LOG.warn("Unsupported transport type '{}' for server: {}", type, serverName);
                return;
            }

            LOG.info("Registering MCP server: {} with URL: {}", serverName, url);

            McpTransport mcpTransport = new HttpMcpTransport.Builder()
                    .sseUrl(url)
                    .logRequests(false) // Disable logging for better performance
                    .logResponses(false)
                    .build();

            McpClient mcpClient = new DefaultMcpClient.Builder()
                    .key(serverName)
                    .transport(mcpTransport)
                    .build();

            // Add client to the list (connection will be tested when tools are requested)
            mcpClients.add(mcpClient);
            LOG.info("Successfully registered MCP server: {}", serverName);

        } catch (Exception e) {
            LOG.error("Failed to register MCP server: " + serverName, e);
        }
    }

    /**
     * Initializes the tool provider with registered MCP clients.
     */
    private void initializeToolProvider() {
        if (mcpClients.isEmpty()) {
            LOG.warn("No MCP clients registered, tool provider will be empty");
            return;
        }

        toolProvider = McpToolProvider.builder()
                .mcpClients(mcpClients.toArray(new McpClient[0]))
                .build();

        LOG.info("Tool provider initialized with {} MCP clients", mcpClients.size());
    }

    /**
     * Gets the list of available tools from all registered MCP servers.
     * 
     * @return List of tool specifications
     */
    public List<ToolSpecification> getAvailableTools() {
        List<ToolSpecification> allTools = new ArrayList<>();

        for (McpClient client : mcpClients) {
            try {
                List<ToolSpecification> clientTools = client.listTools();
                allTools.addAll(clientTools);
                LOG.debug("Retrieved {} tools from MCP server: {}", clientTools.size(), client.key());
            } catch (Exception e) {
                LOG.error("Failed to get tools from MCP server: " + client.key(), e);
            }
        }

        LOG.info("Retrieved {} total tools from {} MCP servers", allTools.size(), mcpClients.size());
        return allTools;
    }

    /**
     * Gets the number of registered MCP clients.
     * 
     * @return Number of registered clients
     */
    public int getRegisteredClientsCount() {
        return mcpClients.size();
    }

    /**
     * Gets the tool provider for use in other services.
     * 
     * @return The MCP tool provider
     */
    public McpToolProvider getToolProvider() {
        return toolProvider;
    }
}
