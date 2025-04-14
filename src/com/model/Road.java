package com.model;

public class Road {
    private Intersection source;
    private Intersection destination;
    private double length;
    private boolean isOneWay;
    private boolean isBlocked;
    private int vehicleCount; // Number of vehicles on the road

    public Road(Intersection source, Intersection destination, double length, boolean isOneWay, boolean isBlocked) {
        this.source = source;
        this.destination = destination;
        this.length = length;
        this.isOneWay = isOneWay;
        this.isBlocked = isBlocked;
        this.vehicleCount = 0; // Initially no vehicles
    }

    public Intersection getSource() {
        return source;
    }

    public Intersection getDestination() {
        return destination;
    }

    public double getLength() {
        return length;
    }

    public boolean isOneWay() {
        return isOneWay;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public int getVehicleCount() {
        return vehicleCount;
    }

    public void addVehicle() {
        this.vehicleCount++;
    }

    public void removeVehicle() {
        if (this.vehicleCount > 0) {
            this.vehicleCount--;
        }
    }


    // Calculate the weight of the road based on vehicles, blockage, length, and one-way status
    public double calculateWeight() {
        if (isBlocked) {
            return Double.POSITIVE_INFINITY; // Blocked roads have infinite weight
        }

        double trafficFactor = 1 + (vehicleCount / 10.0); // Traffic factor based on vehicles (adjust /10 based on road capacity)
        double directionFactor = isOneWay ? 1 : 1.5; // One-way roads are faster

        // Check if either source or destination intersection has a traffic light
        double trafficLightFactor = 1.0;
        if (source.hasTrafficLight() || destination.hasTrafficLight()) {
            trafficLightFactor = 1.5; // Increase weight if any intersection has a traffic light
        }

        return length * trafficFactor * directionFactor * trafficLightFactor;
    }

    // Method to return the scaled coordinates of the source and destination intersections
    public double[] getScaledCoordinates(double scaleFactor) {
        double sourceX = source.getX() * scaleFactor;
        double sourceY = source.getY() * scaleFactor;
        double destX = destination.getX() * scaleFactor;
        double destY = destination.getY() * scaleFactor;

        return new double[]{sourceX, sourceY, destX, destY};
    }
}
