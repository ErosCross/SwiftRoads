package com.simulation;

import com.model.CityMap;
import com.model.Road;
import java.util.List;
import java.util.Random;

public class WeightSimulator {
    private CityMap cityMap;
    private static final int INITIAL_VEHICLES = 25; // Default vehicles for most roads
    private static final int SPECIAL_VEHICLES = 70; // Special case: 70 vehicles for A -> C
    private static final int MAX_CHANGE = 5; // Max number of vehicles to add or remove per step
    private static final Random random = new Random();


    public WeightSimulator(CityMap cityMap) {
        this(cityMap, true); // Default: initialize traffic
    }

    public WeightSimulator(CityMap cityMap, boolean initializeTraffic) {
        this.cityMap = cityMap;
        if (initializeTraffic) {
            initializeTraffic();
        }
    }

    // Initialize traffic
    private void initializeTraffic() {
        for (Road road : cityMap.getRoads()) {
            int vehiclesToAdd = INITIAL_VEHICLES;
            if (road.getSource().getId().equals("A") && road.getDestination().getId().equals("C")) {
                vehiclesToAdd = SPECIAL_VEHICLES;
            }



            for (int i = 0; i < vehiclesToAdd; i++) {
                road.addVehicle();
            }
        }
    }

    // Simulate adding or removing random number of vehicles
    public void simulateTraffic() {
        for (Road road : cityMap.getRoads()) {
            int change = random.nextInt(MAX_CHANGE + 1);
            boolean addVehicles = random.nextBoolean();
            if (addVehicles) {
                for (int i = 0; i < change; i++) {
                    road.addVehicle();
                }
            } else {
                for (int i = 0; i < change; i++) {
                    road.removeVehicle();
                }
            }

            //System.out.println("Road: " + road.getSource().getId() + " -> " + road.getDestination().getId());
            //System.out.println("Vehicles: " + road.getVehicleCount());
            //System.out.println("Weight: " + road.calculateWeight());
        }
    }


}
