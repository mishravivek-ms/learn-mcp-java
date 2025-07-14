package org.acme.command;

import static java.lang.System.err;
import static java.lang.System.out;

import org.acme.client.ToolsService;

import dev.langchain4j.agent.tool.ToolSpecification;
import picocli.CommandLine.Command;

@Command(name = "tools", description = "List available MCP tools from registered servers")
public class ToolsCommand implements Runnable {

    ToolsService toolsService = new ToolsService();

    @Override
    public void run() {
        try {
            out.println("Available MCP Tools:");
            out.println("====================");

            var tools = toolsService.getAvailableTools();

            if (tools.isEmpty()) {
                out.println("No tools available from registered MCP servers.");
                out.println("Please ensure MCP servers are running and properly configured.");
                return;
            }

            for (var tool : tools) {
                displayTool(tool);
            }

            out.println("\nTotal tools available: " + tools.size());

        } catch (Exception e) {
            err.println("Error listing MCP tools: " + e.getMessage());
        }
    }

    private void displayTool(ToolSpecification tool) {
        out.println("\n• " + tool.name());

        if (tool.description() != null && !tool.description().isEmpty()) {
            out.println("  Description: " + tool.description());
        }

        if (tool.parameters() != null) {
            out.println("  Parameters: " + tool.parameters().toString());
        }
    }
}
