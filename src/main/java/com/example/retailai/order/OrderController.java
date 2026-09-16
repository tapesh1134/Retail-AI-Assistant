package com.example.retailai.order;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderRepository repository;

    public OrderController(OrderRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{orderNumber}")
    public OrderToolResult getOrder(@PathVariable String orderNumber) {
        return repository.findByOrderNumber(orderNumber)
                .map(order -> new OrderToolResult(
                        order.getOrderNumber(),
                        order.getStatus(),
                        order.getCarrier(),
                        order.getTrackingNumber(),
                        order.getEstimatedDelivery(),
                        order.getTotalAmount(),
                        null))
                .orElse(new OrderToolResult(orderNumber, null, null, null, null, null, "Order not found"));
    }
}
