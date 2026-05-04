package org.training.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.training.springai.advisors.TokenUsageAuditAdvisor;

@Configuration
public class HelpDeskChatClientConfig {

    @Value("classpath:prompts/help-desk-prompt.st")
    Resource helpDeskPromptTemplate;

    @Bean("helpDeskChatClient")
    public ChatClient chatClient(OpenAiChatModel openAiChatModel, ChatMemory chatMemory) {
        Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        return ChatClient.builder(openAiChatModel)
                .defaultSystem(helpDeskPromptTemplate)
                .defaultAdvisors(new SimpleLoggerAdvisor(), memoryAdvisor, new TokenUsageAuditAdvisor())
                .build();
    }
}
