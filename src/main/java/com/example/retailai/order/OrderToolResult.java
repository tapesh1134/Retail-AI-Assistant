package com.example.retailai.order;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderToolResult(
        String orderNumber,
        String status,
        String carrier,
        String trackingNumber,
        LocalDate estimatedDelivery,
        BigDecimal totalAmount,
        String error
) {}
