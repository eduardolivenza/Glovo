package com.glovoapp.backender.business;

import com.glovoapp.backender.repositories.CourierRepository;
import com.glovoapp.backender.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoreImplTest {

    private CoreImpl coreImpl;

    @BeforeEach
    public void setUp(){
        OrderFilter orderFilter = new OrderFilter(5, 0.5, new String[]{"pizza", "cake"}, new String[]{"vip", "food"});
        this.coreImpl = new CoreImpl( new OrderRepository(), new CourierRepository(), orderFilter);
    }

    @Test
    public void findByCourierIdTest() throws CourierNotFoundException {

        // courier with box and motorbike. All orders are visible
        List<Order> returnedList = coreImpl.findByCourierId("courier-1");
        assertEquals(6, returnedList.size());
        // courier without box but with motorbike. All orders are visible except ones that contains pizza or cake (6 - 2 = 4)
        returnedList = coreImpl.findByCourierId("courier-2");
        assertEquals(4, returnedList.size());
        // courier with box but long distance orders are hidden from him -> just 3 are available
        returnedList = coreImpl.findByCourierId("courier-3");
        assertEquals(3, returnedList.size());
        // courier without box and without long term vehicle. Just two close order appears
        returnedList = coreImpl.findByCourierId("courier-4");
        assertEquals(2, returnedList.size());
    }
}
