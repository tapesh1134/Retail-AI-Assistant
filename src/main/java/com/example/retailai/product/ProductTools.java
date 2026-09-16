package com.example.retailai.product;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class ProductTools {

    @Tool(description = "Check demo product compatibility. Use when the customer asks whether one product works with another product.")
    public CompatibilityResult checkCompatibility(
            @ToolParam(description = "Product ID, for example CHARGER-65W") String productId,
            @ToolParam(description = "Target product ID, for example LAPTOP-USB-C") String targetProductId) {

        String p = productId.trim().toUpperCase();
        String t = targetProductId.trim().toUpperCase();

        if (p.equals("CHARGER-65W") && t.equals("LAPTOP-USB-C")) {
            return new CompatibilityResult(true, p, t,
                    "The 65W USB-C Power Delivery charger is compatible with the demo USB-C laptop.");
        }

        if (p.equals("CHARGER-20W") && t.equals("LAPTOP-USB-C")) {
            return new CompatibilityResult(false, p, t,
                    "The demo laptop requires a 65W USB-C PD charger for normal charging.");
        }

        return new CompatibilityResult(false, p, t,
                "Compatibility is unknown for these demo product IDs. Do not assume compatibility.");
    }
}
