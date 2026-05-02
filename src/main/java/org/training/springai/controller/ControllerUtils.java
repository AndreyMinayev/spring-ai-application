package org.training.springai.controller;

import org.springframework.ai.chat.client.ChatClient;

import java.util.Set;

public class ControllerUtils {
    public static final Set<String> VALID_MODELS = Set.of("ollama", "openai");
    public static final int MAX_MESSAGE_LENGTH = 1000;

    public static ChatClient selectClient(String model, ChatClient openAiChatClient, ChatClient ollamaChatClient) {
        return "openai".equalsIgnoreCase(model) ? openAiChatClient : ollamaChatClient;
    }
}
