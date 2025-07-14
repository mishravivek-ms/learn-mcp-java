package org.acme.command;

import java.util.List;

import org.acme.client.ToolsService;

import dev.langchain4j.agent.tool.ToolSpecification;
import picocli.CommandLine.Command;

/**
 * Command to list available MCP tools from registered servers.
 * 
 * This command connects to registered MCP servers and displays
 * information about available tools and their specifications.
 */
@Command(name = "tools", 
         description = "List available MCP tools from registered servers")
public class ToolsCommand implements Runnable {

    ToolsService toolsService;

    public ToolsCommand() {
        toolsService = new ToolsService();
    }

    @Override
    public void run() {
        try {
            System.out.println("Available MCP Tools:");
            System.out.println("==================");
            
            List<ToolSpecification> tools = toolsService.getAvailableTools();
            
            if (tools.isEmpty()) {
                System.out.println("No tools available from registered MCP servers.");
                System.out.println("Please ensure MCP servers are running and properly configured.");
                return;
            }

            for (ToolSpecification tool : tools) {
                displayTool(tool);
            }
            
            System.out.println("\nTotal tools available: " + tools.size());
            
        } catch (Exception e) {
            System.err.println("Error listing MCP tools: " + e.getMessage());
        }
    }

    private void displayTool(ToolSpecification tool) {
        System.out.println("\n• " + tool.name());
        
        if (tool.description() != null && !tool.description().isEmpty()) {
            System.out.println("  Description: " + tool.description());
        }
        
        if (tool.parameters() != null) {
            System.out.println("  Parameters: " + tool.parameters().toString());
        }
    }
}
