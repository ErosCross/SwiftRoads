package com.simulation;

import com.model.CityMap;
import com.model.Road;
import java.util.List;
import java.util.Random;

public class WeightSimulator {
    private CityMap cityMap;
    private static final int MAX_VEHICLES = 50; // Maximum number of vehicles to generate
    private static final Random random = new Random();

    public WeightSimulator(CityMap cityMap) {
        this.cityMap = cityMap;
    }

    // Generate a random number of vehicles for each road and update weights
    public void simulateTraffic() {
        for (Road road : cityMap.getRoads()) {
            // Randomly assign vehicles to roads, ensuring it makes sense logically
            int vehicleCount = random.nextInt(MAX_VEHICLES); // Random vehicle count
            for (int i = 0; i < vehicleCount; i++) {
                road.addVehicle(); // Add vehicle to the road
            }

            // Calculate and print road weight after vehicles are added
            System.out.println("Road: " + road.getSource().getId() + " -> " + road.getDestination().getId());
            System.out.println("Weight: " + road.calculateWeight());
        }
    }

    // Remove vehicles from roads logically (could be based on vehicle travel or a time step)
    public void clearVehicles() {
        for (Road road : cityMap.getRoads()) {
            road.removeVehicle(); // Clear the vehicle count for each road (simulation step)
        }
    }
}
