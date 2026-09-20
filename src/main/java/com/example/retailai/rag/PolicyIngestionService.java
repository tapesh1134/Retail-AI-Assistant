package com.example.retailai.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class PolicyIngestionService implements CommandLineRunner {

    private static final Logger log =
            LoggerFactory.getLogger(
                    PolicyIngestionService.class
            );

    private final VectorStore vectorStore;

    public PolicyIngestionService(
            VectorStore vectorStore
    ) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(String... args) {

        try {

            List<Document> documents = List.of(
                    load(
                            "policies/returns.txt",
                            "returns"
                    ),
                    load(
                            "policies/refunds.txt",
                            "refunds"
                    ),
                    load(
                            "policies/warranty.txt",
                            "warranty"
                    ),
                    load(
                            "policies/compatibility.txt",
                            "compatibility"
                    )
            );

            vectorStore.add(documents);

            log.info(
                    "Policy documents successfully indexed."
            );

        } catch (Exception exception) {

            log.error(
                    "Policy ingestion failed. " +
                            "Application will continue without updated policy embeddings.",
                    exception
            );
        }
    }

    private Document load(
            String path,
            String type
    ) throws Exception {

        ClassPathResource resource =
                new ClassPathResource(path);

        String text;

        try (var inputStream =
                     resource.getInputStream()) {

            text = new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }

        return new Document(
                text,
                Map.of(
                        "policyType",
                        type
                )
        );
    }
}