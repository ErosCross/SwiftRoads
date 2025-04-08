package com.io;

import com.model.CityMap;
import com.model.Intersection;
import com.model.Road;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CityLoader {

    // Method to load intersections from a CSV file (with coordinates)
    private static Map<String, Intersection> loadIntersectionsFromCSV(String intersectionsFilePath, CityMap cityMap) throws IOException {
        Map<String, Intersection> intersections = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(intersectionsFilePath));
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            // Skip empty lines or comments
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            String[] parts = line.split(",");
            if (parts.length == 3) {
                String id = parts[0];
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);

                // Use cityMap to add intersections with coordinates
                cityMap.addIntersection(id, x, y); // Adding intersection with coordinates directly to cityMap
            }
        }
        reader.close();
        return intersections;
    }

    // Method to load city and roads from CSV
    public static CityMap loadCityFromCSV(String roadsFilePath, String intersectionsFilePath) throws IOException {
        CityMap cityMap = new CityMap();

        // Load intersections first, passing the cityMap to be populated
        loadIntersectionsFromCSV(intersectionsFilePath, cityMap);

        // Read the roads file and add roads to the city
        BufferedReader reader = new BufferedReader(new FileReader(roadsFilePath));
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            // Skip empty lines or comments
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            String[] parts = line.split(",");
            if (parts.length == 5) {
                String from = parts[0];
                String to = parts[1];
                double length = Double.parseDouble(parts[2]);
                boolean isBlocked = Boolean.parseBoolean(parts[3]);
                boolean isOneWay = Boolean.parseBoolean(parts[4]);


                Intersection fromIntersection = cityMap.getIntersectionById(from);
                Intersection toIntersection = cityMap.getIntersectionById(to);

                if (fromIntersection != null && toIntersection != null) {
                    Road road = new Road(fromIntersection, toIntersection, length, isBlocked, isOneWay);
                    cityMap.addRoad(road); // Add the road to the cityMap
                }
            }
        }

        reader.close();
        return cityMap;
    }
}
