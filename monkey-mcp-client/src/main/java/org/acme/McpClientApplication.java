package org.acme;

import org.acme.command.ChatCommand;
import org.acme.command.ToolsCommand;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "mcp-client", 
         mixinStandardHelpOptions = true, 
         version = "1.0.0", 
         description = "Console application to interact with AI and MCP servers", 
         subcommands = {ChatCommand.class, ToolsCommand.class })
public class McpClientApplication {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new McpClientApplication()).execute(args);
        System.exit(exitCode);
    }

}
