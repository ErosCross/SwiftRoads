package com.model;

public class Intersection {
    private String id;
    private TrafficLight trafficLight;
    private double x;
    private double y;

    public Intersection(String id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
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

    @Override
    public String toString() {
        return "Intersection{id='" + id + '\'' +
                ", x=" + x +
                ", y=" + y +
                (trafficLight != null ? ", light=" + trafficLight : "") +
                '}';
    }
}
