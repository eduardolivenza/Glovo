package com.glovoapp.backender.business;

import com.glovoapp.backender.business.DistanceCalculator;
import com.glovoapp.backender.business.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DistanceCalculatorTest {
    @Test
    public void smokeTest() {
        Location francescMacia = new Location(41.3925603, 2.1418532);
        Location placaCatalunya = new Location(41.3870194,2.1678584);
        Location torreAgbar  = new Location(41.4034422,2.1894946);
        Location forum  = new Location(41.4112736,2.225997);
        Location campNou  = new Location(41.381089,2.120594);

        // More or less 2km from Francesc Macia to Placa Catalunya
        assertEquals(2.0, DistanceCalculator.calculateDistance(francescMacia, placaCatalunya), 0.3);
        assertEquals(9.25, DistanceCalculator.calculateDistance(campNou, forum), 0.3);
        assertEquals(3.0, DistanceCalculator.calculateDistance(torreAgbar, forum), 0.3);
    }

}