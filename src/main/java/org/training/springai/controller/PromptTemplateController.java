package org.training.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.training.springai.controller.ControllerUtils.selectClient;

@RestController
@RequestMapping("/api")
public class PromptTemplateController {
    private final ChatClient openAiChatClient;
    private final ChatClient ollamaChatClient;
    @Value("classpath:prompts/email-assistant-prompt.st")
    private Resource promptTemplate;

    public PromptTemplateController(@Qualifier("openAiChatClient") ChatClient openAiChatClient,
                                    @Qualifier("ollamaChatClient") ChatClient ollamaChatClient) {
        this.openAiChatClient = openAiChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }

    @GetMapping("/email")
    public String emailResponse(@RequestParam String customerName, @RequestParam String customerMessage,
                                @RequestParam(defaultValue = "ollama") String model) {
        ChatClient client = selectClient(model, openAiChatClient, ollamaChatClient);
        return client.prompt()
                .system("You are professional service assistant witch helps drafting responses")
                .user(promptUserSpec -> promptUserSpec.text(promptTemplate)
                        .param("customerName", customerName)
                        .param("customerMessage", customerMessage))
                .call().content();
    }
}
