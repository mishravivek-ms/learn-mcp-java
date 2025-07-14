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

import static java.lang.System.out;

public class ChatService {

    private static final Logger LOG = LoggerFactory.getLogger(ChatService.class);

    private ToolsService toolsService;

    private Bot bot;
    private static final String DEFAULT_USER_ID = "default-user";

    private ChatModel chatModel;

    public ChatService() {
        toolsService = new ToolsService();

        try {
            chatModel = OllamaChatModel.builder()
                    .baseUrl("http://localhost:11434") // Default Ollama endpoint
                    .modelName("llama3.2") // Specify the model to use
                    .temperature(0.7)
                    .build();

            ChatMemoryStore chatMemoryStore = new InMemoryChatMemoryStore();

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

    public String chat(String message) {
        try {
            return bot.chat(DEFAULT_USER_ID, message);
        } catch (Exception e) {
            LOG.error("Error during chat interaction", e);
            return "Sorry, I encountered an error while processing your message.";
        }
    }

    public void startInteractiveChat() {
        out.println("=== Monkey MCP Chat ===");
        out.println("Starting chat with Ollama (llama3.2) + MCP Tools");
        out.println("Available tools: " + toolsService.getAvailableTools().size() + " monkey species tools");
        out.println("Type 'exit', 'quit', or 'bye' to end the conversation");
        out.println("Try asking: 'What monkey species do you know?' or 'Tell me about a random monkey'");
        out.println("================================================================\n");

        try (var scanner = new Scanner(System.in)) {
            while (true) {
                out.print("You: ");
                var input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    continue;
                }

                switch (input.toLowerCase()) {
                    case "exit", "quit", "bye" -> {
                        out.println("Goodbye! 👋");
                        return;
                    }
                }

                var response = chat(input);
                out.printf("AI: %s\n", response);
            }
        }
    }

    public boolean isAvailable() {
        return bot != null && chatModel != null;
    }

}
