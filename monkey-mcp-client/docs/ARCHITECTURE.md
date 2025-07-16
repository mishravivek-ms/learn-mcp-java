# MCP Java Client - Architecture Overview

## 📖 Table of Contents
1. [System Architecture](#system-architecture)
2. [Key Components](#key-components)
3. [Data Flow](#data-flow)
4. [Role of LLM](#role-of-llm)
5. [Component Interactions](#component-interactions)

---

## 🏗️ System Architecture

The MCP Java Client follows a layered architecture that integrates:
- **Command Line Interface (CLI)** - User interaction layer
- **Chat Service** - LLM orchestration and conversation management
- **Tools Service** - MCP server integration and tool management
- **Ollama Integration** - Local LLM model execution
- **MCP Protocol** - Communication with external MCP servers

```
┌─────────────────────────────────────────────────────────────┐
│                    USER INTERFACE                          │
├─────────────────────────────────────────────────────────────┤
│  McpClientApplication (CLI Entry Point)                    │
│  ├── ChatCommand                                           │
│  └── ToolsCommand                                          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                  CORE SERVICES                             │
├─────────────────────────────────────────────────────────────┤
│  ChatService (LLM Orchestration)                          │
│  ├── Bot Interface (AI Services)                          │
│  ├── Memory Management                                     │
│  └── Tool Integration                                      │
│                                                            │
│  ToolsService (MCP Management)                             │
│  ├── MCP Client Registry                                   │
│  ├── Tool Provider                                         │
│  └── Configuration Management                              │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                 EXTERNAL INTEGRATIONS                      │
├─────────────────────────────────────────────────────────────┤
│  Ollama (Local LLM)          │  MCP Servers                │
│  ├── llama3.2 Model         │  ├── MonkeyMCP Server       │
│  ├── Chat Model             │  ├── Tool Specifications    │
│  └── HTTP Client            │  └── SSE Transport          │
└─────────────────────────────────────────────────────────────┘
```

---

## 🧩 Key Components

### 1. **McpClientApplication**
- **Purpose**: CLI entry point and command routing
- **Technology**: PicoCLI framework
- **Commands**: 
  - `chat` - Start interactive chat session
  - `tools` - List available MCP tools
- **Configuration**: Logging setup and application lifecycle

### 2. **ChatService**
- **Purpose**: LLM orchestration and conversation management
- **Key Features**:
  - Ollama model integration
  - Memory management (20-message window)
  - Tool integration via MCP
  - Error handling and retry logic
- **Configuration**: Configurable via `OllamaConfig`

### 3. **ToolsService**
- **Purpose**: MCP server integration and tool management
- **Key Features**:
  - Dynamic MCP server registration
  - Tool discovery and specification
  - SSE transport management
  - Connection pooling and lifecycle management

### 4. **OllamaConfig**
- **Purpose**: Centralized configuration management
- **Configurable Parameters**:
  - Base URL, Model name, Timeout duration
  - Retry count, Temperature settings
  - System property overrides

---

## 🔄 Data Flow

### High-Level Data Flow
```
User Input → CLI → ChatService → LLM → Tool Selection → MCP Server → Tool Execution → Response → User
```

### Detailed Flow Diagram
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│    USER     │    │     CLI     │    │ ChatService │    │   Ollama    │
│             │    │             │    │             │    │    LLM      │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
        │                   │                   │                   │
        │ 1. User Input     │                   │                   │
        │ ─────────────────▶│                   │                   │
        │                   │ 2. Route to Chat  │                   │
        │                   │ ─────────────────▶│                   │
        │                   │                   │ 3. Process Query  │
        │                   │                   │ ─────────────────▶│
        │                   │                   │                   │
        │                   │                   │ 4. LLM Decision   │
        │                   │                   │ ◀─────────────────│
        │                   │                   │                   │
        │                   │                   ▼                   │
        │                   │         ┌─────────────┐               │
        │                   │         │ ToolsService│               │
        │                   │         │             │               │
        │                   │         └─────────────┘               │
        │                   │                   │                   │
        │                   │                   │ 5. Tool Discovery │
        │                   │                   │ ─────────────────▶│
        │                   │                   │                   │
        │                   │                   ▼                   │
        │                   │         ┌─────────────┐               │
        │                   │         │ MCP Server  │               │
        │                   │         │             │               │
        │                   │         └─────────────┘               │
        │                   │                   │                   │
        │                   │                   │ 6. Tool Execution │
        │                   │                   │ ─────────────────▶│
        │                   │                   │                   │
        │                   │                   │ 7. Tool Result    │
        │                   │                   │ ◀─────────────────│
        │                   │                   │                   │
        │                   │                   │ 8. Final Response │
        │                   │                   │ ─────────────────▶│
        │                   │                   │                   │
        │                   │                   │ 9. LLM Response   │
        │                   │                   │ ◀─────────────────│
        │                   │                   │                   │
        │                   │10. Format Response│                   │
        │                   │ ◀─────────────────│                   │
        │                   │                   │                   │
        │11. Display Result │                   │                   │
        │ ◀─────────────────│                   │                   │
        │                   │                   │                   │
```

---

## 🧠 Role of LLM (Ollama)

### **Primary Functions**
1. **Natural Language Understanding**: Parse user queries and intents
2. **Tool Selection**: Decide which MCP tools to use based on context
3. **Response Generation**: Create human-readable responses
4. **Conversation Management**: Maintain context across interactions

### **LLM Integration Details**
```java
// LLM Model Configuration
OllamaChatModel chatModel = OllamaChatModel.builder()
    .baseUrl("http://localhost:11434")     // Local Ollama server
    .modelName("llama3.2")                 // Model selection
    .timeout(Duration.ofMinutes(10))       // Timeout handling
    .maxRetries(3)                         // Retry mechanism
    .temperature(0.7)                      // Response creativity
    .build();
```

### **LLM Decision Process**
1. **Input Analysis**: Understand user intent
2. **Context Evaluation**: Review conversation history
3. **Tool Assessment**: Evaluate available MCP tools
4. **Action Planning**: Decide on tool usage sequence
5. **Response Synthesis**: Generate final response

---

## 🔗 Component Interactions

### **Initialization Flow**
```
McpClientApplication
├── Load Configuration
├── Initialize ChatService
│   ├── Configure Ollama Model
│   ├── Setup Memory Management
│   └── Initialize ToolsService
│       ├── Load mcp.json
│       ├── Register MCP Servers
│       └── Create Tool Provider
└── Start Command Processing
```

### **Chat Session Flow**
```
User Input → ChatService.startInteractiveChat()
├── Validate Input
├── Bot.chat(userId, message)
│   ├── LLM Processing
│   ├── Tool Selection (if needed)
│   ├── MCP Server Communication
│   └── Response Generation
└── Display Response
```

### **Tool Discovery Flow**
```
ToolsCommand.run()
├── Initialize ToolsService
├── Load MCP Configuration
├── Connect to MCP Servers
├── Retrieve Tool Specifications
└── Display Available Tools
```

---

## 📊 Performance Considerations

### **Timeout Management**
- **Connection Timeout**: 60 seconds for SSE connections
- **Request Timeout**: 10 minutes for LLM responses
- **Retry Mechanism**: Up to 3 attempts with exponential backoff

### **Memory Management**
- **Chat History**: 20-message sliding window
- **Connection Pooling**: Reuse MCP client connections
- **Resource Cleanup**: Proper shutdown of all services

### **Error Handling**
- **Graceful Degradation**: Continue operation if some tools fail
- **User Feedback**: Clear error messages and troubleshooting tips
- **Logging**: Comprehensive logging for debugging

---

## 🚀 Getting Started

1. **Prerequisites**: Ollama server running with llama3.2 model
2. **MCP Server**: Configure MCP servers in `mcp.json`
3. **Build**: `mvn clean package`
4. **Run**: `java -jar target/cli-mcp-client.jar chat`

For detailed setup and configuration, see:
- [Data Flow Documentation](DATA_FLOW.md)
- [Component Details](COMPONENTS.md)
- [Configuration Guide](CONFIGURATION.md)
- [Troubleshooting Guide](TROUBLESHOOTING.md)
