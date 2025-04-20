package com.io;

import com.model.CityMap;
import com.model.Intersection;
import com.model.Road;
import com.model.OneWayRoad;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CityLoader {

    // Load intersections from a CSV file (id,x,y)
    private static void loadIntersectionsFromCSV(String intersectionsFilePath, CityMap cityMap) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(intersectionsFilePath));
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            String[] parts = line.split(",");
            if (parts.length == 3) {
                String id = parts[0];
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);

                cityMap.addIntersection(id, x, y);
            }
        }
        reader.close();
    }

    // Load city (roads and intersections) from CSV files
    public static CityMap loadCityFromCSV(String roadsFilePath, String intersectionsFilePath) throws IOException {
        CityMap cityMap = new CityMap();

        // Load intersections first
        loadIntersectionsFromCSV(intersectionsFilePath, cityMap);

        // Now load the roads
        BufferedReader reader = new BufferedReader(new FileReader(roadsFilePath));
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

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
                    Road road = isOneWay
                            ? new OneWayRoad(fromIntersection, toIntersection, length, isBlocked)
                            : new Road(fromIntersection, toIntersection, length, isBlocked);

                    cityMap.addRoad(road);
                }
            }
        }

        reader.close();
        return cityMap;
    }
}
