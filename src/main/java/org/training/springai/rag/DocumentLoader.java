package org.training.springai.rag;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DocumentLoader {
    private final VectorStore vectorStore;

    @Value("classpath:data/hr-policy.pdf")
    Resource policyFile;

    @PostConstruct
    public void loadDocument() {
        TikaDocumentReader reader = new TikaDocumentReader(policyFile);
        List<Document> documents = new ArrayList<>(reader.get());
        vectorStore.add(documents);
        TextSplitter splitter = TokenTextSplitter.builder().withChunkSize(100).withMaxNumChunks(400).build();
        vectorStore.add(splitter.split(documents));
    }
}
