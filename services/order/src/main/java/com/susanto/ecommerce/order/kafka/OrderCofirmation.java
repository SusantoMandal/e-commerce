package com.susanto.ecommerce.order.kafka;

import com.susanto.ecommerce.customer.CustomerResponse;
import com.susanto.ecommerce.order.PaymentMethod;
import com.susanto.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderCofirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
