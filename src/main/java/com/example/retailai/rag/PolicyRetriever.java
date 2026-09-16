package com.example.retailai.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PolicyRetriever {
    private final VectorStore vectorStore;

    public PolicyRetriever(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public String retrieve(String question) {
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(question)
                        .topK(4)
                        .build()
        );

        if (docs == null || docs.isEmpty()) {
            return "No relevant store policy was found.";
        }

        return docs.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));
    }
}
