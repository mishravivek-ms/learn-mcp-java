package org.acme;

import org.acme.command.ChatCommand;
import org.acme.command.ToolsCommand;

import io.quarkus.picocli.runtime.annotations.TopCommand;
import picocli.CommandLine.Command;

/**
 * Main application class for the Monkey MCP Console Application.
 * 
 * This application provides a console interface to interact with AI models
 * through Ollama and register MCP (Model Context Protocol) servers for
 * additional tools and capabilities.
 */
@TopCommand
@Command(name = "monkey-mcp", mixinStandardHelpOptions = true, version = "1.0.0", description = "Console application to interact with AI and MCP servers", subcommands = {
        ChatCommand.class, ToolsCommand.class })
public class MonkeyMcpApplication implements Runnable {
    
    @Override
    public void run() {
        // Default behavior when no subcommand is specified
        System.out.println("Monkey MCP Console Application");
        System.out.println("Use 'help' to see available commands.");
        System.out.println("Available commands:");
        System.out.println("  chat  - Start a chat session with Ollama LLM");
        System.out.println("  tools - List available MCP tools");
        System.out.println("  help  - Show this help message");
    }
}
