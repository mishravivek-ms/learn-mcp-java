package org.acme.client;

import java.util.Scanner;

import org.acme.model.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;

/**
 * Service for managing chat interactions with Ollama LLM and MCP tools.
 * 
 * This service provides a console interface for chatting with
 * the Ollama language model using LangChain4J and integrates
 * with MCP tools for enhanced functionality.
 */
public class ChatService {

    private static final Logger LOG = LoggerFactory.getLogger(ChatService.class);

    private ToolsService toolsService;

    private Bot bot;
    private static final String DEFAULT_USER_ID = "default-user";

    private ChatModel chatModel;

    public ChatService() {
        toolsService = new ToolsService();

        try {
            // Build the ChatModel
            chatModel = OllamaChatModel.builder()
                    .baseUrl("http://localhost:11434") // Default Ollama endpoint
                    .modelName("llama3.2") // Specify the model to use
                    .temperature(0.7)
                    .build();

            // Initialize the chat memory store
            ChatMemoryStore chatMemoryStore = new InMemoryChatMemoryStore();

            // Initialize the Bot with chat model, tool provider, and memory
            bot = AiServices.builder(Bot.class)
                    .chatModel(chatModel)
                    .toolProvider(toolsService.getToolProvider())
                    .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
                            .maxMessages(20)
                            .chatMemoryStore(chatMemoryStore)
                            .id(memoryId)
                            .build())
                    .build();

            LOG.info("Chat service initialized with Ollama model and {} MCP tools",
                    toolsService.getAvailableTools().size());
        } catch (Exception e) {
            LOG.error("Failed to initialize chat service", e);
        }
    }

    /**
     * Sends a single message to the bot and returns the response.
     * The bot can use available MCP tools to provide enhanced responses.
     * 
     * @param message the message to send
     * @return the bot's response
     */
    public String chat(String message) {
        try {
            return bot.chat(DEFAULT_USER_ID, message);
        } catch (Exception e) {
            LOG.error("Error during chat interaction", e);
            return "Sorry, I encountered an error while processing your message.";
        }
    }

    /**
     * Starts an interactive chat session in the console.
     */
    public void startInteractiveChat() {
        System.out.println("=== Monkey MCP Chat ===");
        System.out.println("Starting chat with Ollama (llama3.2) + MCP Tools");
        System.out.println("Available tools: " + toolsService.getAvailableTools().size() + " monkey species tools");
        System.out.println("Type 'exit', 'quit', or 'bye' to end the conversation");
        System.out.println("Try asking: 'What monkey species do you know?' or 'Tell me about a random monkey'");
        System.out.println("================================================================\n");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("You: ");
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    continue;
                }

                // Check for exit commands
                if (input.equalsIgnoreCase("exit") ||
                        input.equalsIgnoreCase("quit") ||
                        input.equalsIgnoreCase("bye")) {
                    System.out.println("Goodbye! 👋");
                    break;
                }

                // Get response from AI Bot (with tools)
                System.out.print("AI: ");
                String response = chat(input);
                System.out.println(response + "\n");
            }
        }
    }

    /**
     * Checks if the chat service is available.
     * 
     * @return true if the bot is initialized and ready
     */
    public boolean isAvailable() {
        return bot != null && chatModel != null;
    }

    /**
     * Gets the number of available MCP tools.
     * 
     * @return the number of tools available to the bot
     */
    public int getAvailableToolsCount() {
        return toolsService.getAvailableTools().size();
    }
}
