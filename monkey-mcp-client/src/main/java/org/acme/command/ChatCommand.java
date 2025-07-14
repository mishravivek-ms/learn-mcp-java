package org.acme.command;

import org.acme.client.ChatService;

import picocli.CommandLine.Command;

@Command(name = "chat", description = "Start a chat session with Ollama LLM")
public class ChatCommand implements Runnable {

    ChatService chatService = new ChatService();

    @Override
    public void run() {
        chatService.startInteractiveChat();
    }
}
