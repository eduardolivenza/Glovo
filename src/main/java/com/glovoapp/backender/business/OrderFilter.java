package com.glovoapp.backender.business;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.partitioningBy;

@Component
public class OrderFilter {

    private final double maxDistance;
    private final String[] requiredBoxWords;
    private final String[] prioritize;
    private final double slotSize;

    public OrderFilter(@Value("${distances.maxDistance}") double maxDistance, @Value("${distances.slot}") double slotSize,  @Value("${boxRequired}") String[] requiredBoxWords, @Value("${prioritize}") String[] prioritize)
    {
        this.requiredBoxWords = requiredBoxWords;
        this.prioritize = prioritize;
        this.maxDistance = maxDistance;
        this.slotSize = slotSize;
    }

    public List<Order> hideOrders(List<Order> all, Courier courier) {
        List<Order> filteredList = filterByBox(all, courier);
        return filterByDistance(filteredList, courier);
    }

    public List<Order> priorizeOrders(List<Order> orderList, Courier courier){
        HashMap<Integer, List<Order>> slotsTable = new HashMap<Integer, List<Order>>();
        for(Order o: orderList)
        {
            double distance = DistanceCalculator.calculateDistance(courier.getLocation(), o.getPickup());
            int slot = (int) (distance / slotSize);
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

}
