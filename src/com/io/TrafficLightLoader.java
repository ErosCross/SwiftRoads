package com.io;
import com.model.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TrafficLightLoader {

    // Constant durations for all traffic lights
    private static final int GREEN_DURATION = 30;  // 30 seconds for green
    private static final int YELLOW_DURATION = 5;  // 5 seconds for yellow
    private static final int RED_DURATION = 45;    // 45 seconds for red

    public static void loadTrafficLightsFromCSV(String filePath, CityMap cityMap) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        // Skip the header
        reader.readLine();

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length < 1) continue;  // Ensure there's at least an intersection ID

            String intersectionId = parts[0].trim();

            // Get the intersection by ID from the city map
            Intersection intersection = cityMap.getIntersectionById(intersectionId);

            if (intersection != null) {
                // Set the traffic light with constant durations
                TrafficLight trafficLight = new TrafficLight(GREEN_DURATION, YELLOW_DURATION, RED_DURATION);
                intersection.setTrafficLight(trafficLight);
            } else {
                System.out.println("Warning: Intersection '" + intersectionId + "' not found in city map.");
            }
        }

        reader.close();
    }
}
