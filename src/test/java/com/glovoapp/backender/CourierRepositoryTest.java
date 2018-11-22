package com.glovoapp.backender;

import com.glovoapp.backender.business.Courier;
import com.glovoapp.backender.business.Location;
import com.glovoapp.backender.business.Vehicle;
import com.glovoapp.backender.repositories.CourierRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CourierRepositoryTest {

    @Test
    public void findOneExistingTest() {
        Courier courier = new CourierRepository().findById("courier-1");
        Courier expected = new Courier().withId("courier-1")
                .withBox(true)
                .withName("Manolo Escobar")
                .withVehicle(Vehicle.MOTORCYCLE)
                .withLocation(new Location(41.4034422, 2.1894946));

        assertEquals(expected, courier);
    }

    @Test
    public void findOneNotExistingTest() {
        Courier courier = new CourierRepository().findById("bad-courier-id");
        assertNull(courier);
    }

    @Test
    public void findAll() {
        List<Courier> all = new CourierRepository().findAll();
        assertFalse(all.isEmpty());
    }
}