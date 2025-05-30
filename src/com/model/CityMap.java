package com.model;

import java.util.*;

public class CityMap {
    private final Map<String, Intersection> intersections = new HashMap<>();
    private final List<Road> roads = new ArrayList<>();

    // Method to add a road to the city map
    public void addRoad(Road road) {
        roads.add(road);
    }

    // Method to add an intersection to the map
    public void addIntersection(String id, double x, double y) {
        intersections.put(id, new Intersection(id, x, y));
    }

    // Method to get an intersection by its name (new method)
    public Intersection getIntersectionByName(String name) {
        for (Intersection intersection : intersections.values()) {
            if (intersection.getId().equalsIgnoreCase(name)) {
                return intersection;
            }
        }
        return null; // Return null if no intersection is found by that name
    }



    // Method to get all roads in the city
    public List<Road> getRoads() {
        return roads;
    }

    // Method to get all intersections in the city
    public Collection<Intersection> getIntersections() {
        return intersections.values();
    }

    // Method to get an intersection by its ID
    public Intersection getIntersectionById(String id) {
        return intersections.get(id);
    }
}
