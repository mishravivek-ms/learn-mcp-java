package org.acme.command;

import org.acme.client.ChatService;

import picocli.CommandLine.Command;

import static java.lang.System.err;

@Command(name = "chat", description = "Start a chat session with Ollama LLM")
public class ChatCommand implements Runnable {

    ChatService chatService = new ChatService();

    @Override
    public void run() {
        if (!chatService.isAvailable()) {
            err.println("Error: Chat service is not available.");
            err.println("Please ensure Ollama is running on localhost:11434");
            return;
        }

        chatService.startInteractiveChat();
    }
}
