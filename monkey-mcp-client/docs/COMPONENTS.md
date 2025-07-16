# MCP Java Client - Component Details

## 🧩 Component Breakdown

This document provides detailed information about each component in the MCP Java Client system, including their responsibilities, interfaces, and implementation details.

---

## 📋 Component Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    COMPONENT HIERARCHY                     │
├─────────────────────────────────────────────────────────────┤
│ McpClientApplication                                        │
│ ├── Commands                                                │
│ │   ├── ChatCommand                                         │
│ │   └── ToolsCommand                                        │
│ ├── Services                                                │
│ │   ├── ChatService                                         │
│ │   └── ToolsService                                        │
│ ├── Configuration                                           │
│ │   └── OllamaConfig                                        │
│ └── External Integrations                                   │
│     ├── Ollama LLM                                          │
│     └── MCP Servers                                         │
└─────────────────────────────────────────────────────────────┘
```

---

## 🏛️ Application Layer

### **McpClientApplication**

**Purpose**: Main application entry point and command orchestration

**Key Responsibilities**:
- CLI argument parsing and routing
- Application lifecycle management
- Logging configuration
- Command execution coordination

**Implementation Details**:
```java
@Command(name = "mcp-client", 
         mixinStandardHelpOptions = true, 
         version = "1.0.0", 
         description = "Java based console app to chat with AI+MCP servers using Ollama Local llama3.2 model", 
         subcommands = {ChatCommand.class, ToolsCommand.class })
public class McpClientApplication {
    
    // Main entry point
    public static void main(String[] args) {
        configureLogging();
        int exitCode = new CommandLine(new McpClientApplication()).execute(args);
        System.exit(exitCode);
    }
}
```

**Dependencies**:
- PicoCLI for command line processing
- SLF4J for logging
- Java Logging API

**Configuration**:
- `logging.properties` for log configuration
- Command line arguments for runtime behavior

---

## 🎯 Command Layer

### **ChatCommand**

**Purpose**: Handle interactive chat sessions

**Key Responsibilities**:
- Initialize chat service
- Validate service availability
- Start interactive chat loop
- Handle user input/output

**Implementation Details**:
```java
@Command(name = "chat", description = "Start a chat session")
public class ChatCommand implements Runnable {
    
    ChatService chatService = new ChatService();
    
    @Override
    public void run() {
        if (chatService.isAvailable()) {
            chatService.startInteractiveChat();
        } else {
            System.err.println("✗ Chat service is not available.");
        }
    }
}
```

**Interface**:
- Input: User console input
- Output: Formatted chat responses
- Error Handling: Service availability checks

### **ToolsCommand**

**Purpose**: Display available MCP tools

**Key Responsibilities**:
- Initialize tools service
- Retrieve tool specifications
- Format and display tool information
- Handle tool discovery errors

**Implementation Details**:
```java
@Command(name = "tools", description = "List available MCP tools from registered servers")
public class ToolsCommand implements Runnable {
    
    ToolsService toolsService = new ToolsService();
    
    @Override
    public void run() {
        var tools = toolsService.getAvailableTools();
        
        // Format and display tools
        Consumer<ToolSpecification> printFunction = (ts) -> {
            out.println("Tool: " + ts.name());
            if (ts.description() != null && !ts.description().isEmpty()) {
                out.println("  Description: " + ts.description());
            }
            if (ts.parameters() != null) {
                out.println("  Parameters: " + ts.parameters().toString());
            }
        };
        
        tools.stream().forEach(printFunction);
        toolsService.shutdown();
    }
}
```

**Output Format**:
```
═════ MCP Tools ═════

Tool: get_monkey_species
  Description: Retrieves information about monkey species
  Parameters: {}

Tool: get_random_monkey
  Description: Returns information about a random monkey
  Parameters: {}

───────────────────────────────────
Total: 2 tools available
```

---

## 🔧 Service Layer

### **ChatService**

**Purpose**: Core chat orchestration and LLM integration

**Key Responsibilities**:
- LLM model configuration and management
- Conversation memory management
- Tool integration coordination
- Error handling and recovery
- User interaction management

**Architecture**:
```java
public class ChatService {
    
    // Core Components
    private OllamaChatModel chatModel;
    private Bot bot;
    private ToolsService toolsService;
    
    // Inner Interface
    interface Bot {
        @SystemMessage("You are a helpful AI assistant...")
        String chat(@MemoryId String memoryId, @UserMessage String message);
        String chat(List<ChatMessage> messages);
    }
}
```

**Configuration Flow**:
1. **Model Setup**: Configure Ollama connection
2. **Memory Setup**: Initialize chat memory store
3. **Tool Integration**: Connect to MCP tools
4. **Service Building**: Create AI services bot
5. **Health Check**: Validate system readiness

**Memory Management**:
```java
// 20-message sliding window
MessageWindowChatMemory.builder()
    .maxMessages(20)
    .chatMemoryStore(chatMemoryStore)
    .id(memoryId)
    .build()
```

**Error Handling**:
- Connection timeouts with retry logic
- Tool execution failures
- Memory overflow protection
- Graceful degradation

### **ToolsService**

**Purpose**: MCP server integration and tool management

**Key Responsibilities**:
- MCP server discovery and registration
- Tool specification retrieval
- Connection lifecycle management
- Configuration parsing
- Error handling and recovery

**MCP Server Registration Process**:
```java
private void registerMCPServers() {
    // 1. Load mcp.json configuration
    var inputStream = getConfigurationFile();
    
    // 2. Parse server configurations
    ObjectMapper objectMapper = new ObjectMapper();
    var config = objectMapper.readTree(inputStream);
    var servers = config.get("servers");
    
    // 3. Register each server
    servers.fieldNames().forEachRemaining(serverName -> {
        registerServer(serverName, servers.get(serverName));
    });
}
```

**Transport Configuration**:
```java
// SSE Transport with enhanced timeouts
var mcpTransport = new HttpMcpTransport.Builder()
    .sseUrl(url)
    .timeout(Duration.ofSeconds(60))
    .logRequests(false)
    .logResponses(false)
    .build();
```

**Tool Provider Integration**:
```java
toolProvider = McpToolProvider.builder()
    .mcpClients(mcpClients.toArray(new McpClient[mcpClients.size()]))
    .build();
```

---

## ⚙️ Configuration Layer

### **OllamaConfig**

**Purpose**: Centralized configuration management

**Key Responsibilities**:
- Default value management
- System property override handling
- Type conversion and validation
- Configuration parameter documentation

**Configuration Parameters**:
```java
public class OllamaConfig {
    
    // Default Values
    public static final String DEFAULT_BASE_URL = "http://localhost:11434";
    public static final String DEFAULT_MODEL = "llama3.2";
    public static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(10);
    public static final int DEFAULT_MAX_RETRIES = 3;
    public static final double DEFAULT_TEMPERATURE = 0.7;
    
    // System Property Overrides
    public static String getBaseUrl() {
        return System.getProperty("ollama.baseUrl", DEFAULT_BASE_URL);
    }
    
    public static String getModelName() {
        return System.getProperty("ollama.model", DEFAULT_MODEL);
    }
    
    // ... other getters with validation
}
```

**Usage Examples**:
```bash
# Override model
java -Dollama.model=codellama -jar app.jar

# Override timeout
java -Dollama.timeout=1800 -jar app.jar

# Override multiple settings
java -Dollama.baseUrl=http://remote:11434 -Dollama.model=llama2 -jar app.jar
```

---

## 🔌 Integration Layer

### **Ollama Integration**

**Purpose**: Local LLM model integration

**Components**:
- **OllamaChatModel**: LangChain4J Ollama adapter
- **HTTP Client**: Communication with Ollama server
- **Timeout Management**: Request/response handling
- **Retry Logic**: Error recovery

**Model Configuration**:
```java
chatModel = OllamaChatModel.builder()
    .baseUrl(OllamaConfig.getBaseUrl())
    .modelName(OllamaConfig.getModelName())
    .timeout(OllamaConfig.getTimeout())
    .maxRetries(OllamaConfig.getMaxRetries())
    .temperature(OllamaConfig.getTemperature())
    .build();
```

**Communication Protocol**:
- **Endpoint**: `http://localhost:11434/api/chat`
- **Method**: HTTP POST
- **Format**: JSON requests/responses
- **Streaming**: Optional streaming responses

### **MCP Server Integration**

**Purpose**: External MCP server connectivity

**Components**:
- **MCP Clients**: Server connection management
- **HTTP Transport**: SSE-based communication
- **Tool Provider**: Tool specification management
- **Protocol Handler**: MCP protocol implementation

**Server Configuration**:
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

**Communication Flow**:
1. **Connection**: Establish SSE connection
2. **Handshake**: Exchange capabilities
3. **Tool Discovery**: Retrieve tool specifications
4. **Tool Execution**: Execute tool calls
5. **Response Handling**: Process tool results

---

## 📊 Component Metrics

### **Performance Characteristics**

| Component | Initialization Time | Memory Usage | Network Calls |
|-----------|-------------------|--------------|---------------|
| McpClientApplication | < 1s | Low | 0 |
| ChatService | 2-5s | Medium | 1 (health check) |
| ToolsService | 1-3s | Low | N (per MCP server) |
| OllamaConfig | < 100ms | Minimal | 0 |

### **Error Scenarios**

| Component | Common Errors | Recovery Strategy |
|-----------|---------------|------------------|
| ChatService | LLM timeout | Retry with exponential backoff |
| ToolsService | MCP server unavailable | Skip server, continue with others |
| OllamaConfig | Invalid property values | Use defaults, log warning |
| Commands | User input errors | Validate and prompt for correction |

---

## 🔄 Component Lifecycle

### **Initialization Order**
1. **Application**: Load configuration, setup logging
2. **Commands**: Parse arguments, validate inputs
3. **Services**: Initialize core services
4. **Integrations**: Connect to external systems
5. **Health Checks**: Validate system readiness

### **Shutdown Sequence**
1. **User Sessions**: Complete active interactions
2. **Services**: Cleanup resources
3. **Connections**: Close MCP clients
4. **Integrations**: Disconnect from external systems
5. **Application**: Exit gracefully

---

## 🛠️ Extension Points

### **Adding New Commands**
1. Create command class implementing `Runnable`
2. Add `@Command` annotation
3. Register in `McpClientApplication.subcommands`
4. Implement business logic

### **Adding New MCP Servers**
1. Update `mcp.json` configuration
2. Ensure server supports required transport
3. Test connectivity and tool discovery
4. Document available tools

### **Customizing LLM Integration**
1. Extend `OllamaConfig` for new parameters
2. Modify `ChatService` model configuration
3. Update timeout and retry logic
4. Test with different model types

---

This component documentation provides a comprehensive view of the system architecture. For data flow details, see [Data Flow Documentation](DATA_FLOW.md), and for configuration options, see [Configuration Guide](CONFIGURATION.md).
