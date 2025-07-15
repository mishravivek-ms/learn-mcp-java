package org.acme.client;

import static java.lang.System.out;

import java.util.Scanner;

import org.acme.model.Bot;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;

public class ChatService {

    private static final String DEFAULT_USER_ID = "default-user";

    private Bot bot;

    public ChatService() {
        var chatModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama3.2")
                .temperature(0.7)
                .build();

        var chatMemoryStore = new InMemoryChatMemoryStore();
        var toolsService = new ToolsService();

        bot = AiServices.builder(Bot.class)
                .chatModel(chatModel)
                .toolProvider(toolsService.getToolProvider())
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
                        .maxMessages(20)
                        .chatMemoryStore(chatMemoryStore)
                        .id(memoryId)
                        .build())
                .build();

        out.printf("✓ Chat initialized with Ollama model and %d MCP tool(s)",
                toolsService.getAvailableTools().size());
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
                        return;
                    }
                }

                var response = bot.chat(DEFAULT_USER_ID, input);

                out.printf("AI: %s\n", response);
            }
        }
    }

    private void printInfo() {
        out.println();
        out.println("═══════════════════════════════════");
        out.println("      Monkey MCP Chat");
        out.println("═══════════════════════════════════");
        out.println("→ Starting chat with Ollama (llama3.2) + MCP Tools");
        out.println();
        out.println("💡 Try asking:");
        out.println("   • 'What monkey species do you know?'");
        out.println("   • 'Tell me about a random monkey'");
        out.println();
        out.println("Type 'exit', 'quit', or 'bye' to end the conversation");
        out.println("───────────────────────────────────");
        out.println();
    }

}
