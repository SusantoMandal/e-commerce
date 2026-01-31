package com.susanto.ecommerce.order;

import com.susanto.ecommerce.product.PurchaseRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record OrderRequest(
        Integer id,
        String reference,
        @Positive(message = "Order amount should be positive")
        BigDecimal amount,
        @NotNull(message = "Payment method should be precised")
        PaymentMethod paymentMethod,
        @NotNull(message = "Customer method should be present")
        @NotEmpty(message = "Customer method should be present")
        @NotBlank(message = "Customer method should be present")
        String customerId,
        @NotEmpty(message = "You should atleast purchased one product")
        List<PurchaseRequest> products
) {
}
