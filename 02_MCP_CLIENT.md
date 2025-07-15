# Part 3: Building Your Own MCP Client Application

> **Navigation**: [← Back to MCP Server](01_MCP_SERVER.md) | [Back to Overview](READM```bash
# List all available MCP tools
./mvnw quarkus:dev -Dquarkus.args="tools"

# Start interactive chat with AI assistant
./mvnw quarkus:dev -Dquarkus.args="chat"

# Show help
./mvnw quarkus:dev -Dquarkus.args="help"
```

### Interactive Chat Mode
Start an interactive chat session with the AI assistant:

```bash
./mvnw quarkus:dev -Dquarkus.args="chat"
```

Example conversation:
```
=== Monkey MCP Chat ===
Starting chat with Ollama (llama3.2) + MCP Tools
Available tools: 4 monkey species tools
Type 'exit', 'quit', or 'bye' to end the conversation
================================================================

You: What monkey species do you know?
AI: I can help you with monkey species information! Let me check what species are available...

[AI calls list_monkey_species tool]

I have information about several monkey species including:
- Proboscis Monkey (Borneo)
- Golden Snub-nosed Monkey (China)
- Mandrill (Central Africa)
- Howler Monkey (Central/South America)
- Spider Monkey (Central/South America)

Would you like to know more details about any specific species?

You: Tell me about the Proboscis Monkey
AI: Let me get detailed information about the Proboscis Monkey...

[AI calls get_monkey_details tool with "Proboscis Monkey"]
```

### Using the MCP Client

In this section, you'll learn how to build an MCP client application using Quarkus that can communicate with the monkey species MCP server you just created. This console application will demonstrate how to:

- Connect to an MCP server via HTTP SSE transport
- List available MCP tools from the server
- Call MCP tools with parameters
- Handle responses and display results

## 🎯 What You'll Learn
- How to create a console application with Quarkus and PicoCLI
- How to implement MCP client-server communication
- How to integrate LangChain4j with Ollama for AI chat
- How to register and use MCP servers from configuration
- How to handle JSON message parsing and error handling

## Step-by-Step Walkthrough

### Create a new Quarkus Console Application

Create a new Quarkus project named **monkey-mcp-client**. 
For that, run the following Quarkus CLI command in your terminal in your workspace folder:

```bash
quarkus create app --no-code -x picocli,langchain4j-mcp,langchain4j-ollama monkey-mcp-client
```

> **Note**: We're using `picocli` extension to create a console application interface, and `rest-client-jackson` for HTTP communication.

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

### Client Configuration
Create a `mcp.json` file in your project root with the following content:

```json
{
  "servers": {
    "monkey-server": {
      "type": "sse",
      "url": "http://localhost:8080/mcp/sse"
    }
  }
}
```

This configuration file tells the client where to find your MCP server and what transport protocol to use.

### Build the MCP Client using GitHub Copilot

Now that your project is set up, let's use GitHub Copilot to help us implement the MCP Client application.

#### Provide GitHub Copilot Instructions for better context

Create a file named `java-cli-mcp-client.instructions.md` with the following content:

```markdown
      # Console Application to interact with AI and MCP Servers

      Build a console app with Java 21, Picocli, and LangChain4J with Ollama for chat interactions that also registers MCP servers for additional tools.

      - Use Picocli version 4.7.7
      - Use LangChain4J version 1.1.0-rc1
      - Use LangChain4J API to interact with Ollama LLMs
      - Use LangChain4J to register MCP servers and their tools based on the `mcp.json` file in the current directory
      - Use LLangChain4J to list available tools from the registered MCP servers
      - Use LangChain4J to chat with the Ollama LLM 

      ## Stack
      - Java 21 
      - Picocli for console application interface
      - LangChain4J for chat interactions and MCP client support
      - Ollama LLMs for AI chat
      - Register provided MCP servers in the `mcp.json` file

      ## Structure
      - Use standard Java naming conventions (PascalCase classes, camelCase methods)
      - Organize in packages: `model`, `client`, `command`
      - Console application with multiple commands
      - Add Javadoc for public methods

      ## Features
      - Automatically registers MCP servers from the `mcp.json` file in the current directory
      - List available tools from the server
      - Chat with Ollama LLM using LangChain4J

      ## Console Commands
      - `tools` - List all available MCP tools from registered servers in `mcp.json`
      - `chat` - Start a chat session with the default Ollama model llama3.2
      - `help` - Show help for available commands

      ## Maven
      - Use Maven for build and dependency management
      - Configure Maven to build an uber jar for easy command-line execution
      - Generate a run.sh script for easy execution, and pass arguments to the main class
```

Now, you can ask GitHub Copilot to help you implement the MCP client by providing it with the following prompt:

```plaintext
Implement a console chat application that also registers the Monkey MCP server offered through HTTP SSE transport for additional tools.

The monkey species MCP server is at http://localhost:8080/mcp/sse
```

### Test the MCP Client

First, make sure your MCP server is running. In the `monkey-mcp-server` directory, run:

```bash
./mvnw quarkus:dev
```

Then, in a new terminal, navigate to your MCP client directory and run:

```bash
chmod +x run.sh
./run.sh
```

### Using the MCP Client

Once the client is running, you can use the following commands:

```bash
# Show help
./run.sh -h

# List all available MCP tools
./run.sh tools

# Start chat with the AI assistant
./run.sh chat
```

## Troubleshooting

### Common Issues

1. **Connection refused**: Make sure the MCP server is running on `http://localhost:8080`
2. **JSON parsing errors**: Verify the server is returning valid MCP protocol messages
3. **Command not found**: Check that Picocli commands are properly annotated

### Debugging Tips

- Debug from Visual Studio Code using the Java debugger starting with the main class
- Check the server logs for any errors
- Use the MCP Inspector tool to verify server responses: `npx @modelcontextprotocol/inspector`
- Test individual HTTP requests using curl or Postman

## Conclusion

Congratulations! You've successfully built an MCP client application that can communicate with your monkey species MCP server. This console application demonstrates:

- How to implement MCP client-server communication
- Proper handling of HTTP SSE transport
- Console application development with Picocli
- JSON message parsing and formatting
- Error handling and user feedback

Your MCP client can now:
- Connect to MCP servers
- List available tools
- Call tools with parameters
- Display formatted results
- Handle errors gracefully

This foundation can be extended to build more sophisticated MCP clients, including graphical applications, web interfaces, or integration with other systems.

### 🎉 Tutorial Complete!

You've successfully built:
- ✅ A Quarkus-based MCP server with monkey species data
- ✅ An interactive CLI client using LangChain4j
- ✅ Integration with Ollama for AI chat capabilities
- ✅ GitHub Copilot integration for enhanced development

### Next Steps
- Explore adding more MCP tools to your server
- Implement authentication and security
- Deploy your applications to production
- Contribute to the MCP ecosystem

---

> **Congratulations!** You've completed the Monkey MCP Java tutorial series.