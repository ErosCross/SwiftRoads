package com.io;
import com.model.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TrafficLightLoader {

    public static void loadTrafficLightsFromCSV(String filePath, CityMap cityMap) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        // Skip the header
        reader.readLine();

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length < 3) continue;

            String intersectionId = parts[0].trim();
            int greenDuration = Integer.parseInt(parts[1].trim());
            int redDuration = Integer.parseInt(parts[2].trim());

            Intersection intersection = cityMap.getIntersectionById(intersectionId);

            if (intersection != null) {
                TrafficLight trafficLight = new TrafficLight(greenDuration, redDuration);
                intersection.setTrafficLight(trafficLight);
            } else {
                System.out.println("Warning: Intersection '" + intersectionId + "' not found in city map.");
            }
        }

        reader.close();
    }
}