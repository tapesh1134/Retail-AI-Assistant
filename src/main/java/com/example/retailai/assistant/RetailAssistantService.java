package com.example.retailai.assistant;

import com.example.retailai.order.OrderTools;
import com.example.retailai.product.ProductTools;
import com.example.retailai.rag.PolicyRetriever;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class RetailAssistantService {

    private static final String SYSTEM_PROMPT = """
            You are a concise retail customer support assistant.

            You support:
            1. Order status and delivery questions.
            2. Returns, refunds and warranty policy questions.
            3. Product compatibility questions.

            Rules:
            - Never invent order information.
            - For order questions, use the getOrderStatus tool.
            - For product compatibility questions, use the checkCompatibility tool.
            - For returns, refunds and warranty questions, use the supplied policy context.
            - If the policy context does not answer the question, say that the policy information is insufficient.
            - If an order is not found, say that the order could not be found.
            - Keep answers short and natural.
            - For voice, prefer short sentences.
            """;

    private final ChatClient chatClient;
    private final OrderTools orderTools;
    private final ProductTools productTools;
    private final PolicyRetriever policyRetriever;

    public RetailAssistantService(
            ChatClient.Builder chatClientBuilder,
            OrderTools orderTools,
            ProductTools productTools,
            PolicyRetriever policyRetriever
    ) {
        this.chatClient = chatClientBuilder.build();
        this.orderTools = orderTools;
        this.productTools = productTools;
        this.policyRetriever = policyRetriever;
    }

    public String chat(String message) {

        String policyContext = retrievePolicyContext(message);

        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(buildPrompt(message, policyContext))
                .tools(orderTools, productTools)
                .call()
                .content();
    }

    /*
     * Temporary compatibility solution.
     *
     * Do NOT use ChatClient.stream() with the current
     * Capgemini OpenAI-compatible endpoint + Spring AI 1.0.9.
     *
     * The gateway returns:
     *
     * data: {...}
     *
     * and Spring AI 1.0.9 attempts to parse the entire
     * line as JSON.
     */
    public Flux<String> stream(String message) {

        return Flux.defer(() -> {

            String answer = chat(message);

            if (answer == null || answer.isBlank()) {
                return Flux.empty();
            }

            return Flux.just(answer);
        });
    }

    private String retrievePolicyContext(String message) {

        try {

            String policyContext =
                    policyRetriever.retrieve(message);

            if (policyContext == null ||
                    policyContext.isBlank()) {

                return "No relevant policy information found.";
            }

            return policyContext;

        } catch (Exception exception) {

            return "Policy information is currently unavailable.";
        }
    }

    private String buildPrompt(
            String message,
            String policyContext
    ) {

        return """
                Customer question:
                %s

                Relevant store policy context:
                %s
                """.formatted(
                message,
                policyContext
        );
    }
}