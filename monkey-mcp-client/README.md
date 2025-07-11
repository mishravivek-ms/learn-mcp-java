# Monkey MCP Console Application

A Quarkus console application that integrates with AI models through Ollama and registers MCP (Model Context Protocol) servers for additional tools and capabilities.

## Features

- **Chat Interface**: Interactive console chat with Ollama LLMs
- **MCP Integration**: Connect to the Monkey species MCP server via HTTP SSE transport
- **Tool Discovery**: List and use available tools from MCP servers
- **Modular Design**: Clean separation of concerns with services and commands

## Technology Stack

- **Java 21** with Quarkus Framework
- **Picocli** for console application interface
- **LangChain4J** for AI chat interactions and MCP client support
- **Ollama** for local LLM hosting

## Prerequisites

1. **Java 21** or later
2. **Ollama** running locally on `http://localhost:11434` with `llama3.2` model
3. **Monkey MCP Server** running on `http://localhost:8080/mcp/sse`

## Quick Start

### 1. Install Ollama and pull the model
```bash
# Install Ollama (macOS)
brew install ollama

# Start Ollama service
ollama serve

# Pull the llama3.2 model
ollama pull llama3.2
```

### 2. Start the Monkey MCP Server
Ensure the monkey species MCP server is running at `http://localhost:8080/mcp/sse`.

### 3. Build and run the application
```bash
# Build the application
./mvnw clean package

# Run the application
java -jar target/quarkus-app/quarkus-run.jar

# Or use Quarkus dev mode
./mvnw quarkus:dev
```

## Available Commands

### Chat Command
Start an interactive chat session with the Ollama LLM:
```bash
java -jar target/quarkus-app/quarkus-run.jar chat
```

### Tools Command
List all available MCP tools from registered servers:
```bash
java -jar target/quarkus-app/quarkus-run.jar tools
```

### Help Command
Show available commands:
```bash
java -jar target/quarkus-app/quarkus-run.jar help
```

## MCP Configuration

The application connects to MCP servers defined in `mcp.json`:

```json
{
  "servers": {
    "monkey-species": {
      "command": "http",
      "args": {
        "url": "http://localhost:8080/mcp/sse",
        "transport": "sse"
      },
      "description": "Monkey species information server via HTTP SSE transport"
    }
  }
}
```

## Application Configuration

Key configuration properties in `application.properties`:

```properties
# Ollama Configuration
quarkus.langchain4j.ollama.base-url=http://localhost:11434
quarkus.langchain4j.ollama.model-name=llama3.2
quarkus.langchain4j.ollama.temperature=0.7

# MCP Configuration  
mcp.monkey.server.url=http://localhost:8080/mcp/sse
mcp.monkey.server.enabled=true
```

## Project Structure

```
src/main/java/org/acme/
├── MonkeyMcpApplication.java     # Main application class
├── client/
│   ├── ChatService.java         # Ollama chat service
│   └── McpClientService.java    # MCP client service
└── command/
    ├── ChatCommand.java         # Chat command implementation
    └── ToolsCommand.java        # Tools command implementation
```

## Available MCP Tools

The monkey species MCP server provides these tools:

1. **get_monkey_species** - Get information about a specific monkey species
2. **list_monkey_species** - List all available monkey species  
3. **get_monkey_habitat** - Get habitat information for a monkey species

## Example Usage

### Chat Session
```
You: Tell me about capuchin monkeys
AI: I can help you with monkey species information! I have access to MCP tools that can provide details about various monkey species, their habitats, and characteristics...

You: List all monkey species
AI: I have access to several MCP tools for monkey species information:
- get_monkey_species: Get detailed information about a specific species
- list_monkey_species: List all available monkey species
- get_monkey_habitat: Get habitat information for a species

You: exit
Goodbye!
```

### Tools Listing
```
$ java -jar target/quarkus-app/quarkus-run.jar tools

=== Available MCP Tools ===

Connected to: http://localhost:8080/mcp/sse
Available tools:

1. get_monkey_species
   Description: Get information about a specific monkey species
   Parameters:
     - species_name (string): Name of the monkey species to get information about

2. list_monkey_species
   Description: List all available monkey species

3. get_monkey_habitat
   Description: Get habitat information for a monkey species
   Parameters:
     - species_name (string): Name of the monkey species

Total: 3 tools available
```

## Development

### Running in Development Mode
```bash
./mvnw quarkus:dev
```

### Building Native Executable
```bash
./mvnw package -Dnative
```

## Troubleshooting

1. **Ollama Connection Issues**: Ensure Ollama is running on `localhost:11434`
2. **MCP Server Connection**: Verify the monkey MCP server is accessible at `localhost:8080/mcp/sse`
3. **Model Not Found**: Pull the required model with `ollama pull llama3.2`

## Future Enhancements

- Real MCP protocol implementation with proper HTTP SSE transport
- Dynamic MCP server registration from `mcp.json`
- Tool execution integration in chat sessions
- Multiple LLM model support
- Enhanced error handling and retry logic
