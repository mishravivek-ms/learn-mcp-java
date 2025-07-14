package org.acme.model;

import java.util.List;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface Bot {

    @SystemMessage("""
            You are a helpful AI assistant with access to monkey species information tools.
            You can answer questions about monkeys and help with various tasks.
            When users ask about monkey species, use the available tools to provide accurate information.
            """)
    String chat(@MemoryId String memoryId, @UserMessage String message);

    String chat(List<ChatMessage> messages);
}
