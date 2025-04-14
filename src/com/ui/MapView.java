package com.ui;

import com.model.CityMap;
import com.model.Intersection;
import com.model.Road;
import com.model.TrafficLight;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.Random;

public class MapView extends Canvas {
    private static final double RADIUS = 18; // Default node radius
    private static final double SCALE_FACTOR = 2.5; // Increase scaling factor for bigger map
    private static final Random random = new Random(); // Random generator for random vehicles

    private CityMap cityMap;

    // Constructor
    public MapView(CityMap cityMap) {
        this.cityMap = cityMap;
        adjustCanvasSize();
        drawCity();

        // Start updating vehicle counts and weights every X milliseconds
        startVehicleUpdate();
        startTrafficLightUpdate();
    }

    private void adjustCanvasSize() {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

        // Adjusting for scaling factor
        for (Intersection intersection : cityMap.getIntersections()) {
            minX = Math.min(minX, intersection.getX() * SCALE_FACTOR);
            minY = Math.min(minY, intersection.getY() * SCALE_FACTOR);
            maxX = Math.max(maxX, intersection.getX() * SCALE_FACTOR);
            maxY = Math.max(maxY, intersection.getY() * SCALE_FACTOR);
        }

        double width = maxX - minX + 500; // Increased the width to spread out more
        double height = maxY - minY + 500; // Increased the height to spread out more
        setWidth(width);
        setHeight(height);
    }

    // Method to update and draw the city
    public void drawCity() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.web("#2f2f2f")); // Dark background
        gc.fillRect(0, 0, getWidth(), getHeight());

        // Draw all roads
        for (Road road : cityMap.getRoads()) {
            drawRoad(gc, road);
        }

        // Draw all intersections
        for (Intersection intersection : cityMap.getIntersections()) {
            drawIntersection(gc, intersection);
        }
    }

    private void drawRoad(GraphicsContext gc, Road road) {
        double[] coordinates = road.getScaledCoordinates(SCALE_FACTOR);
        double x1 = coordinates[0];
        double y1 = coordinates[1];
        double x2 = coordinates[2];
        double y2 = coordinates[3];

        // Adjust to avoid overlap (making the roads more spaced out)
        double offsetX = 0;
        double offsetY = 0;
        if (Math.abs(x1 - x2) < 10) {
            offsetX = road.isOneWay() ? 5 : -5;
        } else if (Math.abs(y1 - y2) < 10) {
            offsetY = road.isOneWay() ? 5 : -5;
        }

        // Apply offset
        x1 += offsetX * SCALE_FACTOR;
        y1 += offsetY * SCALE_FACTOR;
        x2 += offsetX * SCALE_FACTOR;
        y2 += offsetY * SCALE_FACTOR;

        // Calculate road weight (change this as per your logic)
        double roadWeight = road.calculateWeight();

        // Set the outline color based on the number of vehicles (more vehicles = redder)
        int vehicleCount = road.getVehicleCount();
        Color roadOutlineColor = getOutlineColor(vehicleCount);

        // Blocked roads should be drawn with a different color
        if (road.isBlocked()) {
            gc.setLineWidth(9 * SCALE_FACTOR);  // Increased line width for visibility
            gc.setStroke(Color.RED);  // Change to a bright color like red to make it clearly visible
        } else {
            if (road.isOneWay()) {
                gc.setLineWidth(4 * SCALE_FACTOR);  // Increased width for one-way roads
                drawArrow(gc, x1, y1, x2, y2);
            } else {
                gc.setLineWidth(9 * SCALE_FACTOR);  // Increased width for normal roads
            }
            gc.setStroke(roadOutlineColor);
        }

        gc.strokeLine(x1, y1, x2, y2);

        // Draw the weight text inside the road
        double textX = (x1 + x2) / 2;  // Position in the middle of the road
        double textY = (y1 + y2) / 2;  // Position in the middle of the road
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 18 * SCALE_FACTOR));  // Increased font size
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.format("Weight: %.2f", roadWeight), textX, textY);
    }

    private void drawIntersection(GraphicsContext gc, Intersection inter) {
        double x = inter.getX() * SCALE_FACTOR;
        double y = inter.getY() * SCALE_FACTOR;

        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(1.5 * SCALE_FACTOR);
        shadow.setOffsetY(1.5 * SCALE_FACTOR);
        shadow.setColor(Color.gray(0.2));
        gc.applyEffect(shadow);

        gc.setFill(Color.web("#3b82f6"));
        gc.fillOval(x - RADIUS * SCALE_FACTOR, y - RADIUS * SCALE_FACTOR, RADIUS * 2 * SCALE_FACTOR, RADIUS * 2 * SCALE_FACTOR);

        gc.setStroke(Color.web("#f3f4f6"));
        gc.setLineWidth(2 * SCALE_FACTOR);
        gc.strokeOval(x - RADIUS * SCALE_FACTOR, y - RADIUS * SCALE_FACTOR, RADIUS * 2 * SCALE_FACTOR, RADIUS * 2 * SCALE_FACTOR);

        gc.applyEffect(null);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 18 * SCALE_FACTOR));  // Increased font size
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(inter.getId(), x, y + 6 * SCALE_FACTOR);  // Adjusted position for larger text

        if (inter.hasTrafficLight()) {
            TrafficLight light = inter.getTrafficLight();
            gc.setFill(light.isGreen() ? Color.LIMEGREEN : Color.RED);
            gc.fillOval(x + RADIUS * SCALE_FACTOR - 8, y - RADIUS * SCALE_FACTOR + 8, 14 * SCALE_FACTOR, 14 * SCALE_FACTOR); // Increased size of traffic light
        }
    }

    private void drawArrow(GraphicsContext gc, double x1, double y1, double x2, double y2) {
        double midX = (x1 + x2) / 2;
        double midY = (y1 + y2) / 2;

        double angle = Math.atan2(y2 - y1, x2 - x1);
        double len = 30 * SCALE_FACTOR;  // Increased arrow size

        double arrowX1 = midX - len * Math.cos(angle - Math.PI / 6);
        double arrowY1 = midY - len * Math.sin(angle - Math.PI / 6);
        double arrowX2 = midX - len * Math.cos(angle + Math.PI / 6);
        double arrowY2 = midY - len * Math.sin(angle + Math.PI / 6);

        gc.setLineWidth(4 * SCALE_FACTOR);  // Increased arrow width
        gc.setStroke(Color.WHITE);

        gc.strokeLine(midX, midY, arrowX1, arrowY1);
        gc.strokeLine(midX, midY, arrowX2, arrowY2);
    }

    // Helper method to get outline color based on the number of vehicles on the road
    private Color getOutlineColor(int vehicleCount) {
        if (vehicleCount < 5) {
            return Color.web("#9ca3af"); // Light gray for low traffic
        } else if (vehicleCount < 15) {
            return Color.web("#f4c430"); // Yellow for moderate traffic
        } else {
            return Color.web("#ff4d6d"); // Red for heavy traffic
        }
    }

    // Method to periodically update the vehicle count and road weights
    private void startVehicleUpdate() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            // Randomly add vehicles to random roads
            for (Road road : cityMap.getRoads()) {
                if (!road.isBlocked()) {
                    // Randomly decide to add a vehicle
                    if (random.nextDouble() < 0.1) { // 10% chance of adding a vehicle
                        road.addVehicle();
                    }
                }
            }
            drawCity(); // Redraw the city with updated vehicle counts
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Method to periodically update traffic light states (green/red)
    private void startTrafficLightUpdate() {
        Timeline trafficLightTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            // Toggle the traffic lights
            for (Intersection intersection : cityMap.getIntersections()) {
                if (intersection.hasTrafficLight()) {
                    TrafficLight light = intersection.getTrafficLight();
                    light.toggle(); // Switch the state (green/red)
                }
            }
            drawCity(); // Redraw the city with updated traffic light states
        }));
        trafficLightTimeline.setCycleCount(Timeline.INDEFINITE);
        trafficLightTimeline.play();
    }
}
