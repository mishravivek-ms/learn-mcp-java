package org.acme;

import org.acme.command.ChatCommand;
import org.acme.command.ToolsCommand;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "monkey-mcp", 
         mixinStandardHelpOptions = true, 
         version = "1.0.0", 
         description = "Console application to interact with AI and MCP servers", 
         subcommands = {ChatCommand.class, ToolsCommand.class })
public class MonkeyMcpApplication {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new MonkeyMcpApplication()).execute(args);
        System.exit(exitCode);
    }

}
