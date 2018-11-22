package com.glovoapp.backender.business;

import java.util.Comparator;

public class OrderDistanceComparator implements Comparator<Order>
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
