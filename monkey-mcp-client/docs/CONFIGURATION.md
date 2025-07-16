# MCP Java Client - Configuration Guide

## ⚙️ Configuration Overview

This guide provides comprehensive information about configuring the MCP Java Client for optimal performance and functionality.

---

## 📋 Configuration Files

### **1. mcp.json - MCP Server Configuration**

**Location**: 
- Working directory: `./mcp.json`
- Bundled resource: `src/main/resources/mcp.json`

**Structure**:
```json
{
  "servers": {
    "server-name": {
      "type": "transport-type",
      "url": "server-url",
      "timeout": "optional-timeout",
      "retries": "optional-retries"
    }
  }
}
```

**Example**:
```json
{
  "servers": {
    "monkeymcp": {
      "type": "sse",
      "url": "http://localhost:8080/mcp/sse"
    },
    "filesystemcp": {
      "type": "sse",
      "url": "http://localhost:8081/mcp/sse"
    }
  }
}
```

### **2. logging.properties - Logging Configuration**

**Location**: `src/main/resources/logging.properties`

**Example**:
```properties
# Root logger configuration
.level=INFO
handlers=java.util.logging.ConsoleHandler

# Console handler configuration
java.util.logging.ConsoleHandler.level=INFO
java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter

# Package-specific logging levels
dev.langchain4j.level=WARN
org.acme.level=INFO

# Simple formatter pattern
java.util.logging.SimpleFormatter.format=%1$tF %1$tT %4$s %2$s %5$s%6$s%n
```

---

## 🔧 System Properties Configuration

### **Ollama Configuration**

| Property | Default | Description | Example |
|----------|---------|-------------|---------|
| `ollama.baseUrl` | `http://localhost:11434` | Ollama server URL | `-Dollama.baseUrl=http://remote:11434` |
| `ollama.model` | `llama3.2` | Model name | `-Dollama.model=codellama` |
| `ollama.timeout` | `600` | Timeout in seconds | `-Dollama.timeout=1800` |
| `ollama.maxRetries` | `3` | Maximum retry attempts | `-Dollama.maxRetries=5` |
| `ollama.temperature` | `0.7` | Response creativity | `-Dollama.temperature=0.5` |

### **Usage Examples**:
```bash
# Development environment
java -Dollama.model=llama3.2:8b -Dollama.timeout=300 -jar app.jar chat

# Production environment
java -Dollama.model=llama3.2:70b -Dollama.timeout=1800 -Dollama.maxRetries=5 -jar app.jar chat

# Testing environment
java -Dollama.baseUrl=http://test-server:11434 -Dollama.model=llama2 -jar app.jar chat
```

---

## 🚀 Environment-Specific Configurations

### **Development Environment**

**Characteristics**:
- Faster response times
- Verbose logging
- Smaller models
- Shorter timeouts

**Configuration**:
```bash
# Development startup script
java -Dollama.model=llama3.2:1b \
     -Dollama.timeout=120 \
     -Dollama.temperature=0.8 \
     -Dlogging.level=DEBUG \
     -jar target/cli-mcp-client.jar chat
```

**mcp.json**:
```json
{
  "servers": {
    "local-dev": {
      "type": "sse",
      "url": "http://localhost:8080/mcp/sse"
    }
  }
}
```

### **Production Environment**

**Characteristics**:
- Robust error handling
- Longer timeouts
- Larger models
- Minimal logging

**Configuration**:
```bash
# Production startup script
java -Xmx4g \
     -Dollama.model=llama3.2:70b \
     -Dollama.timeout=1800 \
     -Dollama.maxRetries=3 \
     -Dollama.temperature=0.7 \
     -Dlogging.level=WARN \
     -jar target/cli-mcp-client.jar chat
```

**mcp.json**:
```json
{
  "servers": {
    "production-mcp": {
      "type": "sse",
      "url": "https://mcp-server.company.com/mcp/sse"
    },
    "backup-mcp": {
      "type": "sse",
      "url": "https://backup-mcp.company.com/mcp/sse"
    }
  }
}
```

### **Testing Environment**

**Characteristics**:
- Mock servers
- Fast responses
- Deterministic behavior
- Comprehensive logging

**Configuration**:
```bash
# Testing startup script
java -Dollama.model=llama3.2:1b \
     -Dollama.timeout=60 \
     -Dollama.temperature=0.1 \
     -Dlogging.level=INFO \
     -jar target/cli-mcp-client.jar chat
```

**mcp.json**:
```json
{
  "servers": {
    "mock-server": {
      "type": "sse",
      "url": "http://localhost:9999/mcp/sse"
    }
  }
}
```

---

## 🔄 Dynamic Configuration

### **Configuration Reload**

The application currently requires restart for configuration changes. Future versions may support:
- Hot reload of mcp.json
- Runtime property updates
- Configuration management APIs

### **Configuration Validation**

**Startup Validation**:
```java
public class ConfigurationValidator {
    
    public static void validateOllamaConfig() {
        // Validate URL format
        String baseUrl = OllamaConfig.getBaseUrl();
        if (!isValidUrl(baseUrl)) {
            throw new IllegalArgumentException("Invalid Ollama URL: " + baseUrl);
        }
        
        // Validate timeout range
        Duration timeout = OllamaConfig.getTimeout();
        if (timeout.isNegative() || timeout.toSeconds() > 3600) {
            throw new IllegalArgumentException("Timeout must be between 0 and 3600 seconds");
        }
        
        // Validate model name
        String model = OllamaConfig.getModelName();
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Model name cannot be empty");
        }
    }
}
```

---

## 🛠️ Advanced Configuration

### **Custom HTTP Client Configuration**

For advanced use cases, you can configure custom HTTP client settings:

```java
// Custom HTTP client with proxy support
HttpClient httpClient = HttpClient.newBuilder()
    .connectTimeout(Duration.ofSeconds(30))
    .proxy(ProxySelector.of(new InetSocketAddress("proxy.company.com", 8080)))
    .authenticator(new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication("username", "password".toCharArray());
        }
    })
    .build();

// Configure Ollama model with custom client
OllamaChatModel chatModel = OllamaChatModel.builder()
    .baseUrl("http://localhost:11434")
    .modelName("llama3.2")
    .timeout(Duration.ofMinutes(10))
    .httpClient(httpClient)
    .build();
```

### **SSL/TLS Configuration**

For secure connections to MCP servers:

```java
// SSL context configuration
SSLContext sslContext = SSLContext.getInstance("TLS");
sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

// Custom HTTP transport with SSL
HttpMcpTransport transport = new HttpMcpTransport.Builder()
    .sseUrl("https://secure-mcp.company.com/mcp/sse")
    .sslContext(sslContext)
    .timeout(Duration.ofSeconds(60))
    .build();
```

---

## 📊 Performance Tuning

### **Memory Configuration**

**JVM Memory Settings**:
```bash
# For large models
java -Xmx8g -Xms2g -jar app.jar

# For development
java -Xmx2g -Xms512m -jar app.jar

# For production servers
java -Xmx16g -Xms4g -XX:+UseG1GC -jar app.jar
```

**Chat Memory Configuration**:
```java
// Adjust message window size
MessageWindowChatMemory.builder()
    .maxMessages(50)  // Increase for longer conversations
    .chatMemoryStore(chatMemoryStore)
    .build();
```

### **Timeout Optimization**

**Model-Specific Timeouts**:
```bash
# Small models (1B-7B parameters)
java -Dollama.timeout=120 -jar app.jar

# Medium models (13B-30B parameters)
java -Dollama.timeout=300 -jar app.jar

# Large models (70B+ parameters)
java -Dollama.timeout=1800 -jar app.jar
```

**Connection Timeout Tuning**:
```java
// MCP transport timeout configuration
HttpMcpTransport transport = new HttpMcpTransport.Builder()
    .sseUrl(url)
    .timeout(Duration.ofSeconds(30))    // Quick timeout for tool calls
    .keepAliveTimeout(Duration.ofMinutes(5))  // Keep connection alive
    .build();
```

---

## 🔍 Monitoring and Observability

### **Logging Configuration**

**Detailed Logging**:
```properties
# Enable detailed logging
.level=DEBUG
dev.langchain4j.level=DEBUG
org.acme.level=DEBUG

# Log to file
handlers=java.util.logging.FileHandler,java.util.logging.ConsoleHandler
java.util.logging.FileHandler.pattern=logs/mcp-client-%u.log
java.util.logging.FileHandler.limit=10485760
java.util.logging.FileHandler.count=10
```

**Structured Logging**:
```java
// Custom logging format
public class StructuredFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        return String.format("{\"timestamp\":\"%s\",\"level\":\"%s\",\"message\":\"%s\",\"logger\":\"%s\"}%n",
            Instant.ofEpochMilli(record.getMillis()),
            record.getLevel(),
            record.getMessage(),
            record.getLoggerName());
    }
}
```

### **Health Check Configuration**

**Service Health Monitoring**:
```java
public class HealthChecker {
    
    public HealthStatus checkOllamaHealth() {
        try {
            // Quick health check
            OllamaChatModel testModel = OllamaChatModel.builder()
                .baseUrl(OllamaConfig.getBaseUrl())
                .modelName(OllamaConfig.getModelName())
                .timeout(Duration.ofSeconds(5))
                .build();
            
            testModel.generate("test");
            return HealthStatus.HEALTHY;
        } catch (Exception e) {
            return HealthStatus.UNHEALTHY;
        }
    }
    
    public HealthStatus checkMcpServerHealth(String serverUrl) {
        // MCP server health check implementation
        return HealthStatus.HEALTHY;
    }
}
```

---

## 🚨 Troubleshooting Configuration

### **Common Configuration Issues**

**1. Ollama Connection Issues**
```bash
# Check Ollama server status
curl http://localhost:11434/api/tags

# Verify model availability
ollama list

# Test with different timeout
java -Dollama.timeout=300 -jar app.jar
```

**2. MCP Server Connection Issues**
```bash
# Test MCP server connectivity
curl -H "Accept: text/event-stream" http://localhost:8080/mcp/sse

# Check server logs
tail -f /var/log/mcp-server.log

# Try different server URL
# Update mcp.json with correct URL
```

**3. Memory Issues**
```bash
# Increase JVM heap size
java -Xmx4g -jar app.jar

# Monitor memory usage
jstat -gc -t [pid] 1s

# Use different garbage collector
java -XX:+UseG1GC -jar app.jar
```

### **Configuration Validation Script**

```bash
#!/bin/bash
# validate-config.sh

echo "Validating MCP Client Configuration..."

# Check Ollama server
echo "Checking Ollama server..."
if curl -s http://localhost:11434/api/tags > /dev/null; then
    echo "✓ Ollama server is running"
else
    echo "✗ Ollama server is not accessible"
    exit 1
fi

# Check MCP configuration
echo "Checking MCP configuration..."
if [ -f "mcp.json" ]; then
    echo "✓ mcp.json found"
    # Validate JSON syntax
    if python -m json.tool mcp.json > /dev/null 2>&1; then
        echo "✓ mcp.json is valid JSON"
    else
        echo "✗ mcp.json is invalid JSON"
        exit 1
    fi
else
    echo "⚠ mcp.json not found, will use bundled resource"
fi

# Check Java version
echo "Checking Java version..."
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{print $1}')
if [ "$java_version" -ge "21" ]; then
    echo "✓ Java version $java_version is supported"
else
    echo "✗ Java version $java_version is not supported (requires 21+)"
    exit 1
fi

echo "Configuration validation complete!"
```

---

## 📚 Configuration Best Practices

### **1. Environment Separation**
- Use different configurations for dev/test/prod
- Externalize sensitive configuration
- Use configuration management tools

### **2. Security**
- Avoid hardcoding credentials
- Use environment variables for sensitive data
- Implement proper SSL/TLS for production

### **3. Performance**
- Configure appropriate timeouts for your use case
- Monitor resource usage and adjust memory settings
- Use connection pooling for high-throughput scenarios

### **4. Monitoring**
- Enable appropriate logging levels
- Implement health checks
- Monitor key metrics (response times, error rates)

### **5. Documentation**
- Document configuration changes
- Maintain configuration versioning
- Create configuration templates

---

This configuration guide provides comprehensive information for setting up and tuning the MCP Java Client. For implementation details, see [Component Documentation](COMPONENTS.md) and [Architecture Overview](ARCHITECTURE.md).
