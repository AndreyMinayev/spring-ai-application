package org.training.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.training.springai.advisors.TokenUsageAuditAdvisor;

import static org.training.springai.controller.ControllerUtils.*;

@RestController
@RequestMapping("/api")
public class ChatController {
    private final ChatClient openAiChatClient;
    private final ChatClient ollamaChatClient;

    public ChatController(@Qualifier("openAiChatClient") ChatClient openAiChatClient,
                          @Qualifier("ollamaChatClient") ChatClient ollamaChatClient) {
        this.openAiChatClient = openAiChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message,
                       @RequestParam(defaultValue = "ollama") String model) {
        if (message == null || message.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message must not be empty");
        }
        if (message.length() > MAX_MESSAGE_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message exceeds maximum length");
        }
        if (!VALID_MODELS.contains(model.toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid model. Allowed: " + VALID_MODELS);
        }
        ChatClient client = selectClient(model, openAiChatClient, ollamaChatClient);
        return client.prompt()
                .advisors(new TokenUsageAuditAdvisor())
                .user(message)
                .call().content();
    }
}
