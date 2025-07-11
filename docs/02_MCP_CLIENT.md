# Part 3: Building Your Own MCP Client Application

> **Navigation**: [← Back to MCP Server](01_MCP_SERVER.md) | [Next: Advanced Topics →](03_ADVANCED_TOPICS.md)

## Overview

In this section, you'll learn how to build an MCP client application using Quarkus that can communicate with the monkey species MCP server you just created. This console application will demonstrate how to:

- Connect to an MCP server via HTTP SSE transport
- List available MCP tools from the server
- Call MCP tools with parameters
- Handle responses and display results

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

### Build the MCP Client using GitHub Copilot

Now that your project is set up, let's use GitHub Copilot to help us implement the MCP Client application.

#### Provide GitHub Copilot Instructions for better context

Create the folder `.github` and inside it create a file named `copilot-instructions.md` with the following content:

```markdown
      # Quarkus Console Application to interact with AI and MCP Servers

      Build a console app with Java 21, Quarkus, and LangChain4J with Ollama for chat interactions that also registers MCP servers for additional tools.

      - Use LangChain4J API to interact with Ollama LLMs
      - Use LangChain4J to register MCP servers and their tools based on the `mcp.json` file in the current directory
      - Use LLangChain4J to list available tools from the registered MCP servers
      - Use LangChain4J to chat with the Ollama LLM 

      ## Stack
      - Java 21 with Quarkus Framework
      - Picocli for console application interface
      - LangChain4J for chat interactions and MCP client support
      - Ollama LLMs for AI chat
      - Register provided MCP servers in the `mcp.json` file

      ## Quick Start
      ```bash
      quarkus create app --no-code -x picocli,langchain4j-ollama monkey-mcp-client
      ```

      ## Structure
      - Use standard Java naming conventions (PascalCase classes, camelCase methods)
      - Organize in packages: `model`, `client`, `command`
      - Console application with multiple commands
      - Add Javadoc for public methods

      ## Features
      - Chat with Ollama LLM using LangChain4J
      - Register MCP servers from the `mcp.json` file in the current directory
      - List available tools from the server

      ## Console Commands
      - `tools` - List all available MCP tools from registered servers in `mcp.json`
      - `chat` - Start a chat session with the default Ollama model llama3.2
      - `help` - Show help for available commands
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
./mvnw quarkus:dev
```

### Using the MCP Client

Once the client is running, you can use the following commands:

```bash
# List all available MCP tools
./mvnw quarkus:dev -Dquarkus.args="list-tools"

# List all monkey species
./mvnw quarkus:dev -Dquarkus.args="list-monkeys"

# Get details for a specific monkey species
./mvnw quarkus:dev -Dquarkus.args="get-monkey 'Proboscis Monkey'"

# Get a random monkey species
./mvnw quarkus:dev -Dquarkus.args="random-monkey"

# Show help
./mvnw quarkus:dev -Dquarkus.args="help"
```

### Building a Native Executable (Optional)

For faster startup and lower memory usage, you can build a native executable:

```bash
./mvnw package -Pnative
```

Then run the native executable:

```bash
./target/monkey-mcp-client-1.0.0-SNAPSHOT-runner list-monkeys
```

### Expected Output

When you run the `list-monkeys` command, you should see output similar to:

```
🐵 Monkey Species List:
====================
1. Proboscis Monkey (Borneo)
2. Golden Snub-nosed Monkey (China)
3. Mandrill (Central Africa)
4. Howler Monkey (Central/South America)
5. Spider Monkey (Central/South America)

Total species: 5
```

When you run `get-monkey "Proboscis Monkey"`, you should see:

```
🐵 Proboscis Monkey Details:
==========================
Species: Proboscis Monkey
Location: Borneo
Population: 15,000
Coordinates: 0.961883, 114.55485
Accessed: 2 times

Details: The proboscis monkey or long-nosed monkey, known as the bekantan in Malay, is a reddish-brown arboreal Old World monkey that is endemic to the south-east Asian island of Borneo.
```

## Troubleshooting

### Common Issues

1. **Connection refused**: Make sure the MCP server is running on `http://localhost:8080`
2. **JSON parsing errors**: Verify the server is returning valid MCP protocol messages
3. **Command not found**: Check that Picocli commands are properly annotated
4. **SSL/TLS issues**: For HTTPS connections, ensure certificates are properly configured

### Debugging Tips

- Use `./mvnw quarkus:dev -Dquarkus.log.level=DEBUG` for detailed logging
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

---

> **Next Step**: Explore advanced MCP topics like authentication, custom transports, and server clustering.

**Continue to**: [Part 4 - Advanced MCP Topics →](03_ADVANCED_TOPICS.md)