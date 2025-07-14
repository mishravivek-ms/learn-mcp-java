package org.acme.command;

import static java.lang.System.err;
import static java.lang.System.out;

import java.util.function.Consumer;

import org.acme.client.ToolsService;

import dev.langchain4j.agent.tool.ToolSpecification;
import picocli.CommandLine.Command;

@Command(name = "tools", description = "List available MCP tools from registered servers")
public class ToolsCommand implements Runnable {

    ToolsService toolsService = new ToolsService();

    @Override
    public void run() {
        try {
            out.println("========================");
            out.println("  Available MCP Tools:  ");
            out.println("------------------------");

            var tools = toolsService.getAvailableTools();

            if (tools.isEmpty()) {
                out.println("   No tools available from registered MCP servers.");
                out.println("   Please ensure MCP servers are running and properly configured.");
                return;
            }

            Consumer<ToolSpecification> printFunction = (ts) -> {
                out.println("# TOOL NAME: " + ts.name());
                if (ts.description() != null && !ts.description().isEmpty()) {
                    out.println("## Description: " + ts.description());
                }
                if (ts.parameters() != null) {
                    out.println("## Parameters: " + ts.parameters().toString());
                }
            };

            tools.stream().forEach(printFunction);
            out.println("\nTotal tools available: " + tools.size());
        } catch (Exception e) {
            err.println("Error listing MCP tools: " + e.getMessage());
        }
    }

}
