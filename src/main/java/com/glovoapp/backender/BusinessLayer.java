package com.glovoapp.backender;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BusinessLayer {

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final double maxDistance;
    private final String[] requiredBoxWords;

    public BusinessLayer(@Value("${distances.maxDistance}") double maxDistance, @Value("${boxRequired}") String[] requiredBoxWords, OrderRepository orderRepository, CourierRepository courierRepository){
        this.maxDistance = maxDistance;
        this.orderRepository = orderRepository;
        this.courierRepository = courierRepository;
        this.requiredBoxWords = requiredBoxWords;
    }

    List<Order> findAll() {
        return orderRepository.findAll();
    }

    List<Order> findByCourierId(String courierId) {
        Courier courier = courierRepository.findById(courierId);
        List<Order> orderList = filterByBox(findAll(), courier);
        orderList = filterByDistance(orderList, courier);
        
        //Collections.sort(orderList, (o1, o2)  -> DistanceCalculator.calculateDistance(courier.getLocation(), o1.getPickup()) > DistanceCalculator.calculateDistance(courier.getLocation(), o2.getPickup()));
        return new ArrayList<>(orderList);
    }

    private List<Order> filterByBox(List<Order> ordersList, Courier courier) {
        List<Order> filteredList = ordersList;
        if (!courier.getBox())
        {
            for (String word: requiredBoxWords)
            {
                filteredList = filteredList.stream().filter(order -> !(order.getDescription().toLowerCase().contains(word))).collect(Collectors.toList());
            }
        }
        return filteredList;
    }

    private List<Order> filterByDistance(List<Order> ordersList, Courier courier) {
        List<Order> filteredList = ordersList;
        if (!courier.getVehicle().equals(Vehicle.MOTORCYCLE) || equals(Vehicle.ELECTRIC_SCOOTER))
        {
            filteredList = ordersList.stream().filter(order -> (DistanceCalculator.calculateDistance(courier.getLocation(), order.getPickup()) + DistanceCalculator.calculateDistance(order.getPickup(), order.getDelivery())) < maxDistance).collect(Collectors.toList());
        }
        return filteredList;
    }

}
