# Part 3: Building Your Own MCP Client Application

> **Navigation**: [← Back to MCP Server](01_MCP_SERVER.md) | [Back to Overview](README.md)## Overview

In this section, you'll learn how to build an MCP client application that can communicate with the monkey species MCP server you just created. This console application will demonstrate how to:

- Connect to an MCP server via HTTP SSE transport
- List available MCP tools from the server
- Call MCP tools with parameters
- Handle responses and display results

## 🎯 What You'll Learn
- How to create a console application with Java 21, Maven, and PicoCLI
- How to implement MCP client-server communication using LangChain4j
- How to integrate LangChain4j with Ollama for AI chat capabilities
- How to register and use MCP servers from JSON configuration
- How to build and package a distributable JAR application

## Step-by-Step Walkthrough

### Create a new Java Maven Project

Create a new Java Maven project named **monkey-mcp-client**. 
For that, run the following Maven command in your terminal in your workspace folder:

```bash
mvn archetype:generate -DgroupId=org.acme -DartifactId=monkey-mcp-client -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
```

Alternatively, you can create the project structure manually or use your IDE to create a new Maven project.

> **Note**: We're using `picocli` for console application interface, and `langchain4j-mcp` with `langchain4j-ollama` for MCP and AI chat functionality.

### Navigate to the project directory

```bash
cd monkey-mcp-client
```

### Open the project in VS Code

```bash
code .
```

- Ensure the Java Extension Pack is installed and active
- Verify that the project builds successfully with `./mvnw verify`

### Build the MCP Client using GitHub Copilot

Now that your project is set up, let's use GitHub Copilot to help us implement the MCP Client application.

#### Provide GitHub Copilot Instructions for better context

Inside the folder `.github`, create a file named `java-console-app-mcp-client.instructions.md` with the following content:

```markdown
    # Java CLI MCP Client with LangChain4J, PicoCLI, and Ollama

    Build a Java 21 console application that connects to MCP servers and enables AI chat with LangChain4J + Ollama.

    ## Dependencies
    | groupId         | artifactId         | version     |
    | --------------- | ------------------ | ----------- |
    | info.picocli    | picocli            | 4.7.7       |
    | dev.langchain4j | langchain4j-ollama | 1.1.0-rc1   |
    | dev.langchain4j | langchain4j-mcp    | 1.1.0-beta7 |
    
    ## Main Application
    - Use @Command annotation with subcommands: ChatCommand.class, ToolsCommand.class
    - Main method creates CommandLine and executes with args
    - Configure logging from logging.properties file and use SEVERE level for everything

    ## ChatService.java
    - Uses OllamaChatModel.builder() with baseUrl "http://localhost:11434", modelName "llama3.2"
    - Timeout of 10 seconds, temperature 0.7
    - Integrates with ToolsService for MCP tool access
    - Inner interface Bot with @SystemMessage and chat methods
    - Uses AiServices.builder() to create bot with chat memory and tool provider
    - startInteractiveChat() method with Scanner for user input
    - Handles "exit", "quit", "bye" commands to end chat
    - Error handling for unavailable services

    ## Tools Service
    - Reads mcp.json configuration file from working directory
    - Registers MCP servers using DefaultMcpClient with HttpMcpTransport
    - Creates McpToolProvider from registered clients
    - getAvailableTools() returns List<ToolSpecification>
    - getToolProvider() returns McpToolProvider for AI integration
    - JSON parsing with Jackson ObjectMapper
    - Error handling for missing configuration or server connection issues

    ## Chat Command
    - @Command(name = "chat", description = "Start a chat session")
    - Creates ChatService instance and calls startInteractiveChat()
    - Checks if service is available before starting

    ## Tools Command
    - @Command(name = "tools", description = "List available MCP tools from registered servers")
    - Creates ToolsService instance and displays available tools
    - Formatted output with tool names and descriptions
    - Shows helpful message if no tools available

    ## Configuration (mcp.json)
    ```json
    { "servers": {
        "monkeymcp": {
          "type": "sse", 
          "url": "http://localhost:8080/mcp/sse"
        }
      }
    }
    ```

    ## Implementation Notes
    - Use System.out for regular output, System.err for errors
    - Include proper exception handling for network operations
    - Use Duration.ofSeconds(10) for timeouts
    - Generate random user IDs for chat sessions
    - Support both working directory for mcp.json file
    - Use Jackson for JSON parsing
    - Implement proper resource cleanup with try-with-resources
```

Now, you can ask GitHub Copilot to help you implement the MCP client by providing it with the following prompt:

```plaintext
Implement a console chat application that also registers the Monkey MCP server offered through HTTP SSE transport for additional tools.

The monkey species MCP server is at http://localhost:8080/mcp/sse
```

### Client Configuration
Ensure that GitHub Copilot created an `mcp.json` file in your project root with the following content:

```json
{
  "servers": {
    "monkeymcp": {
      "type": "sse",
      "url": "http://localhost:8080/mcp/sse"
    }
  }
}
```

This configuration file tells the client where to find your MCP server and what transport protocol to use.


### Test the MCP Client

First, make sure your MCP server is running. In the `monkey-mcp-server` directory, run:

```bash
./mvnw quarkus:dev
```

Then, in a new terminal, navigate to your MCP client directory and build the project:

```bash
cd monkey-mcp-client
./mvnw package
```

### Using the MCP Client

Once the client is built, you can use the following commands:

```bash
# Show help
java -jar target/monkey-mcp-client.jar -h

# List all available MCP tools
java -jar target/monkey-mcp-client.jar tools

# Start chat with the AI assistant
java -jar target/monkey-mcp-client.jar chat
```

### Expected Output

When you run the `tools` command, you should see output similar to:

```
═════ MCP Tools ═════

🔧 list_monkey_species
   Lists all available monkey species in the database

🔧 get_monkey_details  
   Gets detailed information about a specific monkey species
   Parameter: species (string) - The name of the monkey species

🔧 get_random_monkey
   Returns a random monkey species from the database

🔧 get_monkey_statistics
   Returns statistics about the monkey species database

Total tools available: 4
```

When you run the `chat` command and ask about monkeys, you should see:

```
=== Monkey MCP Chat ===
Starting chat with Ollama (llama3.2) + MCP Tools
Available tools: 4 monkey species tools
Type 'exit', 'quit', or 'bye' to end the conversation
Try asking: 'What monkey species do you know?' or 'Tell me about a random monkey'
================================================================

You: What monkey species do you know?
→ Initializing MCP tools...
✓ MCP tools initialized successfully
AI: I can help you with information about various monkey species! Let me check what species are available in our database...

I have information about several fascinating monkey species including:
- Proboscis Monkey from Borneo
- Golden Snub-nosed Monkey from China  
- Mandrill from Central Africa
- Howler Monkey from Central/South America
- Spider Monkey from Central/South America

Would you like to know more details about any specific species?
```

### Building the Application

To build the application and create an executable JAR:

```bash
./mvnw package
```

This will create `monkey-mcp-client.jar` in the `target/` directory using the Maven Assembly Plugin configuration.

For easier execution, you can also run the application directly with Maven:

```bash
# List tools
./mvnw exec:java -Dexec.mainClass="org.acme.McpClientApplication" -Dexec.args="tools"

# Start chat
./mvnw exec:java -Dexec.mainClass="org.acme.McpClientApplication" -Dexec.args="chat"
```

## Troubleshooting

### Common Issues

1. **Connection refused**: Make sure the MCP server is running on `http://localhost:8080`
2. **JSON parsing errors**: Verify the server is returning valid MCP protocol messages
3. **Command not found**: Check that Picocli commands are properly annotated

### Debugging Tips

- Debug from Visual Studio Code using the Java debugger with the main class `org.acme.McpClientApplication`
- Check the server logs for any errors in the monkey-mcp-server terminal
- Use the MCP Inspector tool to verify server responses: `npx @modelcontextprotocol/inspector`
- Test the server endpoint directly: `curl -X GET http://localhost:8080/mcp/sse`
- Verify the `mcp.json` file is in the correct location and has valid JSON syntax
- Check that Ollama is running locally: `ollama list` and `ollama serve`

## Conclusion

Congratulations! You've successfully built an MCP client application that can communicate with your monkey species MCP server. This console application demonstrates:

- How to implement MCP client-server communication using LangChain4j
- Proper handling of HTTP SSE transport with Java
- Console application development with PicoCLI and Maven
- JSON configuration parsing and MCP server registration
- AI chat integration with Ollama and tool usage

Your MCP client can now:
- Connect to MCP servers via HTTP SSE
- List available tools from registered servers
- Enable AI chat with automatic tool access
- Display formatted results and handle errors gracefully
- Run as a standalone JAR application

This foundation can be extended to build more sophisticated MCP clients, including graphical applications, web interfaces, or integration with other systems.

### 🎉 Tutorial Complete!

You've successfully built:
- ✅ A Quarkus-based MCP server with monkey species data and HTTP SSE transport
- ✅ An interactive Java CLI client using LangChain4j and Maven
- ✅ Integration with Ollama llama3.2 for AI chat capabilities
- ✅ MCP tool registration and automatic AI tool usage
- ✅ GitHub Copilot integration for enhanced development workflow

### Next Steps
- Explore adding more MCP tools to your server
- Implement authentication and security
- Deploy your applications to production
- Contribute to the MCP ecosystem

---

> **Congratulations!** You've completed the Monkey MCP Java tutorial series.