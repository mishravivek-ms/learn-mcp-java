package org.acme.model;

import java.util.List;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface Bot {

    @SystemMessage("""
            You are a helpful AI assistant with access to various information tools.
            You can answer questions about anything users ask and help with various tasks.
            Use the available tools to provide accurate information.
            """)
    String chat(@MemoryId String memoryId, @UserMessage String message);

    String chat(List<ChatMessage> messages);
}
