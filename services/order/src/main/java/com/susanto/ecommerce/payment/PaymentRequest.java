package com.susanto.ecommerce.payment;

import com.susanto.ecommerce.customer.CustomerResponse;
import com.susanto.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
