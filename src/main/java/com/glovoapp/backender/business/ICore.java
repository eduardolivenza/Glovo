package com.glovoapp.backender.business;

import java.util.List;

/*
   Interface used for define all the operations that will be available from the API
*/
public interface ICore {

    List<Order> findAll();

    List<Order> findByCourierId(String courierId) throws CourierNotFoundException;
}
