package com.glovoapp.backender;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.partitioningBy;

@Component
public class BusinessLayer {

    private final OrderRepository orderRepository;
    private final CourierRepository courierRepository;
    private final double maxDistance;
    private final String[] requiredBoxWords;
    private final String[] prioritize;

    public BusinessLayer(@Value("${distances.maxDistance}") double maxDistance, @Value("${boxRequired}") String[] requiredBoxWords,  @Value("${prioritize}") String[] prioritize, OrderRepository orderRepository, CourierRepository courierRepository){
        this.maxDistance = maxDistance;
        this.orderRepository = orderRepository;
        this.courierRepository = courierRepository;
        this.requiredBoxWords = requiredBoxWords;
        this.prioritize = prioritize;
    }

    List<Order> findAll() {
        return orderRepository.findAll();
    }

    List<Order> findByCourierId(String courierId) {
        Courier courier = courierRepository.findById(courierId);
        List<Order> orderList = filterByBox(findAll(), courier);
        orderList = filterByDistance(orderList, courier);
        orderList = priorizeOrders(orderList, courier);
        return new ArrayList<>(orderList);
    }

    private List<Order> priorizeOrders(List<Order> orderList, Courier courier){
        HashMap<Integer, List<Order>> slotsTable = new HashMap<Integer, List<Order>>();
        for(Order o: orderList)
        {
            double distance = DistanceCalculator.calculateDistance(courier.getLocation(), o.getPickup());
            int slot = (int) (distance / 0.5);
            //<TODO> remove checkers
            o.slot = slot;
            o.pickupDistance = distance;
            //---------------------
            slotsTable.computeIfAbsent(slot, k -> new ArrayList<>()).add(o);
        }
        List<Order> finalList = new ArrayList<>();
        for (Integer key: slotsTable.keySet()){
            List<Order> others = slotsTable.get(key);
            for (String priority: prioritize)
            {
                switch (priority) {
                    case "vip":
                        Map<Boolean, List<Order>> priorisingVIP = others.stream()
                                .collect(partitioningBy(o -> o.getVip()));
                        List<Order> ordersWithVIP = priorisingVIP.get(true);
                        others = priorisingVIP.get(false);
                        ordersWithVIP.sort(new OrderDistanceComparator(courier));
                        finalList.addAll(ordersWithVIP);
                        break;
                    case "food":
                        Map<Boolean, List<Order>> priorisingFood = others.stream()
                                .collect(partitioningBy(o -> o.getFood()));
                        List<Order> ordersWithFood =priorisingFood.get(true);
                        others =priorisingFood.get(false);
                        ordersWithFood.sort(new OrderDistanceComparator(courier));
                        finalList.addAll(ordersWithFood);
                        break;
                }
            }
            others.sort(new OrderDistanceComparator(courier));
            finalList.addAll(others);
        }
        return finalList;
    }

    private List<Order> filterByBox(List<Order> ordersList, Courier courier) {
        List<Order> filteredList = ordersList;
        if (!courier.getBox()) {
            for (String word: requiredBoxWords){
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

    class OrderDistanceComparator implements Comparator<Order>
    {
        private Courier courier;

        OrderDistanceComparator(Courier c) {
            this.courier = c;
        }

        @Override
        public int compare(Order o1, Order o2) {
            Double distanceO1 = (Double) DistanceCalculator.calculateDistance(courier.getLocation(), o1.getPickup());
            Double distanceO2 = (Double) DistanceCalculator.calculateDistance(courier.getLocation(), o2.getPickup());
            return distanceO1.compareTo(distanceO2);
        }
    }


}
