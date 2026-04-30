package org.training.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.training.springai.advisors.TokenUsageAuditAdvisor;
import org.training.springai.dto.CountryCities;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StructuredOutputController {
    private final ChatClient client;

    public StructuredOutputController(OpenAiChatModel openAiChatModel) {
        ChatOptions options = OpenAiChatOptions.builder()
                .model(OpenAiApi.ChatModel.GPT_5_CHAT_LATEST)
                .build();
        this.client = ChatClient.builder(openAiChatModel)
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), new TokenUsageAuditAdvisor()))
                .defaultOptions(options)
                .build();
    }

    @GetMapping("/structured-response")
    public ResponseEntity<CountryCities> structuredResponse(@RequestParam String message) {
        CountryCities countryCities = client.prompt()
                .user(message)
                .call().entity(CountryCities.class);
        return ResponseEntity.ok(countryCities);
    }

    @GetMapping("/structured-response2")
    public ResponseEntity<CountryCities> structuredResponse2(@RequestParam String message) {
        CountryCities countryCities = client.prompt()
                .user(message)
                .call().entity(new BeanOutputConverter<>(CountryCities.class));
        return ResponseEntity.ok(countryCities);
    }

    @GetMapping("/list-response")
    public ResponseEntity<List<String>> listResponse(@RequestParam String message) {
        List<String> countryCities = client.prompt()
                .user(message)
                .call().entity(new ListOutputConverter());
        return ResponseEntity.ok(countryCities);
    }

    @GetMapping("/map-response")
    public ResponseEntity<Map<String, Object>> mapResponse(@RequestParam String message) {
        Map<String, Object> countryCities = client.prompt()
                .user(message)
                .call().entity(new MapOutputConverter());
        return ResponseEntity.ok(countryCities);
    }

    @GetMapping("/structured-list-response")
    public ResponseEntity<List<CountryCities>> structuredListResponse(@RequestParam String message) {
        List<CountryCities> countryCities = client.prompt()
                .user(message)
                .call().entity(new ParameterizedTypeReference<List<CountryCities>>() {
                });
        return ResponseEntity.ok(countryCities);
    }
}
