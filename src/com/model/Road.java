package com.model;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Road {
    private static final double SCALE_FACTOR = 2.5;
    private boolean highlighted;
    protected Intersection source;
    protected Intersection destination;
    protected double length;
    protected boolean isBlocked;
    protected int vehicleCount;
    protected String style;  // New property to store the style of the road

    public Road(Intersection source, Intersection destination, double length, boolean isBlocked) {
        this.source = source;
        this.destination = destination;
        this.length = length;
        this.isBlocked = isBlocked;
        this.vehicleCount = 0;
        this.style = ""; // Default style is an empty string (no style)
        this.highlighted = false;
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
        return false; // Default for base class
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

    public double calculateWeight() {
        if (isBlocked) {
            return Double.POSITIVE_INFINITY;
        }

        double trafficFactor = 1 + (vehicleCount / 10.0);


        double trafficLightFactor = 1.0;
        if (source.hasTrafficLight() || destination.hasTrafficLight()) {
            trafficLightFactor = 1.5;
        }

        return length * trafficFactor  * trafficLightFactor;
    }

    public double[] getScaledCoordinates(double scaleFactor) {
        double sourceX = source.getX() * scaleFactor;
        double sourceY = source.getY() * scaleFactor;
        double destX = destination.getX() * scaleFactor;
        double destY = destination.getY() * scaleFactor;

        return new double[]{sourceX, sourceY, destX, destY};
    }

    public double getStyleWidth() {
        if (isBlocked()) {
            return 9 * SCALE_FACTOR; // Blocked roads are thicker and red (as per the earlier code)
        }

        // Use thinner line for one-way roads
        if (this instanceof OneWayRoad) {
            return 4 * SCALE_FACTOR; // One-way roads have thinner lines
        }

        return 9 * SCALE_FACTOR; // Default thickness for two-way roads
    }




    // New method to set the style of the road
    public void setStyle(String style) {
        this.style = style;
    }

    // Method to get the current style of the road
    public String getStyle() {
        return style;
    }

    public void reset() {
        this.isBlocked = false; // Reset the blocked status
        this.vehicleCount = 0;   // Reset the vehicle count
        this.style = "";         // Reset the style
    }

    public Paint getStyleColor() {
        if (isBlocked()) {
            return Color.RED; // Blocked roads are red
        }
        if (this.highlighted) {

            return Color.web("#00AD83");
        }
        // Adjust color based on vehicle count
        int vehicleCount = getVehicleCount();
        if (vehicleCount < 15) {
            return Color.web("#9ca3af"); // Light gray for low traffic
        } else if (vehicleCount < 60) {
            return Color.web("#f4c430"); // Yellow for moderate traffic
        } else {
            return Color.web("#ff4d6d"); // Red for heavy traffic
        }
    }

    public void highlightRoad() {

        this.highlighted = !this.highlighted;



    }

    @Override
    public String toString() {
        return this.source.getId() + " -> " + this.destination.getId();
    }


}
