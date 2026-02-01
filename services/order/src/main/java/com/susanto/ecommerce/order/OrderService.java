package com.susanto.ecommerce.order;

import com.susanto.ecommerce.customer.CustomerClient;
import com.susanto.ecommerce.exception.BusinessException;
import com.susanto.ecommerce.order.kafka.OrderCofirmation;
import com.susanto.ecommerce.order.kafka.OrderProducer;
import com.susanto.ecommerce.orderline.OrderLineRequest;
import com.susanto.ecommerce.orderline.OrderLineService;
import com.susanto.ecommerce.payment.PaymentClient;
import com.susanto.ecommerce.payment.PaymentRequest;
import com.susanto.ecommerce.product.ProductClient;
import com.susanto.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;

    public Integer createOrder(@Valid OrderRequest request) {
        // check customer exits (Feign client to call customer-ms)
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(()-> new BusinessException("Cannot create order:: No customer exists with provided ID:: %s" + request.customerId()));

        //purchase the product (RestTemplate to call product-ms)
        var purchaseProducts = this.productClient.purchaseProducts(request.products());

        //persist the order
        var order = this.repository.save(mapper.toOrder(request));

        //persist order line
        for (PurchaseRequest purchaseRequest: request.products()) {
             orderLineService.saveOrderLine(
                     new OrderLineRequest(
                             null,
                             order.getId(),
                             purchaseRequest.productId(),
                             purchaseRequest.quantity()
                     )
             );
        }

        //Start payment (payment-ms)
        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);

        //send order confirmation by Kafka (notification-ms)
        orderProducer.sendOrderConfirmation(
                new OrderCofirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchaseProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .toList();
    }

    public OrderResponse findById(Integer orderId) {
        return repository.findById(orderId)
                .map(mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", orderId)));
    }
}
