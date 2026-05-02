package org.training.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.training.springai.controller.ControllerUtils.selectClient;

@RestController
@RequestMapping("/api")
public class StreamController {
    private final ChatClient openAiChatClient;
    private final ChatClient ollamaChatClient;

    public StreamController(@Qualifier("openAiChatClient") ChatClient openAiChatClient,
                            @Qualifier("ollamaChatClient") ChatClient ollamaChatClient) {
        this.openAiChatClient = openAiChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }

    @GetMapping("/stream")
    public Flux<String> chat(@RequestParam String message,
                             @RequestParam(defaultValue = "ollama") String model) {
        ChatClient client = selectClient(model, openAiChatClient, ollamaChatClient);
        return client.prompt()
                .user(message)
                .stream().content();
    }
}
