package com.model;


public class OneWayRoad extends Road {

    public OneWayRoad(Intersection source, Intersection destination, double length, boolean isBlocked) {
        super(source, destination, length, isBlocked);
    }

    @Override
    public boolean isOneWay() {
        return true;
    }

    @Override
    public double calculateWeight() {
        if (isBlocked) {
            return Double.POSITIVE_INFINITY;
        }

        double trafficFactor = 1 + (vehicleCount / 10.0);
        double directionFactor = 1.0; // One-way roads are faster

        double trafficLightFactor = 1.0;
        if (source.hasTrafficLight() || destination.hasTrafficLight()) {
            trafficLightFactor = 1.5;
        }

        return length * trafficFactor * directionFactor * trafficLightFactor;
    }

    // Override the setStyle method to apply custom style for OneWayRoad
    @Override
    public void setStyle(String style) {
        // Apply a specific style for one-way roads (e.g., blue color)
        super.setStyle(style + " -fx-stroke: blue; -fx-stroke-width: 3px;");
    }
}
