package org.training.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api/rag")
public class RagController {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("classpath:prompts/random-data-system-prompt.st")
    Resource promptTemplate;

    @Value("classpath:prompts/hr-data-system-prompt.st")
    Resource hrPromptTemplate;

    public RagController(@Qualifier("chatMemoryClient") ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message, @RequestHeader String username) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(message)
                .topK(3)  // only top 3 results will be considered
                .similarityThreshold(0.5)
                .build();
        List<Document> data = vectorStore.similaritySearch(searchRequest);
        String context = data.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));
        return chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, username))
                .system(promptSpec -> promptSpec.text(promptTemplate).param("documents", context))
                .call()
                .content();
    }

    @GetMapping("/hr-chat")
    public String hrChat(@RequestParam String message, @RequestHeader String username) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(message)
                .topK(3)  // only top 3 results will be considered
                .similarityThreshold(0.5)
                .build();
        List<Document> data = vectorStore.similaritySearch(searchRequest);
        String context = data.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));
        return chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, username))
                .system(promptSpec -> promptSpec.text(hrPromptTemplate).param("documents", context))
                .call()
                .content();
    }

    @GetMapping("/hr-chat2") // advisor does everything  automatically - minor config in Chat memory client config
    public String hrChat2(@RequestParam String message, @RequestHeader String username) {
        return chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, username))
                .call()
                .content();
    }
}
