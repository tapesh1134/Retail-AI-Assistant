package com.example.retailai.embedding;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary
public class CustomEmbeddingModel implements EmbeddingModel {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String model;
    private final int dimensions;

    public CustomEmbeddingModel(
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            @Value("${custom.embedding.url}") String url,
            @Value("${custom.embedding.api-key}") String apiKey,
            @Value("${custom.embedding.model}") String model,
            @Value("${custom.embedding.dimensions}") int dimensions) {

        this.restClient = restClientBuilder
                .baseUrl(url)
                .defaultHeader(
                        "Authorization",
                        "Bearer " + apiKey
                )
                .defaultHeader(
                        "Content-Type",
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();

        this.objectMapper = objectMapper;
        this.model = model;
        this.dimensions = dimensions;
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {

        List<String> inputs = request.getInstructions();

        if (inputs == null || inputs.isEmpty()) {
            throw new IllegalArgumentException(
                    "Embedding request must contain at least one input"
            );
        }

        List<Embedding> embeddings = new ArrayList<>();

        for (int i = 0; i < inputs.size(); i++) {

            String input = inputs.get(i);

            if (input == null || input.isBlank()) {
                throw new IllegalArgumentException(
                        "Embedding input must not be null or blank"
                );
            }

            CustomEmbeddingRequest body =
                    new CustomEmbeddingRequest(
                            model,
                            input,
                            dimensions
                    );

            String rawResponse = restClient
                    .post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);

            if (rawResponse == null || rawResponse.isBlank()) {
                throw new IllegalStateException(
                        "Custom embedding API returned an empty response"
                );
            }

            try {

                JsonNode root =
                        objectMapper.readTree(rawResponse);

                JsonNode dataNode =
                        root.get("data");

                if (dataNode == null
                        || !dataNode.isArray()
                        || dataNode.isEmpty()) {

                    throw new IllegalStateException(
                            "Custom embedding response does not contain " +
                                    "a valid 'data' array: " + rawResponse
                    );
                }

                JsonNode firstDataNode =
                        dataNode.get(0);

                if (firstDataNode == null
                        || !firstDataNode.isObject()) {

                    throw new IllegalStateException(
                            "Custom embedding response contains " +
                                    "an invalid data[0] object: " + rawResponse
                    );
                }

                JsonNode embeddingNode =
                        firstDataNode.get("embedding");

                if (embeddingNode == null
                        || !embeddingNode.isArray()
                        || embeddingNode.isEmpty()) {

                    throw new IllegalStateException(
                            "Custom embedding response does not contain " +
                                    "a valid 'data[0].embedding' array: " +
                                    rawResponse
                    );
                }

                float[] vector =
                        new float[embeddingNode.size()];

                for (int j = 0; j < embeddingNode.size(); j++) {

                    JsonNode valueNode =
                            embeddingNode.get(j);

                    if (valueNode == null
                            || !valueNode.isNumber()) {

                        throw new IllegalStateException(
                                "Invalid value at embedding index " + j
                        );
                    }

                    vector[j] =
                            (float) valueNode.asDouble();
                }

                if (vector.length != dimensions) {

                    throw new IllegalStateException(
                            "Embedding dimension mismatch. Expected "
                                    + dimensions
                                    + " but custom API returned "
                                    + vector.length
                    );
                }

                embeddings.add(
                        new Embedding(vector, i)
                );

            } catch (IllegalStateException e) {

                throw e;

            } catch (Exception e) {

                throw new IllegalStateException(
                        "Failed to parse custom embedding response",
                        e
                );
            }
        }

        return new EmbeddingResponse(embeddings);
    }

    @Override
    public float[] embed(String text) {

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "Text to embed must not be null or blank"
            );
        }

        EmbeddingRequest request =
                new EmbeddingRequest(
                        List.of(text),
                        null
                );

        return call(request)
                .getResults()
                .getFirst()
                .getOutput();
    }

    @Override
    public float[] embed(Document document) {

        if (document == null) {
            throw new IllegalArgumentException(
                    "Document must not be null"
            );
        }

        String text = document.getText();

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "Document text must not be null or blank"
            );
        }

        return embed(text);
    }

    @Override
    public int dimensions() {
        return dimensions;
    }

    public record CustomEmbeddingRequest(
            String model,
            String input,
            Integer dimensions
    ) {
    }
}