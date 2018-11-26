package com.glovoapp.backender.business;

public class CourierNotFoundException extends Throwable {

    public CourierNotFoundException(String courierID)
    {
        super("Courier " + courierID + " not found in our system");
    }
}
