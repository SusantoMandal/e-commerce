package com.susanto.ecommerce.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PurchaseRequest(
        @NotNull(message = "Product is mandotory")
        Integer productId,
        @Positive(message = "Quantity is mandotory")
        double quantity
) {
}
