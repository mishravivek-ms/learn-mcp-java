package org.acme.command;

import org.acme.client.ChatService;

import picocli.CommandLine.Command;

@Command(name = "chat", description = "Start a chat session with an LLM")
public class ChatCommand implements Runnable {

    @Override
    public void run() {
        var chatService = new ChatService();
        chatService.startInteractiveChat();
    }

}
