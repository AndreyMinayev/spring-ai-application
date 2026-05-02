package org.training.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.training.springai.advisors.TokenUsageAuditAdvisor;
import org.training.springai.rag.WebSearchDocumentRetriever;

import java.util.List;

@Configuration
public class WebSearchRAGChatClientConfig {
    @Bean
    public RetrievalAugmentationAdvisor webSearchRAGAdvisor(RestClient.Builder restClientBuilder) {
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(WebSearchDocumentRetriever.builder()
                        .restClientBuilder(restClientBuilder).maxResults(5).build())
                .build();
    }

    @Bean("webSearchRAGChatClient")
    public ChatClient chatClient(OpenAiChatModel openAiChatModel, ChatMemory chatMemory,
                                 RetrievalAugmentationAdvisor webSearchRAGAdvisor) {
        return ChatClient.builder(openAiChatModel)
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        new TokenUsageAuditAdvisor(), webSearchRAGAdvisor))
                .build();
    }
}
