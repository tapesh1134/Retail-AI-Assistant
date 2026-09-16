package com.example.retailai.product;

public record CompatibilityResult(
        boolean compatible,
        String productId,
        String targetProductId,
        String reason
) {}
