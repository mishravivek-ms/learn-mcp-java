# MCP Chat Client - Timeout Fix Guide

## Changes Made

The following changes have been implemented to fix the timeout issues:

### 1. **Increased Timeout Duration**
- Changed from 10 seconds to **10 minutes** (600 seconds)
- Added **retry mechanism** with up to 3 attempts
- Enhanced error handling with user-friendly messages

### 2. **Configuration Management**
- Created `OllamaConfig` class for centralized configuration
- All settings can be overridden via system properties

### 3. **Better Error Handling**
- Connection testing before starting chat
- Helpful troubleshooting messages on timeout
- Option to continue chatting after errors

## Usage

### Basic Usage
```bash
java -jar target/cli-mcp-client.jar chat
```

### Custom Configuration
You can override the default settings using system properties:

```bash
# Use different model
java -Dollama.model=llama2 -jar target/cli-mcp-client.jar chat

# Set custom timeout (in seconds)
java -Dollama.timeout=1800 -jar target/cli-mcp-client.jar chat

# Set maximum retries
java -Dollama.maxRetries=5 -jar target/cli-mcp-client.jar chat

# Set custom Ollama server URL
java -Dollama.baseUrl=http://remote-server:11434 -jar target/cli-mcp-client.jar chat

# Combine multiple settings
java -Dollama.model=codellama -Dollama.timeout=1200 -Dollama.maxRetries=3 -jar target/cli-mcp-client.jar chat
```

## Configuration Options

| Property | Default | Description |
|----------|---------|-------------|
| `ollama.baseUrl` | `http://localhost:11434` | Ollama server URL |
| `ollama.model` | `llama3.2` | Model name to use |
| `ollama.timeout` | `600` | Timeout in seconds |
| `ollama.maxRetries` | `3` | Maximum retry attempts |
| `ollama.temperature` | `0.7` | Model temperature |

## Troubleshooting

### Before Running
1. **Start Ollama server**: `ollama serve`
2. **Check available models**: `ollama list`
3. **Pull model if needed**: `ollama pull llama3.2`
4. **Test API**: `curl http://localhost:11434/api/tags`

### If You Still Get Timeouts
1. **Try a smaller model**: `ollama pull llama3.2:1b`
2. **Increase timeout**: `-Dollama.timeout=1800` (30 minutes)
3. **Check system resources**: Ensure sufficient RAM
4. **Use different model**: `-Dollama.model=llama2`

### Common Issues
- **Model not found**: Run `ollama pull <model-name>`
- **Connection refused**: Start Ollama with `ollama serve`
- **Still timing out**: Try `-Dollama.timeout=3600` (1 hour)

## Quick Start Checklist

1. ✅ **Ollama is running**: `ollama serve`
2. ✅ **Model is available**: `ollama list`
3. ✅ **Build the project**: `mvn clean package`
4. ✅ **Run with timeout fix**: `java -jar target/cli-mcp-client.jar chat`

The timeout issue should now be resolved with these changes!
