package com.susanto.ecommerce.order;

import com.susanto.ecommerce.customer.CustomerClient;
import com.susanto.ecommerce.exception.BusinessException;
import com.susanto.ecommerce.orderline.OrderLineRequest;
import com.susanto.ecommerce.orderline.OrderLineService;
import com.susanto.ecommerce.product.ProductClient;
import com.susanto.ecommerce.product.PurchaseRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;

    public Integer createOrder(@Valid OrderRequest request) {
        // check customer exits (Feign client to call customer-ms)
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(()-> new BusinessException("Cannot create order:: No customer exists with provided ID:: %s" + request.customerId()));

        //purchase the product (RestTemplate to call product-ms)
        this.productClient.purchaseProducts(request.products());

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

        //TODO: Start payment (payment-ms)

        //TODO: send order confirmation (notification-ms)

        return null;
    }
}
