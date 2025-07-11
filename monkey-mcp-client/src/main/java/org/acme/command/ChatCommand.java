package org.acme.command;

import org.acme.client.ChatService;

import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command to start a chat session with Ollama LLM.
 * 
 * This command provides an interactive chat interface with the Ollama
 * language model using LangChain4J for conversation management.
 */
@Command(name = "chat", 
         description = "Start a chat session with Ollama LLM")
public class ChatCommand implements Runnable {

    @Inject
    ChatService chatService;

    @Option(names = {"-m", "--message"}, description = "Send a single message and exit")
    private String singleMessage;

    @Parameters(description = "Message to send (alternative to --message)")
    private String[] messageArgs;

    @Override
    public void run() {
        try {
            if (!chatService.isAvailable()) {
                System.err.println("Error: Chat service is not available.");
                System.err.println("Please ensure Ollama is running on localhost:11434");
                return;
            }

            if (singleMessage != null || (messageArgs != null && messageArgs.length > 0)) {
                String message = singleMessage != null ? singleMessage : String.join(" ", messageArgs);
                System.out.println("You: " + message);
                System.out.print("AI: ");
                String response = chatService.chat(message);
                System.out.println(response);
                return;
            }

            chatService.startInteractiveChat();
        } catch (Exception e) {
            System.err.println("Error during chat session: " + e.getMessage());
        }
    }
}
