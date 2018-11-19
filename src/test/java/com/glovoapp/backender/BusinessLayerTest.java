package com.glovoapp.backender;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BusinessLayerTest {

    private BusinessLayer businessLayer;

    @BeforeEach
    void setUp(){
        this.businessLayer = new BusinessLayer(5, new String[]{"pizza", "cake", "flamingo"}, new String[]{"vip", "food"}, new OrderRepository(), new CourierRepository());
    }

    @Test
    void findByCourierId() {

        List<Order> returnedList = businessLayer.findByCourierId("courier-1");
        assertEquals(4, returnedList.size());
        returnedList = businessLayer.findByCourierId("courier-2");
        assertEquals(2, returnedList.size());
        returnedList = businessLayer.findByCourierId("courier-3");
        assertEquals(2, returnedList.size());
        returnedList = businessLayer.findByCourierId("courier-4");
        assertEquals(3, returnedList.size());


    }
}
