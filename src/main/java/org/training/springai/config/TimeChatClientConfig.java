package org.training.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.training.springai.advisors.TokenUsageAuditAdvisor;
import org.training.springai.tools.TimeTools;

@Configuration
public class TimeChatClientConfig {
    @Bean("timeChatClient")
    public ChatClient chatClient(OpenAiChatModel openAiChatModel, ChatMemory chatMemory, TimeTools timeTools) {
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        return ChatClient.builder(openAiChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor(), memoryAdvisor, new TokenUsageAuditAdvisor())
                .defaultTools(timeTools)
                .build();
    }
}
