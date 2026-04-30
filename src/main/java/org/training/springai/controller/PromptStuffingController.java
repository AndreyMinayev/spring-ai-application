package org.training.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PromptStuffingController {
    private final ChatClient openAiChatClient;
    private final ChatClient ollamaChatClient;
    @Value("classpath:prompts/system-prompt.st")
    private Resource systemPromptTemplate;

    public PromptStuffingController(@Qualifier("openAiChatClient") ChatClient openAiChatClient,
                                    @Qualifier("ollamaChatClient") ChatClient ollamaChatClient) {
        this.openAiChatClient = openAiChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }

    @GetMapping("/stuffed-chat")
    public String stuffedChat(@RequestParam String message,
                                @RequestParam(defaultValue = "ollama") String model) {
        ChatClient client = "openai".equalsIgnoreCase(model) ? openAiChatClient : ollamaChatClient;
        return client.prompt()
                .system(systemPromptTemplate)
                .user(message)
                .call().content();
    }
}
