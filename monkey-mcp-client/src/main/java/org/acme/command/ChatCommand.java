package org.acme.command;

import org.acme.client.ChatService;

import picocli.CommandLine.Command;

@Command(name = "chat", description = "Start a chat session")
public class ChatCommand implements Runnable {

    ChatService chatService = new ChatService();

    @Override
    public void run() {
        if (chatService.isAvailable()) {
            chatService.startInteractiveChat();
        } else {
            System.err.println("✗ Chat service is not available. Please check the Ollama model connection.");
        }

    }

}
