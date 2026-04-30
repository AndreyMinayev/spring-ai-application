package org.training.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.training.springai.advisors.TokenUsageAuditAdvisor;

import java.util.List;

@Configuration
public class ChatClientConfig {

    @Value("classpath:prompts/hr-system-prompt.st")
    private Resource hrSystemPrompt;

    @Bean
    public ChatClient openAiChatClient(OpenAiChatModel openAiChatModel) {
        ChatOptions options = OpenAiChatOptions.builder()
                .model(OpenAiApi.ChatModel.GPT_5_CHAT_LATEST)
                .build();

        return ChatClient.builder(openAiChatModel)
//                .defaultSystem(hrSystemPrompt)
                .defaultOptions(options)
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), new TokenUsageAuditAdvisor()))
                .build();
    }

    @Bean
    public ChatClient ollamaChatClient(OllamaChatModel ollamaChatModel) {
        return ChatClient.builder(ollamaChatModel)
                .defaultSystem(hrSystemPrompt)
                .build();
    }
}
