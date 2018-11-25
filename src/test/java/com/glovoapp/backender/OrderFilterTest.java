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
    Location campNou  = new Location(41.381089,2.120594);

    private OrderFilter orderFilter;
    private List<Order> orders;
    Courier courier;

    @BeforeEach
    public void setUp(){
        orderFilter = new OrderFilter(5, 0.5, new String[]{"burguer"}, new String[]{"vip", "food"});
        OrderRepository repository = new OrderRepository();
        orders = repository.findAll();
        courier = new Courier();
    }

    @Test
    public void hideOrdersTest() {
        courier.withBox(true).withVehicle(Vehicle.MOTORCYCLE).withLocation(forum);
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
        orderFilter = new OrderFilter(10, 0.5, new String[]{"burguer"}, new String[]{"vip", "food"});
        returnedList = orderFilter.hideOrders(orders,  courier );
        assertEquals(returnedList.size(),4); // now we notice that 4 orders are less than 10km from us
        // if we modify to 20, now all order should appear
        orderFilter = new OrderFilter(20, 0.5, new String[]{"burguer"}, new String[]{"vip", "food"});
        returnedList = orderFilter.hideOrders(orders,  courier );
        assertEquals(returnedList.size(),6); // now we notice that 4 orders are less than 10km from us
        // finally we test same query but with the courier without box. it should be 5 because one order contains burguer
        courier.withBox(false);
        returnedList = orderFilter.hideOrders(orders,  courier );
        assertEquals(returnedList.size(),5);
    }

    @Test
    public void priorizeOrdersVipTest() {
        courier = new Courier().withBox(true).withVehicle(Vehicle.MOTORCYCLE).withLocation(francescMacia);
        Order o1 = new Order().withVip(true).withFood(false).withDelivery(placaCatalunya).withPickup(francescMacia);
        Order o2 = new Order().withVip(true).withFood(false).withDelivery(placaCatalunya).withPickup(campNou);
        orders.add(o1);
        orders.add(o2);
        List<Order> returnedList = orderFilter.priorizeOrders(orders, courier);
        // vip order must be first because pickup and courier location are same, so it will be in first slot
        assertEquals(o1, returnedList.get(0));
        // in next two positions we return other 2 orders located in same place as first
        assertEquals("order-4", returnedList.get(1).getId());
        assertEquals("order-5", returnedList.get(2).getId());
        // in the first position of second slot we return the other VIP order
        assertEquals(o2, returnedList.get(3));
        // in third position we return order with ID 5 because its also in francesc macia (like courier)

    }

    @Test
    public void priorizeOrdersFoodTest() {
        courier = new Courier().withBox(true).withVehicle(Vehicle.MOTORCYCLE).withLocation(francescMacia);
        Order o1 = new Order().withVip(true).withFood(false).withDelivery(placaCatalunya).withPickup(francescMacia);
        Order o2 = new Order().withVip(false).withFood(true).withDelivery(placaCatalunya).withPickup(francescMacia);
        Order o3 = new Order().withVip(true).withFood(true).withDelivery(placaCatalunya).withPickup(campNou);
        orders.add(o3);
        orders.add(o1);
        orders.add(o2);
        orderFilter = new OrderFilter(5, 3, new String[]{}, new String[]{"food"});
        List<Order> returnedList = orderFilter.priorizeOrders(orders, courier);
        // Orders with food are o2 and o3
        assertEquals(o2, returnedList.get(0));
        assertEquals("order-2", returnedList.get(1).getId());
        assertEquals(o3, returnedList.get(2));
        // in next two positions we return other 2 orders located in same place as first from courier and the O1
        assertEquals("order-4", returnedList.get(3).getId());
        assertEquals("order-5", returnedList.get(4).getId());

    }

}
