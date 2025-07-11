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