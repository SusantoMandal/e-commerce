package com.susanto.ecommerce.kafka;

import com.susanto.ecommerce.customer.CustomerResponse;
import com.susanto.ecommerce.order.PaymentMethod;
import com.susanto.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
