# MCP Java Client - Troubleshooting Guide

## 🔧 Troubleshooting Overview

This guide provides comprehensive troubleshooting information for common issues encountered when using the MCP Java Client.

---

## 🚨 Common Issues and Solutions

### **1. Timeout Exceptions**

**Symptoms**:
```
dev.langchain4j.exception.TimeoutException: request timed out
at dev.langchain4j.http.client.jdk.JdkHttpClient.execute(JdkHttpClient.java:58)
at dev.langchain4j.model.ollama.OllamaClient.chat(OllamaClient.java:93)
```

**Root Causes**:
- Ollama server not running
- Model taking too long to respond
- Network connectivity issues
- Insufficient system resources

**Solutions**:
```bash
# 1. Check Ollama server status
ollama serve

# 2. Verify model is available
ollama list

# 3. Pull model if needed
ollama pull llama3.2

# 4. Increase timeout
java -Dollama.timeout=1800 -jar target/cli-mcp-client.jar chat

# 5. Try smaller model
java -Dollama.model=llama3.2:1b -jar target/cli-mcp-client.jar chat
```

### **2. MCP Server Connection Issues**

**Symptoms**:
```
✗ Failed to register MCP server: monkeymcp - Connection refused
⚠ Failed to get tools from MCP server: monkeymcp - SocketTimeoutException
```

**Root Causes**:
- MCP server not running
- Incorrect server URL
- Network firewall blocking connection
- Server overloaded

**Solutions**:
```bash
# 1. Check MCP server status
curl -H "Accept: text/event-stream" http://localhost:8080/mcp/sse

# 2. Verify server configuration
cat mcp.json

# 3. Check network connectivity
telnet localhost 8080

# 4. Review server logs
tail -f /var/log/mcp-server.log

# 5. Update mcp.json with correct URL
{
  "servers": {
    "monkeymcp": {
      "type": "sse",
      "url": "http://localhost:8080/mcp/sse"
    }
  }
}
```

### **3. Memory and Performance Issues**

**Symptoms**:
```
java.lang.OutOfMemoryError: Java heap space
Slow response times
High CPU usage
```

**Root Causes**:
- Insufficient JVM heap size
- Large conversation history
- Memory leaks
- Inefficient garbage collection

**Solutions**:
```bash
# 1. Increase heap size
java -Xmx4g -Xms1g -jar target/cli-mcp-client.jar chat

# 2. Use different garbage collector
java -XX:+UseG1GC -jar target/cli-mcp-client.jar chat

# 3. Monitor memory usage
jstat -gc -t [pid] 1s

# 4. Profile application
java -XX:+FlightRecorder -XX:StartFlightRecording=duration=60s,filename=profile.jfr -jar app.jar

# 5. Reduce conversation history
# Modify ChatService to use smaller message window
MessageWindowChatMemory.builder()
    .maxMessages(10)  // Reduced from 20
    .build();
```

### **4. Model Loading Issues**

**Symptoms**:
```
✗ Failed to connect to Ollama model: Model not found
Model loading failed
```

**Root Causes**:
- Model not installed
- Incorrect model name
- Corrupted model files
- Insufficient disk space

**Solutions**:
```bash
# 1. List available models
ollama list

# 2. Pull required model
ollama pull llama3.2

# 3. Check model name in configuration
java -Dollama.model=llama3.2 -jar target/cli-mcp-client.jar chat

# 4. Verify disk space
df -h

# 5. Remove unused models
ollama rm unused-model-name
```

---

## 🔍 Diagnostic Commands

### **System Health Check**

```bash
#!/bin/bash
# health-check.sh

echo "=== MCP Java Client Health Check ==="

# Check Java version
echo "Java Version:"
java -version

# Check Ollama server
echo -e "\nOllama Server Status:"
if curl -s http://localhost:11434/api/tags > /dev/null; then
    echo "✓ Ollama server is running"
    echo "Available models:"
    ollama list
else
    echo "✗ Ollama server is not accessible"
fi

# Check MCP server
echo -e "\nMCP Server Status:"
if curl -s -H "Accept: text/event-stream" http://localhost:8080/mcp/sse > /dev/null; then
    echo "✓ MCP server is accessible"
else
    echo "✗ MCP server is not accessible"
fi

# Check system resources
echo -e "\nSystem Resources:"
echo "Memory Usage:"
free -h
echo "Disk Usage:"
df -h
echo "CPU Usage:"
top -bn1 | grep "Cpu(s)"
```

### **Application Diagnostics**

```bash
#!/bin/bash
# diagnose-app.sh

echo "=== Application Diagnostics ==="

# Check configuration
echo "Configuration Files:"
echo "mcp.json exists: $([ -f mcp.json ] && echo "Yes" || echo "No")"
echo "logging.properties exists: $([ -f src/main/resources/logging.properties ] && echo "Yes" || echo "No")"

# Check build status
echo -e "\nBuild Status:"
mvn clean compile -q && echo "✓ Build successful" || echo "✗ Build failed"

# Check dependencies
echo -e "\nDependency Check:"
mvn dependency:tree -q | head -20

# Check for common issues
echo -e "\nCommon Issues Check:"
echo "Port 11434 (Ollama): $(netstat -an | grep :11434 | wc -l) connections"
echo "Port 8080 (MCP): $(netstat -an | grep :8080 | wc -l) connections"
```

---

## 📊 Monitoring and Logging

### **Enable Debug Logging**

```properties
# logging.properties
.level=DEBUG
dev.langchain4j.level=DEBUG
org.acme.level=DEBUG

# Enable file logging
handlers=java.util.logging.FileHandler,java.util.logging.ConsoleHandler
java.util.logging.FileHandler.pattern=logs/debug-%u.log
java.util.logging.FileHandler.limit=10485760
java.util.logging.FileHandler.count=5
java.util.logging.FileHandler.formatter=java.util.logging.SimpleFormatter
```

### **Performance Monitoring**

```java
// Performance monitoring utility
public class PerformanceMonitor {
    
    public static void monitorChatPerformance() {
        long startTime = System.currentTimeMillis();
        
        try {
            // Chat operation
            String response = chatService.processMessage("test");
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            System.out.println("Chat response time: " + duration + "ms");
            
            if (duration > 30000) {
                System.out.println("⚠ Slow response detected");
            }
            
        } catch (Exception e) {
            System.err.println("Chat error: " + e.getMessage());
        }
    }
    
    public static void monitorMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        System.out.println("Memory usage: " + (usedMemory / 1024 / 1024) + "MB");
        
        if (usedMemory > totalMemory * 0.8) {
            System.out.println("⚠ High memory usage detected");
        }
    }
}
```

---

## 🛠️ Error Recovery Strategies

### **Automatic Recovery**

```java
public class ErrorRecoveryService {
    
    public String handleChatError(Exception e, String userMessage) {
        if (e instanceof TimeoutException) {
            // Retry with shorter timeout
            return retryChatWithTimeout(userMessage, Duration.ofSeconds(30));
        }
        
        if (e instanceof ConnectionException) {
            // Attempt reconnection
            reconnectServices();
            return retryChatWithTimeout(userMessage, Duration.ofMinutes(5));
        }
        
        if (e instanceof OutOfMemoryError) {
            // Clear memory and retry
            clearChatMemory();
            System.gc();
            return retryChatWithTimeout(userMessage, Duration.ofMinutes(10));
        }
        
        // Default fallback
        return "I encountered an issue. Please try again or rephrase your question.";
    }
    
    private void reconnectServices() {
        // Reinitialize MCP connections
        toolsService.shutdown();
        toolsService = new ToolsService();
        
        // Reinitialize chat service
        chatService = new ChatService();
    }
}
```

### **Circuit Breaker Pattern**

```java
public class CircuitBreaker {
    
    private int failureCount = 0;
    private long lastFailureTime = 0;
    private final int failureThreshold = 3;
    private final long recoveryTimeout = 60000; // 1 minute
    
    public String executeChatWithCircuitBreaker(String message) {
        if (isCircuitOpen()) {
            return "Service temporarily unavailable. Please try again later.";
        }
        
        try {
            String response = chatService.processMessage(message);
            reset();
            return response;
        } catch (Exception e) {
            recordFailure();
            throw e;
        }
    }
    
    private boolean isCircuitOpen() {
        if (failureCount >= failureThreshold) {
            return System.currentTimeMillis() - lastFailureTime < recoveryTimeout;
        }
        return false;
    }
    
    private void recordFailure() {
        failureCount++;
        lastFailureTime = System.currentTimeMillis();
    }
    
    private void reset() {
        failureCount = 0;
        lastFailureTime = 0;
    }
}
```

---

## 🔧 Configuration Troubleshooting

### **Configuration Validation**

```java
public class ConfigurationValidator {
    
    public List<String> validateConfiguration() {
        List<String> issues = new ArrayList<>();
        
        // Validate Ollama configuration
        if (!isOllamaAccessible()) {
            issues.add("Ollama server is not accessible at " + OllamaConfig.getBaseUrl());
        }
        
        // Validate model availability
        if (!isModelAvailable(OllamaConfig.getModelName())) {
            issues.add("Model not available: " + OllamaConfig.getModelName());
        }
        
        // Validate MCP configuration
        if (!isMcpConfigValid()) {
            issues.add("MCP configuration is invalid or servers are unreachable");
        }
        
        // Validate memory settings
        if (isMemoryInsufficient()) {
            issues.add("Insufficient memory allocated. Consider increasing heap size.");
        }
        
        return issues;
    }
    
    private boolean isOllamaAccessible() {
        try {
            URL url = new URL(OllamaConfig.getBaseUrl() + "/api/tags");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            return connection.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
```

### **Environment-Specific Issues**

**Development Environment**:
```bash
# Common dev issues
# 1. Ollama not started
ollama serve

# 2. Wrong model
ollama pull llama3.2:1b  # Use smaller model for dev

# 3. Port conflicts
netstat -an | grep :11434
```

**Production Environment**:
```bash
# Common prod issues
# 1. Insufficient memory
java -Xmx8g -jar app.jar

# 2. Network timeouts
# Increase timeout in configuration
java -Dollama.timeout=3600 -jar app.jar

# 3. Connection pooling
# Monitor connection usage
ss -tuln | grep :8080
```

---

## 📋 Error Reference

### **Common Error Codes**

| Error Code | Description | Solution |
|------------|-------------|----------|
| `TIMEOUT_001` | Ollama request timeout | Increase timeout or use smaller model |
| `CONNECTION_002` | MCP server unreachable | Check server status and network |
| `MEMORY_003` | Out of memory | Increase heap size or reduce history |
| `MODEL_004` | Model not found | Pull model or correct model name |
| `CONFIG_005` | Invalid configuration | Validate and fix configuration files |

### **Error Pattern Analysis**

```java
public class ErrorPatternAnalyzer {
    
    public void analyzeErrorPattern(List<Exception> recentErrors) {
        Map<String, Long> errorCounts = recentErrors.stream()
            .collect(Collectors.groupingBy(
                e -> e.getClass().getSimpleName(),
                Collectors.counting()
            ));
        
        errorCounts.forEach((errorType, count) -> {
            if (count > 5) {
                System.out.println("⚠ High frequency error: " + errorType + " (" + count + " occurrences)");
                suggestSolution(errorType);
            }
        });
    }
    
    private void suggestSolution(String errorType) {
        switch (errorType) {
            case "TimeoutException":
                System.out.println("💡 Consider increasing timeout or using a smaller model");
                break;
            case "ConnectionException":
                System.out.println("💡 Check network connectivity and server status");
                break;
            case "OutOfMemoryError":
                System.out.println("💡 Increase JVM heap size or reduce conversation history");
                break;
            default:
                System.out.println("💡 Review logs for more details");
        }
    }
}
```

---

## 🚀 Performance Optimization

### **Response Time Optimization**

```bash
# 1. Use faster model
java -Dollama.model=llama3.2:1b -jar app.jar

# 2. Reduce temperature for faster responses
java -Dollama.temperature=0.1 -jar app.jar

# 3. Enable response caching
java -Dcache.enabled=true -jar app.jar

# 4. Use SSD storage for models
# Move Ollama model directory to SSD
```

### **Memory Optimization**

```bash
# 1. Tune garbage collection
java -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -jar app.jar

# 2. Set appropriate heap size
java -Xmx4g -Xms2g -jar app.jar

# 3. Monitor memory usage
jstat -gc -t [pid] 5s
```

### **Network Optimization**

```bash
# 1. Increase connection timeout
java -Dhttp.connection.timeout=30000 -jar app.jar

# 2. Use connection pooling
java -Dhttp.connection.pool.size=10 -jar app.jar

# 3. Enable compression
java -Dhttp.compression.enabled=true -jar app.jar
```

---

## 🆘 Emergency Procedures

### **System Recovery**

```bash
#!/bin/bash
# emergency-recovery.sh

echo "=== Emergency Recovery Procedure ==="

# 1. Stop all processes
echo "Stopping services..."
pkill -f "ollama serve"
pkill -f "mcp-server"
pkill -f "cli-mcp-client"

# 2. Clean up temporary files
echo "Cleaning temporary files..."
rm -rf /tmp/ollama*
rm -rf /tmp/mcp*

# 3. Restart Ollama
echo "Restarting Ollama..."
ollama serve &
sleep 10

# 4. Restart MCP server
echo "Restarting MCP server..."
# Add your MCP server restart command here

# 5. Test connectivity
echo "Testing connectivity..."
curl -s http://localhost:11434/api/tags > /dev/null && echo "✓ Ollama OK" || echo "✗ Ollama Failed"
curl -s http://localhost:8080/mcp/sse > /dev/null && echo "✓ MCP OK" || echo "✗ MCP Failed"

echo "Recovery procedure complete"
```

### **Data Recovery**

```bash
# Backup conversation history
cp -r ~/.mcp-client/conversations/ backup/

# Restore from backup
cp -r backup/conversations/ ~/.mcp-client/

# Clear corrupted data
rm -rf ~/.mcp-client/cache/
```

---

## 📞 Support Resources

### **Log Collection**

```bash
#!/bin/bash
# collect-logs.sh

echo "Collecting diagnostic information..."

# Create support bundle
mkdir -p support-bundle
cd support-bundle

# System information
uname -a > system-info.txt
java -version > java-version.txt 2>&1
ps aux | grep -E "(ollama|mcp)" > running-processes.txt

# Application logs
cp ../logs/*.log . 2>/dev/null || echo "No application logs found"

# Configuration files
cp ../mcp.json . 2>/dev/null || echo "No mcp.json found"
cp ../src/main/resources/logging.properties . 2>/dev/null || echo "No logging.properties found"

# Network information
netstat -an | grep -E "(11434|8080)" > network-status.txt

# Create archive
cd ..
tar -czf support-bundle-$(date +%Y%m%d-%H%M%S).tar.gz support-bundle/

echo "Support bundle created: support-bundle-$(date +%Y%m%d-%H%M%S).tar.gz"
```

### **Community Resources**

- **GitHub Issues**: Report bugs and request features
- **Documentation**: Comprehensive guides and examples
- **Stack Overflow**: Community Q&A with `mcp-java-client` tag
- **Discord/Slack**: Real-time community support

---

This troubleshooting guide provides comprehensive solutions for common issues. For architecture details, see [Architecture Overview](ARCHITECTURE.md), and for configuration options, see [Configuration Guide](CONFIGURATION.md).
