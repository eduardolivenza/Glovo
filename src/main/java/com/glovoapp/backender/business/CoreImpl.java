package com.glovoapp.backender.business;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CoreImpl implements ICore{

    private final IOrderRepository orderRepository;
    private final ICourierRepository courierRepository;
    private OrderFilter filter;

    public CoreImpl( IOrderRepository orderRepository, ICourierRepository courierRepository, OrderFilter orderFilter){
        this.orderRepository = orderRepository;
        this.courierRepository = courierRepository;
        this.filter = orderFilter;
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> findByCourierId(String courierId) {
        Courier courier = courierRepository.findById(courierId);
        List<Order> orderList = filter.hideOrders(findAll(), courier);
        orderList = filter.priorizeOrders(orderList, courier);
        return new ArrayList<>(orderList);
    }

}
