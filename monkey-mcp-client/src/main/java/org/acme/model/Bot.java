package org.acme.model;

import java.util.List;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * Bot interface for AI interactions with tool support.
 * 
 * This interface defines the contract for AI services that can
 * chat with users and use available tools from MCP servers.
 */
public interface Bot {

    /**
     * Chat with the bot using natural language with memory support.
     * The bot can use available tools to provide enhanced responses.
     * 
     * @param memoryId unique identifier for conversation memory
     * @param message the user's message
     * @return the bot's response
     */
    @SystemMessage("You are a helpful AI assistant with access to monkey species information tools. " +
                  "You can answer questions about monkeys and help with various tasks. " +
                  "When users ask about monkey species, use the available tools to provide accurate information.")
    String chat(@MemoryId String memoryId, @UserMessage String message);

    /**
     * Chat with the bot using a list of messages.
     * 
     * @param messages the conversation history
     * @return the bot's response
     */
    String chat(List<ChatMessage> messages);
}
