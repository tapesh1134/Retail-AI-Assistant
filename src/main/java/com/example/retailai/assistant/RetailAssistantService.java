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
            PolicyRetriever policyRetriever) {
        this.chatClient = chatClientBuilder.build();
        this.orderTools = orderTools;
        this.productTools = productTools;
        this.policyRetriever = policyRetriever;
    }

    public String answer(String message) {
        String policyContext = policyRetriever.retrieve(message);

        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user("""
                    Customer question:
                    %s

                    Relevant store policy context:
                    %s
                    """.formatted(message, policyContext))
                .tools(orderTools, productTools)
                .call()
                .content();
    }

    public Flux<String> stream(String message) {
        String policyContext = policyRetriever.retrieve(message);

        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user("""
                    Customer question:
                    %s

                    Relevant store policy context:
                    %s
                    """.formatted(message, policyContext))
                .tools(orderTools, productTools)
                .stream()
                .content();
    }
}
