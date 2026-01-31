package com.susanto.ecommerce.orderline;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderLineService {

    private final OrderLineRepository repository;
    private final OrderLineMapper mapper;

    public Integer saveOrderLine(OrderLineRequest orderLineRequest) {
        var orderline = mapper.toOrderLine(orderLineRequest);
        return repository.save(orderline).getId();
    }

    public List<OrderLineResponse> findAllByOrderId(Integer orderId) {
       return repository.findAllByOrderId(orderId)
               .stream()
               .map(mapper::toOrderLineResponse)
               .toList();
    }
}
