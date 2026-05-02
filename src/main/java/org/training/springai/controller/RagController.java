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
    private final ChatClient webSearchChatClient;
    private final VectorStore vectorStore;
    @Value("classpath:prompts/random-data-system-prompt.st")
    Resource promptTemplate;
    @Value("classpath:prompts/hr-data-system-prompt.st")
    Resource hrPromptTemplate;

    public RagController(@Qualifier("chatMemoryClient") ChatClient chatClient,
                         @Qualifier("webSearchRAGChatClient") ChatClient webSearchChatClient,
                         VectorStore vectorStore) {
        this.webSearchChatClient = webSearchChatClient;
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message, @RequestHeader String username) {
        return ragChat(message, username, promptTemplate);
    }

    @GetMapping("/hr-chat")
    public String hrChat(@RequestParam String message, @RequestHeader String username) {
        return ragChat(message, username, hrPromptTemplate);
    }

    private String ragChat(String message, String username, Resource template) {
        List<Document> data = vectorStore.similaritySearch(SearchRequest.builder()
                .query(message).topK(3).similarityThreshold(0.5).build());
        String context = data.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));
        return chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, username))
                .system(promptSpec -> promptSpec.text(template).param("documents", context))
                .call()
                .content();
    }

    @GetMapping("/hr-chat-automatic")
    // advisor does everything  automatically - minor config in Chat memory client config
    public String hrChat2(@RequestParam String message, @RequestHeader String username) {
        return chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, username))
                .call()
                .content();
    }

    @GetMapping("/web-search")
    public String webSearch(@RequestParam String message, @RequestHeader String username) {
        return webSearchChatClient.prompt()
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, username))
                .user(message)
                .call()
                .content();
    }
}
