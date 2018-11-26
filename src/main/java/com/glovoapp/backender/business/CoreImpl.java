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
    /**
     * Method that implements the business logic behind the new endpoint
     */
    public List<Order> findByCourierId(String courierId) throws CourierNotFoundException {

        Courier courier = courierRepository.findById(courierId);
        // we check we really have a courier with this ID or if not we trhow an exception
        if (courier != null){
            // if courier exists we fins and organize all the orders that matches with it
            List<Order> orderList = filter.hideOrders(findAll(), courier);
            orderList = filter.priorizeOrders(orderList, courier);
            return new ArrayList<>(orderList);
        }else
        {
         throw new CourierNotFoundException(courierId);
        }
    }

}
