# MCP Java Client - Visual Data Flow Diagram

## 🎨 Visual Data Flow Representation

This document provides visual representations of how data flows through the MCP Java Client system.

---

## 🔄 Complete System Data Flow

```
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                           MCP JAVA CLIENT SYSTEM                                                   │
│                                                                                                                     │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐  │
│  │    USER     │    │     CLI     │    │ ChatService │    │   Ollama    │    │ToolsService │    │ MCP Server  │  │
│  │  Interface  │    │  Commands   │    │   (Core)    │    │    LLM      │    │   (MCP)     │    │   (Tools)   │  │
│  └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘  │
│         │                   │                   │                   │                   │                   │     │
│         │                   │                   │                   │                   │                   │     │
│    ┌────▼────┐         ┌────▼────┐         ┌────▼────┐         ┌────▼────┐         ┌────▼────┐         ┌────▼────┐ │
│    │  Input  │         │ Command │         │  Chat   │         │   LLM   │         │  Tool   │         │  Tool   │ │
│    │Processing│         │Routing  │         │Processing│         │Analysis │         │Discovery│         │Execution│ │
│    └────┬────┘         └────┬────┘         └────┬────┘         └────┬────┘         └────┬────┘         └────┬────┘ │
│         │                   │                   │                   │                   │                   │     │
│         │                   │                   │                   │                   │                   │     │
│    ┌────▼────┐         ┌────▼────┐         ┌────▼────┐         ┌────▼────┐         ┌────▼────┐         ┌────▼────┐ │
│    │Response │         │ Service │         │ Memory  │         │  Tool   │         │  Tool   │         │ Result  │ │
│    │Display  │         │Dispatch │         │Management│         │Selection│         │Provider │         │Processing│ │
│    └─────────┘         └─────────┘         └─────────┘         └─────────┘         └─────────┘         └─────────┘ │
│                                                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 🎯 Detailed Request-Response Flow

```
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                        REQUEST-RESPONSE CYCLE                                                      │
│                                                                                                                     │
│  USER INPUT: "What monkey species do you know?"                                                                    │
│      │                                                                                                             │
│      ▼                                                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                                    PHASE 1: INPUT PROCESSING                                                  │ │
│  │                                                                                                               │ │
│  │  ChatCommand.run() → ChatService.startInteractiveChat() → scanner.nextLine()                                │ │
│  │                                          │                                                                   │ │
│  │                                          ▼                                                                   │ │
│  │  Input Validation → Trim → Check Exit Commands → Route to AI Services                                       │ │
│  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
│                                         │                                                                         │
│                                         ▼                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                                   PHASE 2: AI PROCESSING                                                     │ │
│  │                                                                                                               │ │
│  │  bot.chat(userId, message) → AiServices → OllamaChatModel                                                    │ │
│  │                                          │                                                                   │ │
│  │                                          ▼                                                                   │ │
│  │  ┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐                                           │ │
│  │  │ Memory Retrieval│   │ Context Building│   │ LLM Inference   │                                           │ │
│  │  │ (Last 20 msgs)  │   │ (System + User) │   │ (Ollama Server) │                                           │ │
│  │  └─────────────────┘   └─────────────────┘   └─────────────────┘                                           │ │
│  │                                          │                                                                   │ │
│  │                                          ▼                                                                   │ │
│  │  LLM Decision: "User wants monkey species information - use get_monkey_species tool"                        │ │
│  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
│                                         │                                                                         │
│                                         ▼                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                                  PHASE 3: TOOL EXECUTION                                                     │ │
│  │                                                                                                               │ │
│  │  McpToolProvider.executeTool() → McpClient.callTool() → HTTP SSE Request                                     │ │
│  │                                          │                                                                   │ │
│  │                                          ▼                                                                   │ │
│  │  ┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐                                           │ │
│  │  │ Tool Selection  │   │ Parameter Prep  │   │ MCP Server Call │                                           │ │
│  │  │get_monkey_species│   │     params: {}  │   │ SSE Transport   │                                           │ │
│  │  └─────────────────┘   └─────────────────┘   └─────────────────┘                                           │ │
│  │                                          │                                                                   │ │
│  │                                          ▼                                                                   │ │
│  │  MCP Server Response: ["Rhesus Macaque", "Chimpanzee", "Baboon", "Orangutan"]                               │ │
│  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
│                                         │                                                                         │
│                                         ▼                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                                 PHASE 4: RESPONSE SYNTHESIS                                                  │ │
│  │                                                                                                               │ │
│  │  LLM Response Generation → Context Integration → Natural Language Output                                     │ │
│  │                                          │                                                                   │ │
│  │                                          ▼                                                                   │ │
│  │  ┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐                                           │ │
│  │  │ Tool Results    │   │ Context Merge   │   │ Response Format │                                           │ │
│  │  │ Integration     │   │ (User + Tools)  │   │ (Natural Lang)  │                                           │ │
│  │  └─────────────────┘   └─────────────────┘   └─────────────────┘                                           │ │
│  │                                          │                                                                   │ │
│  │                                          ▼                                                                   │ │
│  │  Final Response: "I can tell you about several monkey species..."                                            │ │
│  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
│                                         │                                                                         │
│                                         ▼                                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                                  PHASE 5: OUTPUT DELIVERY                                                    │ │
│  │                                                                                                               │ │
│  │  Response Formatting → Console Output → Memory Storage → Loop Back                                           │ │
│  │                                          │                                                                   │ │
│  │                                          ▼                                                                   │ │
│  │  USER OUTPUT: "I can tell you about several monkey species: Rhesus Macaque..."                              │ │
│  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 🧠 LLM Decision Tree

```
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                         LLM DECISION TREE                                                          │
│                                                                                                                     │
│                                      USER INPUT RECEIVED                                                           │
│                                             │                                                                       │
│                                             ▼                                                                       │
│                                    ┌─────────────────┐                                                             │
│                                    │ INTENT ANALYSIS │                                                             │
│                                    │                 │                                                             │
│                                    │ • Parse Query   │                                                             │
│                                    │ • Extract Intent│                                                             │
│                                    │ • Identify Domain│                                                            │
│                                    └─────────────────┘                                                             │
│                                             │                                                                       │
│                                             ▼                                                                       │
│                              ┌─────────────────────────────────────┐                                               │
│                              │          DECISION POINT             │                                               │
│                              │                                     │                                               │
│                              │    Does query need external        │                                               │
│                              │    information or tools?            │                                               │
│                              └─────────────────────────────────────┘                                               │
│                                      │                    │                                                         │
│                                     YES                  NO                                                         │
│                                      │                    │                                                         │
│                                      ▼                    ▼                                                         │
│                        ┌─────────────────────┐   ┌─────────────────────┐                                          │
│                        │   TOOL SELECTION    │   │  DIRECT RESPONSE    │                                          │
│                        │                     │   │                     │                                          │
│                        │ • Analyze Available │   │ • Use Built-in      │                                          │
│                        │   Tools             │   │   Knowledge         │                                          │
│                        │ • Match to Query    │   │ • Generate Response │                                          │
│                        │ • Rank by Relevance│   │ • Return to User    │                                          │
│                        └─────────────────────┘   └─────────────────────┘                                          │
│                                      │                    │                                                         │
│                                      ▼                    │                                                         │
│                        ┌─────────────────────┐            │                                                         │
│                        │  TOOL EXECUTION     │            │                                                         │
│                        │                     │            │                                                         │
│                        │ • Prepare Parameters│            │                                                         │
│                        │ • Call MCP Tools    │            │                                                         │
│                        │ • Handle Results    │            │                                                         │
│                        │ • Error Recovery    │            │                                                         │
│                        └─────────────────────┘            │                                                         │
│                                      │                    │                                                         │
│                                      ▼                    │                                                         │
│                        ┌─────────────────────┐            │                                                         │
│                        │ RESULT INTEGRATION  │            │                                                         │
│                        │                     │            │                                                         │
│                        │ • Combine Tool Data │            │                                                         │
│                        │ • Add Context       │            │                                                         │
│                        │ • Generate Response │            │                                                         │
│                        │ • Format Output     │            │                                                         │
│                        └─────────────────────┘            │                                                         │
│                                      │                    │                                                         │
│                                      └────────────────────┘                                                         │
│                                             │                                                                       │
│                                             ▼                                                                       │
│                                    ┌─────────────────┐                                                             │
│                                    │ RESPONSE OUTPUT │                                                             │
│                                    │                 │                                                             │
│                                    │ • Format Text   │                                                             │
│                                    │ • Update Memory │                                                             │
│                                    │ • Return to User│                                                             │
│                                    └─────────────────┘                                                             │
│                                                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 Memory Management Flow

```
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                       MEMORY MANAGEMENT                                                            │
│                                                                                                                     │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐                      │
│  │  NEW MESSAGE    │    │  MEMORY STORE   │    │ CONTEXT WINDOW  │    │  LLM CONTEXT    │                      │
│  │   RECEIVED      │    │   RETRIEVAL     │    │   MANAGEMENT    │    │   BUILDING      │                      │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘    └─────────────────┘                      │
│            │                       │                       │                       │                             │
│            ▼                       ▼                       ▼                       ▼                             │
│  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                                    MEMORY OPERATIONS                                                         │ │
│  │                                                                                                               │ │
│  │  1. Input Processing                                                                                         │ │
│  │     └─ User: "What monkey species do you know?"                                                              │ │
│  │                                                                                                               │ │
│  │  2. Memory Retrieval                                                                                         │ │
│  │     └─ InMemoryChatMemoryStore.getMessages(userId, limit=20)                                                 │ │
│  │                                                                                                               │ │
│  │  3. Context Window Building                                                                                  │ │
│  │     ├─ System Message: "You are a helpful AI assistant..."                                                   │ │
│  │     ├─ Previous Messages: [message1, message2, ...]                                                          │ │
│  │     └─ Current Message: "What monkey species do you know?"                                                   │ │
│  │                                                                                                               │ │
│  │  4. LLM Processing                                                                                            │ │
│  │     └─ OllamaChatModel.doChat(contextMessages)                                                               │ │
│  │                                                                                                               │ │
│  │  5. Response Generation                                                                                       │ │
│  │     └─ AI: "I can tell you about several monkey species..."                                                  │ │
│  │                                                                                                               │ │
│  │  6. Memory Update                                                                                             │ │
│  │     ├─ Store User Message                                                                                    │ │
│  │     ├─ Store AI Response                                                                                     │ │
│  │     └─ Update Conversation History                                                                            │ │
│  │                                                                                                               │ │
│  │  7. Window Management                                                                                         │ │
│  │     ├─ Check message count (max 20)                                                                          │ │
│  │     ├─ Remove oldest if needed                                                                               │ │
│  │     └─ Maintain conversation continuity                                                                       │ │
│  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                                    MEMORY STRUCTURE                                                           │ │
│  │                                                                                                               │ │
│  │  User ID: "user-12345678"                                                                                    │ │
│  │  ├─ Message 1: {type: "user", content: "Hello", timestamp: "..."}                                           │ │
│  │  ├─ Message 2: {type: "ai", content: "Hi! How can I help?", timestamp: "..."}                              │ │
│  │  ├─ Message 3: {type: "user", content: "Tell me about monkeys", timestamp: "..."}                          │ │
│  │  ├─ Message 4: {type: "ai", content: "Monkeys are primates...", timestamp: "..."}                          │ │
│  │  └─ ... (up to 20 messages)                                                                                  │ │
│  │                                                                                                               │ │
│  │  Memory Properties:                                                                                           │ │
│  │  ├─ Max Messages: 20                                                                                         │ │
│  │  ├─ Storage Type: In-Memory                                                                                  │ │
│  │  ├─ Eviction Policy: FIFO (First In, First Out)                                                             │ │
│  │  └─ Persistence: Session-based (not persistent across restarts)                                              │ │
│  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 🔧 Tool Integration Flow

```
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                       TOOL INTEGRATION                                                             │
│                                                                                                                     │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐                      │
│  │ TOOL DISCOVERY  │    │ TOOL SELECTION  │    │ TOOL EXECUTION  │    │ RESULT HANDLING │                      │
│  │   (Startup)     │    │   (Runtime)     │    │   (Async)       │    │   (Integration) │                      │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘    └─────────────────┘                      │
│            │                       │                       │                       │                             │
│            ▼                       ▼                       ▼                       ▼                             │
│  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                                   TOOL LIFECYCLE                                                             │ │
│  │                                                                                                               │ │
│  │  PHASE 1: DISCOVERY (Application Startup)                                                                    │ │
│  │  ├─ 1. Load mcp.json configuration                                                                           │ │
│  │  ├─ 2. Connect to MCP servers                                                                                │ │
│  │  │    └─ HTTP SSE: http://localhost:8080/mcp/sse                                                             │ │
│  │  ├─ 3. Retrieve tool specifications                                                                          │ │
│  │  │    ├─ get_monkey_species: "Retrieves monkey species info"                                                 │ │
│  │  │    └─ get_random_monkey: "Returns random monkey information"                                               │ │
│  │  └─ 4. Register tools with McpToolProvider                                                                   │ │
│  │                                                                                                               │ │
│  │  PHASE 2: SELECTION (Query Processing)                                                                       │ │
│  │  ├─ 1. LLM analyzes user query                                                                               │ │
│  │  │    └─ Query: "What monkey species do you know?"                                                           │ │
│  │  ├─ 2. Match query to available tools                                                                        │ │
│  │  │    ├─ get_monkey_species: HIGH relevance                                                                  │ │
│  │  │    └─ get_random_monkey: LOW relevance                                                                    │ │
│  │  ├─ 3. Select best matching tool                                                                             │ │
│  │  │    └─ Selected: get_monkey_species                                                                        │ │
│  │  └─ 4. Prepare tool parameters                                                                               │ │
│  │       └─ Parameters: {} (empty for this tool)                                                                │ │
│  │                                                                                                               │ │
│  │  PHASE 3: EXECUTION (Tool Invocation)                                                                        │ │
│  │  ├─ 1. McpToolProvider.executeTool()                                                                         │ │
│  │  ├─ 2. McpClient.callTool()                                                                                  │ │
│  │  ├─ 3. HTTP SSE Request to MCP server                                                                        │ │
│  │  │    ├─ Method: POST                                                                                        │ │
│  │  │    ├─ URL: http://localhost:8080/mcp/sse                                                                  │ │
│  │  │    └─ Payload: {method: "tools/call", params: {name: "get_monkey_species", arguments: {}}}               │ │
│  │  ├─ 4. MCP Server Processing                                                                                 │ │
│  │  │    ├─ Validate tool request                                                                               │ │
│  │  │    ├─ Execute tool logic                                                                                  │ │
│  │  │    └─ Return results                                                                                      │ │
│  │  └─ 5. Response: ["Rhesus Macaque", "Chimpanzee", "Baboon", "Orangutan"]                                    │ │
│  │                                                                                                               │ │
│  │  PHASE 4: INTEGRATION (Result Processing)                                                                    │ │
│  │  ├─ 1. Receive tool result                                                                                   │ │
│  │  ├─ 2. Validate result format                                                                                │ │
│  │  ├─ 3. Pass result back to LLM                                                                               │ │
│  │  ├─ 4. LLM synthesizes final response                                                                        │ │
│  │  │    └─ "I can tell you about several monkey species: Rhesus Macaque..."                                   │ │
│  │  └─ 5. Return to user                                                                                        │ │
│  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                                    ERROR HANDLING                                                             │ │
│  │                                                                                                               │ │
│  │  Connection Errors:                                                                                           │ │
│  │  ├─ MCP server unavailable → Skip server, continue with others                                               │ │
│  │  ├─ Network timeout → Retry with exponential backoff                                                         │ │
│  │  └─ Invalid response → Log error, use fallback                                                               │ │
│  │                                                                                                               │ │
│  │  Tool Execution Errors:                                                                                       │ │
│  │  ├─ Tool not found → Use LLM built-in knowledge                                                              │ │
│  │  ├─ Invalid parameters → Retry with corrected parameters                                                     │ │
│  │  └─ Execution failure → Graceful degradation                                                                 │ │
│  │                                                                                                               │ │
│  │  Recovery Strategies:                                                                                         │ │
│  │  ├─ Circuit breaker pattern for failing tools                                                                │ │
│  │  ├─ Fallback to built-in LLM knowledge                                                                       │ │
│  │  └─ User notification with troubleshooting tips                                                              │ │
│  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 📊 Performance and Timing Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                    PERFORMANCE TIMELINE                                                            │
│                                                                                                                     │
│  Time: 0s    1s    2s    3s    4s    5s    6s    7s    8s    9s    10s   11s   12s   13s   14s   15s             │
│        │     │     │     │     │     │     │     │     │     │     │     │     │     │     │     │               │
│  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                                   STARTUP PHASE                                                              │ │
│  │  ├─ App Init ──┤                                                                                             │ │
│  │  │             ├─ Ollama Connect ──┤                                                                         │ │
│  │  │             │                   ├─ MCP Discovery ──┤                                                     │ │
│  │  │             │                   │                  ├─ Health Check ──┤                                  │ │
│  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                          │                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                                  QUERY PROCESSING                                                            │ │
│  │                                                       ├─ Input Parse ──┤                                    │ │
│  │                                                       │                ├─ Memory Load ──┤                  │ │
│  │                                                       │                │                ├─ LLM Process ────┤ │
│  │                                                       │                │                │                  │ │
│  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                                              │     │
│  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                                  TOOL EXECUTION                                                              │ │
│  │                                                                                                  ├─Tool Call─┤ │
│  │                                                                                                  │          │ │
│  │                                                                                                  ├─MCP Exec─┤ │
│  │                                                                                                  │          │ │
│  │                                                                                                  ├─Response─┤ │
│  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                                                │   │
│  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                                 RESPONSE GENERATION                                                          │ │
│  │                                                                                                              ├─┤ │
│  │                                                                                                              │ │
│  │                                                                                                     ├─Final─┤ │
│  └─────────────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                                                                                     │
│  Performance Metrics:                                                                                              │
│  ├─ Startup Time: 3-5 seconds                                                                                      │
│  ├─ Query Processing: 1-2 seconds                                                                                  │
│  ├─ LLM Processing: 5-10 seconds                                                                                   │
│  ├─ Tool Execution: 1-3 seconds                                                                                    │
│  └─ Total Response Time: 7-15 seconds                                                                              │
│                                                                                                                     │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

---

This visual documentation provides comprehensive diagrams showing how data flows through the MCP Java Client system. Each diagram illustrates different aspects of the system from user interaction to internal processing and external integrations.

For detailed implementation information, refer to the other documentation files in this suite.
