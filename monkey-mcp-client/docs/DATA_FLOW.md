# MCP Java Client - Data Flow Documentation

## 📊 Data Flow Overview

This document provides a detailed explanation of how data flows through the MCP Java Client system, from user input to final response delivery.

---

## 🔄 Complete Data Flow Sequence

### **Phase 1: Application Initialization**

```
┌─────────────────────────────────────────────────────────────┐
│                 1. APPLICATION STARTUP                     │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ McpClientApplication.main()                                │
│ ├── Configure Logging                                      │
│ ├── Parse CLI Arguments                                     │
│ └── Route to Command (ChatCommand/ToolsCommand)           │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ ChatCommand.run()                                          │
│ ├── new ChatService()                                      │
│ ├── Check isAvailable()                                    │
│ └── startInteractiveChat()                                │
└─────────────────────────────────────────────────────────────┘
```

### **Phase 2: Service Initialization**

```
┌─────────────────────────────────────────────────────────────┐
│                 2. CHATSERVICE INIT                        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ ChatService Constructor                                     │
│ ├── 1. Configure OllamaChatModel                          │
│ │   ├── baseUrl: http://localhost:11434                  │
│ │   ├── modelName: llama3.2                              │
│ │   ├── timeout: 10 minutes                              │
│ │   └── maxRetries: 3                                    │
│ ├── 2. Initialize ToolsService                            │
│ │   ├── Load mcp.json configuration                      │
│ │   ├── Register MCP servers                             │
│ │   └── Create tool provider                             │
│ ├── 3. Setup Memory Management                            │
│ │   ├── InMemoryChatMemoryStore                          │
│ │   └── MessageWindowChatMemory (20 messages)           │
│ └── 4. Create AiServices Bot                              │
│     ├── Configure chat model                              │
│     ├── Attach tool provider                              │
│     └── Setup memory provider                             │
└─────────────────────────────────────────────────────────────┘
```

### **Phase 3: MCP Server Registration**

```
┌─────────────────────────────────────────────────────────────┐
│                 3. MCP SERVER SETUP                        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ ToolsService.registerMCPServers()                          │
│ ├── 1. Load mcp.json                                       │
│ │   ├── Check working directory                           │
│ │   └── Fallback to bundled resource                      │
│ ├── 2. Parse Server Configuration                          │
│ │   ├── Server name: "monkeymcp"                          │
│ │   ├── Type: "sse"                                       │
│ │   └── URL: "http://localhost:8080/mcp/sse"             │
│ ├── 3. Create HTTP MCP Transport                           │
│ │   ├── SSE URL configuration                             │
│ │   ├── Timeout: 60 seconds                               │
│ │   └── Logging: disabled                                 │
│ ├── 4. Create DefaultMcpClient                             │
│ │   ├── Key: server name                                  │
│ │   └── Transport: HTTP SSE                               │
│ └── 5. Register client in mcpClients list                 │
└─────────────────────────────────────────────────────────────┘
```

### **Phase 4: Interactive Chat Session**

```
┌─────────────────────────────────────────────────────────────┐
│                 4. CHAT SESSION LOOP                       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ startInteractiveChat()                                      │
│ ├── 1. Display Welcome Message                             │
│ ├── 2. Enter Input Loop                                     │
│ │   ├── Read user input                                    │
│ │   ├── Check for exit commands                           │
│ │   └── Process message                                    │
│ └── 3. Handle Errors and Retry                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔍 Detailed Message Processing Flow

### **Step 1: User Input Processing**

```
User Types: "What monkey species do you know?"
                    │
                    ▼
┌─────────────────────────────────────────────────────────────┐
│ ChatService.startInteractiveChat()                         │
│ ├── scanner.nextLine()                                     │
│ ├── input.trim()                                           │
│ ├── Check for exit commands                                │
│ └── Call bot.chat(RANDOM_USER, input)                     │
└─────────────────────────────────────────────────────────────┘
```

### **Step 2: AI Services Processing**

```
┌─────────────────────────────────────────────────────────────┐
│ AiServices Bot Interface                                    │
│ ├── @SystemMessage: "You are a helpful AI assistant..."    │
│ ├── @MemoryId: "user-12345678"                            │
│ ├── @UserMessage: "What monkey species do you know?"      │
│ └── Route to LLM with context                              │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ Memory Management                                           │
│ ├── Retrieve conversation history (last 20 messages)      │
│ ├── Add current message to context                         │
│ └── Prepare full context for LLM                           │
└─────────────────────────────────────────────────────────────┘
```

### **Step 3: LLM Processing**

```
┌─────────────────────────────────────────────────────────────┐
│ OllamaChatModel.doChat()                                   │
│ ├── HTTP Request to http://localhost:11434/api/chat       │
│ ├── Payload:                                               │
│ │   ├── model: "llama3.2"                                 │
│ │   ├── messages: [system, history, user]                │
│ │   └── temperature: 0.7                                  │
│ ├── LLM Analysis:                                          │
│ │   ├── Understand intent: "Query about monkey species"   │
│ │   ├── Check available tools                             │
│ │   └── Decide on tool usage                              │
│ └── Generate response or tool calls                        │
└─────────────────────────────────────────────────────────────┘
```

### **Step 4: Tool Selection and Execution**

```
┌─────────────────────────────────────────────────────────────┐
│ LLM Decision: Use MCP Tool                                 │
│ ├── Tool: "get_monkey_species"                            │
│ ├── Parameters: {}                                         │
│ └── Route to McpToolProvider                               │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ McpToolProvider.executeTools()                             │
│ ├── Find matching tool specification                       │
│ ├── Validate parameters                                     │
│ ├── Route to appropriate MCP client                        │
│ └── Execute tool call                                       │
└─────────────────────────────────────────────────────────────┘
```

### **Step 5: MCP Server Communication**

```
┌─────────────────────────────────────────────────────────────┐
│ MCP Client Communication                                    │
│ ├── HTTP SSE Request to: http://localhost:8080/mcp/sse    │
│ ├── Tool Call Message:                                     │
│ │   ├── method: "tools/call"                              │
│ │   ├── tool_name: "get_monkey_species"                   │
│ │   └── arguments: {}                                     │
│ ├── Wait for SSE Response                                  │
│ └── Parse tool result                                       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ MCP Server Response                                         │
│ ├── Status: success                                        │
│ ├── Content:                                               │
│ │   ├── "Rhesus Macaque"                                  │
│ │   ├── "Chimpanzee"                                      │
│ │   └── "Baboon"                                          │
│ └── Return to LLM for processing                           │
└─────────────────────────────────────────────────────────────┘
```

### **Step 6: Response Generation**

```
┌─────────────────────────────────────────────────────────────┐
│ LLM Response Generation                                     │
│ ├── Incorporate tool results                               │
│ ├── Generate natural language response                     │
│ ├── Format output                                          │
│ └── Return final response                                   │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ Final Response                                              │
│ "Here are some monkey species I know about:                │
│ - Rhesus Macaque: Commonly found in Asia...               │
│ - Chimpanzee: Our closest living relatives...             │
│ - Baboon: Social primates found in Africa..."             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔢 Data Flow Metrics

### **Timing Breakdown**
```
┌─────────────────────────────────────────────────────────────┐
│ Phase                    │ Typical Duration                 │
├─────────────────────────────────────────────────────────────┤
│ Application Startup      │ 1-2 seconds                     │
│ Service Initialization   │ 2-5 seconds                     │
│ MCP Server Registration  │ 1-3 seconds                     │
│ LLM Processing          │ 5-30 seconds                     │
│ Tool Execution          │ 1-10 seconds                     │
│ Response Generation     │ 2-10 seconds                     │
└─────────────────────────────────────────────────────────────┘
```

### **Error Handling Points**
1. **Connection Failures**: MCP server unavailable
2. **Timeout Scenarios**: LLM response timeout
3. **Tool Failures**: MCP tool execution errors
4. **Memory Issues**: Chat history management
5. **Configuration Errors**: Invalid mcp.json

---

## 🛠️ Data Transformation

### **Input Transformation**
```
Raw User Input → Trimmed String → Message Object → LLM Context
```

### **Response Transformation**
```
Tool Results → LLM Context → Generated Text → Formatted Output
```

### **Memory Transformation**
```
Chat Messages → Memory Store → Context Window → LLM Input
```

---

## 📋 Configuration Impact on Data Flow

### **Timeout Configuration**
- **Connection Timeout**: Affects MCP server handshake
- **Request Timeout**: Impacts LLM response time
- **Retry Logic**: Influences error recovery

### **Memory Configuration**
- **Window Size**: Affects context length
- **Memory Store**: Impacts persistence
- **Message Filtering**: Influences context quality

### **Tool Configuration**
- **Server URLs**: Determines available tools
- **Transport Type**: Affects communication method
- **Tool Specifications**: Influences tool selection

---

## 🔄 Asynchronous Processing

### **SSE Connection Management**
```
┌─────────────────────────────────────────────────────────────┐
│ SSE Connection Lifecycle                                    │
│ ├── 1. Initial Connection                                   │
│ ├── 2. Keep-alive Heartbeat                                │
│ ├── 3. Message Streaming                                    │
│ ├── 4. Error Recovery                                       │
│ └── 5. Graceful Shutdown                                    │
└─────────────────────────────────────────────────────────────┘
```

### **Concurrent Processing**
- **Multiple MCP Clients**: Parallel tool discovery
- **Async Tool Execution**: Non-blocking tool calls
- **Background Health Checks**: Connection monitoring

---

## 🚀 Performance Optimization

### **Connection Pooling**
- Reuse MCP client connections
- Efficient resource management
- Reduced latency

### **Caching Strategy**
- Tool specification caching
- Memory-based response caching
- Configuration caching

### **Error Recovery**
- Exponential backoff
- Circuit breaker pattern
- Graceful degradation

---

This data flow documentation provides a comprehensive view of how information moves through the MCP Java Client system. For implementation details, see the [Architecture Documentation](ARCHITECTURE.md) and [Component Details](COMPONENTS.md).
