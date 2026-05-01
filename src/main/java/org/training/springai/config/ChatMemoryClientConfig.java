package org.training.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.training.springai.advisors.TokenUsageAuditAdvisor;
import org.training.springai.rag.SensitiveDataMaskingProcessor;

@Configuration
public class ChatMemoryClientConfig {


    @Value("classpath:prompts/hr-data-system-prompt.st")
    Resource hrPromptTemplate;

    @Bean
    ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        return MessageWindowChatMemory.builder().maxMessages(20).chatMemoryRepository(jdbcChatMemoryRepository).build();
    }

    @Bean("chatMemoryClient")
    public ChatClient chatMemoryClient(OpenAiChatModel openAiChatModel, ChatMemory chatMemory,
                                       RetrievalAugmentationAdvisor retrievalAugmentationAdvisor) {
// automatically finds the bean of chat memory - if there is no DB dependency then  uses in memory hashmap
        Advisor memoryAdvisor =  MessageChatMemoryAdvisor.builder(chatMemory).build();
        Advisor loggerAdvisor = new SimpleLoggerAdvisor();
        return ChatClient.builder(openAiChatModel)
                .defaultAdvisors(loggerAdvisor, memoryAdvisor, new TokenUsageAuditAdvisor(), retrievalAugmentationAdvisor)
                .build();
    }

    @Bean     // advisor to retrieve some data from vector store
    RetrievalAugmentationAdvisor retrievalAugmentationAdvisor(VectorStore vectorStore, OpenAiChatModel openAiChatModel) {
        return RetrievalAugmentationAdvisor.builder()
                .queryTransformers(TranslationQueryTransformer.builder()
                        .chatClientBuilder(ChatClient.builder(openAiChatModel)).targetLanguage("English").build())
                .documentRetriever(
                        VectorStoreDocumentRetriever.builder().vectorStore(vectorStore).topK(3).similarityThreshold(0.5).build()
        ).documentPostProcessors(SensitiveDataMaskingProcessor.builder())
                .build();
    }
}
