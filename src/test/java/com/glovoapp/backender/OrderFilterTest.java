package com.glovoapp.backender;

import com.glovoapp.backender.business.*;
import com.glovoapp.backender.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderFilterTest {

    Location francescMacia = new Location(41.3925603, 2.1418532);
    Location placaCatalunya = new Location(41.3870194,2.1678584);
    Location torreAgbar  = new Location(41.4034422,2.1894946);
    Location forum  = new Location(41.4112736,2.225997);

    private OrderFilter orderFilter;
    private List<Order> orders;

    @BeforeEach
    public void setUp(){
        orderFilter = new OrderFilter(5, new String[]{"burguer"}, new String[]{"vip", "food"});
        OrderRepository repository = new OrderRepository();
        orders = repository.findAll();
    }

    @Test
    public void hideOrdersTest() {
        Courier courier = new Courier().withBox(true).withVehicle(Vehicle.MOTORCYCLE).withLocation(forum);
        List<Order> returnedList = orderFilter.hideOrders(orders,  courier );
        assertEquals(returnedList.size(),6);
        courier.withVehicle(Vehicle.BICYCLE).withLocation(placaCatalunya);
        returnedList = orderFilter.hideOrders(orders,  courier );
        assertEquals(returnedList.size(),3); // others orders are further
        // Now we will test with a far courier
        courier.withLocation(forum);
        returnedList = orderFilter.hideOrders(orders,  courier );
        assertEquals(returnedList.size(),0); // orders are so far from this courier
        // we modify maximum distance where we can go by bicycle doing a new instance of the filter and we try using same courier
        orderFilter = new OrderFilter(10, new String[]{"burguer"}, new String[]{"vip", "food"});
        returnedList = orderFilter.hideOrders(orders,  courier );
        assertEquals(returnedList.size(),4); // now we notice that 4 orders are less than 10km from us
        // if we modify to 20, now all order should appear
        orderFilter = new OrderFilter(20, new String[]{"burguer"}, new String[]{"vip", "food"});
        returnedList = orderFilter.hideOrders(orders,  courier );
        assertEquals(returnedList.size(),6); // now we notice that 4 orders are less than 10km from us
        // finally we test same query but with the courier without box. it should be 5 because one order contains burguer
        courier.withBox(false);
        returnedList = orderFilter.hideOrders(orders,  courier );
        assertEquals(returnedList.size(),5);
    }

    @Test
    public void priorizeOrdersTest() {
    }

    @Test
    public void priorizeOrdersFoodTest() {
    }

}
