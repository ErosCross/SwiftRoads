package com.model;

import java.util.ArrayList;
import java.util.List;

public class Intersection {
    private String id;
    private TrafficLight trafficLight;
    private double x;
    private double y;
    private List<Road> connectedRoads; // List of roads connected to this intersection

    public Intersection(String id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.connectedRoads = new ArrayList<>(); // Initialize the list of connected roads
    }

    public String getId() {
        return id;
    }

    public void setTrafficLight(TrafficLight trafficLight) {
        this.trafficLight = trafficLight;
    }

    public TrafficLight getTrafficLight() {
        return trafficLight;
    }

    public boolean hasTrafficLight() {
        return trafficLight != null;
    }

    public void updateTrafficLight() {
        if (trafficLight != null) {
            trafficLight.update();
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // Method to add a road to the list of connected roads
    public void addConnectedRoad(Road road) {
        if (road != null && !connectedRoads.contains(road)) {
            connectedRoads.add(road);
        }
    }

    // Method to get the list of connected roads
    public List<Road> getConnectedRoads() {
        return connectedRoads;
    }

    @Override
    public String toString() {
        return "Intersection{id='" + id + '\'' +
                ", x=" + x +
                ", y=" + y +
                (trafficLight != null ? ", light=" + trafficLight : "") +
                '}';
    }
}
