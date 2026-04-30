package org.training.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api")
public class ChatMemoryController {
    private final ChatClient client;

    public ChatMemoryController(@Qualifier("chatMemoryClient") ChatClient chatMemoryClient) {
        this.client = chatMemoryClient;
    }

    @GetMapping("/chat-memory")
    public String chat(@RequestParam String message, @RequestHeader String username) {
        return client.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, username))
                .call()
                .content();
    }
}
