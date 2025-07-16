package org.acme.client;

import static java.lang.System.out;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.acme.config.OllamaConfig;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;

public class ChatService {

    private static final String SUUID = UUID.randomUUID().toString().substring(0, 8);
    private static final String RANDOM_USER = "user-" + SUUID;

    private Bot bot;

    private OllamaChatModel chatModel;

    private ToolsService toolsService = new ToolsService();

    interface Bot {

        @SystemMessage("""
                You are a helpful AI assistant with access to various information tools.
                You can answer questions about anything users ask and help with various tasks.
                Use the available tools to provide accurate and up to date information.
                """)
        String chat(@MemoryId String memoryId, @UserMessage String message);

        String chat(List<ChatMessage> messages);
    }

    public ChatService() {
        chatModel = OllamaChatModel.builder()
                .baseUrl(OllamaConfig.getBaseUrl())
                .modelName(OllamaConfig.getModelName())
                .timeout(OllamaConfig.getTimeout())
                .maxRetries(OllamaConfig.getMaxRetries())
                .temperature(OllamaConfig.getTemperature())
                .build();

        var chatMemoryStore = new InMemoryChatMemoryStore();

        bot = AiServices.builder(Bot.class)
                .chatModel(chatModel)
                .toolProvider(toolsService.getToolProvider())
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
                        .maxMessages(20)
                        .chatMemoryStore(chatMemoryStore)
                        .id(memoryId)
                        .build())
                .build();

        // Initialize the chat model and check if it's available
        try {
            out.println("🔄 Testing connection to Ollama...");
            bot.chat("health-check", "Say YES if you are ready to chat");
            out.println("✓ Connected to Ollama successfully!");
        } catch (RuntimeException e) {
            out.println("✗ Failed to connect to Ollama model: " + e.getMessage());
            out.println("💡 Troubleshooting tips:");
            out.println("   • Make sure Ollama is running: ollama serve");
            out.println("   • Check if your model is available: ollama list");
            out.println("   • Pull the model if needed: ollama pull llama3.2");
            out.println("   • Try a different model name in the code");
            chatModel = null;
            return;
        }

        out.printf("✓ Chat initialized!",
                toolsService.getAvailableTools().size());
    }

    public boolean isAvailable() {
        return chatModel != null && bot != null;
    }

    public void startInteractiveChat() {
        printInfo();

        try (var scanner = new Scanner(System.in)) {
            while (true) {
                out.print("You: ");
                var input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    continue;
                }
                switch (input.toLowerCase()) {
                    case "exit", "quit", "bye" -> {
                        out.println("👋 Goodbye!");
                        toolsService.shutdown();
                        return;
                    }
                }

                try {
                    out.print("🤖 AI is thinking... ");
                    out.flush();
                    var response = bot.chat(RANDOM_USER, input);
                    out.printf("\rAI: %s\n", response);
                } catch (Exception e) {
                    out.println("\r✗ Error: " + e.getMessage());
                    out.println("💡 This might be due to:");
                    out.println("   • Model taking too long to respond (try a smaller model)");
                    out.println("   • Ollama server issues (restart with 'ollama serve')");
                    out.println("   • Network connectivity problems");
                    out.print("Continue chatting? (y/n): ");
                    var continueChoice = scanner.nextLine().trim();
                    if (!"y".equalsIgnoreCase(continueChoice)) {
                        out.println("👋 Goodbye!");
                        toolsService.shutdown();
                        return;
                    }
                }
            }
        }
    }

    private void printInfo() {
        out.printf("""
                ════════════════════════════════════
                      Java MCP Chat Client
                ════════════════════════════════════
                → Starting chat with Ollama (%s) + MCP Tools
                → Timeout: %s seconds
                → Max retries: %d

                💡 Try asking:

                   • 'What monkey species do you know?'
                   • 'Tell me about a random monkey'

                Type 'exit', 'quit', or 'bye' to end the conversation
                ───────────────────────────────------------------────
                """, 
                OllamaConfig.getModelName(), 
                OllamaConfig.getTimeout().getSeconds(),
                OllamaConfig.getMaxRetries());
    }

}
