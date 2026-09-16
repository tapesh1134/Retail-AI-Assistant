package com.example.retailai.order;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class OrderTools {
    private final OrderRepository repository;

    public OrderTools(OrderRepository repository) {
        this.repository = repository;
    }

    @Tool(description = "Get the current status and delivery information for a retail order. Use this tool whenever the customer asks about an order status, shipment, tracking, or delivery.")
    public OrderToolResult getOrderStatus(
            @ToolParam(description = "The order number, for example 10001") String orderNumber) {

        return repository.findByOrderNumber(orderNumber.trim())
                .map(order -> new OrderToolResult(
                        order.getOrderNumber(),
                        order.getStatus(),
                        order.getCarrier(),
                        order.getTrackingNumber(),
                        order.getEstimatedDelivery(),
                        order.getTotalAmount(),
                        null))
                .orElseGet(() -> new OrderToolResult(
                        orderNumber,
                        null,
                        null,
                        null,
                        null,
                        null,
                        "Order not found"));
    }
}
