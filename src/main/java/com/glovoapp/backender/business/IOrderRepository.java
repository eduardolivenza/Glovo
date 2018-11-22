package com.glovoapp.backender.business;

import java.util.List;

// this interface allows us to forget about the repository implementation
public interface IOrderRepository {

    List<Order> findAll();
}
