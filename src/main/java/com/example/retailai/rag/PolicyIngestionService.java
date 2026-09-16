package com.example.retailai.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class PolicyIngestionService implements CommandLineRunner {
    private final VectorStore vectorStore;

    public PolicyIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Document> documents = List.of(
                load("policies/returns.txt", "returns"),
                load("policies/refunds.txt", "refunds"),
                load("policies/warranty.txt", "warranty"),
                load("policies/compatibility.txt", "compatibility")
        );

        // For a demo, a small fixed policy set is enough.
        vectorStore.add(documents);
    }

    private Document load(String path, String type) throws Exception {
        String text = new String(
                new ClassPathResource(path).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );
        return new Document(text, java.util.Map.of("policyType", type));
    }
}
