package com.glovoapp.backender;

import com.glovoapp.backender.business.CourierNotFoundException;
import com.glovoapp.backender.business.ICore;
import com.glovoapp.backender.business.Location;
import com.glovoapp.backender.business.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class APITest {

    @Mock
    private ICore mockCore;
    private API api;
    Location placaCatalunya = new Location(41.3870194,2.1678584);
    Location torreAgbar  = new Location(41.4034422,2.1894946);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        api = new API("welcome", mockCore);
    }


    /**
     * Test used to check the new endpoint
     */
    @Test
    public void ordersForCourierTest() throws CourierNotFoundException {

        String courier1 = "courier1";
        String courier2 = "courier2";
        List<Order> fakeList = new ArrayList<Order>();
        fakeList.add(new Order().withId("O1").withDescription("FakeOrder 1").withFood(true).withPickup(placaCatalunya).withDelivery(torreAgbar));
        fakeList.add(new Order().withId("O2").withDescription("FakeOrder 2").withFood(true).withPickup(torreAgbar).withDelivery(placaCatalunya));
        when(mockCore.findByCourierId(courier1)).thenReturn(fakeList);
        when(mockCore.findByCourierId(courier2)).thenThrow(new CourierNotFoundException(courier2));
        List<OrderVM> returnedOrders = api.ordersForCourier(courier1);
        assertEquals(returnedOrders.get(0).getDescription(), "FakeOrder 1");
        assertEquals(returnedOrders.get(1).getDescription(), "FakeOrder 2");
        returnedOrders = api.ordersForCourier(courier2);
        assertEquals(returnedOrders, null);
    }
}
