package com.ui;

import com.model.CityMap;
import com.model.Intersection;
import com.model.Road;
import com.model.OneWayRoad;
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
    private static final double RADIUS = 18; // Radius of intersection circles
    private static final double SCALE_FACTOR = 2.5; // Scale factor for zooming the city map
    private static final Random random = new Random(); // Random generator for vehicle updates

    private CityMap cityMap; // Reference to the city model

    // Constructor: initialize canvas and start updates
    public MapView(CityMap cityMap) {
        this.cityMap = cityMap;
        adjustCanvasSize();
        drawCity();
        startVehicleUpdate();       // Repeatedly adds vehicles
        startTrafficLightUpdate(); // Toggles traffic lights
    }

    // Adjust canvas size based on city layout
    private void adjustCanvasSize() {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

        // Calculate bounding box of all intersections
        for (Intersection intersection : cityMap.getIntersections()) {
            minX = Math.min(minX, intersection.getX() * SCALE_FACTOR);
            minY = Math.min(minY, intersection.getY() * SCALE_FACTOR);
            maxX = Math.max(maxX, intersection.getX() * SCALE_FACTOR);
            maxY = Math.max(maxY, intersection.getY() * SCALE_FACTOR);
        }

        // Add padding for readability
        double width = maxX - minX + 500;
        double height = maxY - minY + 500;
        setWidth(width);
        setHeight(height);
    }

    // Main method to render the map
    public void drawCity() {
        GraphicsContext gc = getGraphicsContext2D();

        // Background
        gc.setFill(Color.web("#2f2f2f"));
        gc.fillRect(0, 0, getWidth(), getHeight());

        // Draw roads first (under intersections)
        for (Road road : cityMap.getRoads()) {
            drawRoad(gc, road);
        }

        // Draw intersections on top
        for (Intersection intersection : cityMap.getIntersections()) {
            drawIntersection(gc, intersection);
        }
    }

    // Draw a single road with proper styling
    private void drawRoad(GraphicsContext gc, Road road) {
        double[] coordinates = road.getScaledCoordinates(SCALE_FACTOR);
        double x1 = coordinates[0];
        double y1 = coordinates[1];
        double x2 = coordinates[2];
        double y2 = coordinates[3];

        // Offset to avoid overlap between one-way and two-way roads
        double offsetX = 0;
        double offsetY = 0;
        if (Math.abs(x1 - x2) < 10) {
            offsetX = (road instanceof OneWayRoad) ? 5 : -5;
        } else if (Math.abs(y1 - y2) < 10) {
            offsetY = (road instanceof OneWayRoad) ? 5 : -5;
        }

        // Apply offset
        x1 += offsetX * SCALE_FACTOR;
        y1 += offsetY * SCALE_FACTOR;
        x2 += offsetX * SCALE_FACTOR;
        y2 += offsetY * SCALE_FACTOR;

        // Apply road style via setStyle method
        road.setStyle("stroke-width: 4px;");

        // Get the current style of the road, and apply it
        gc.setLineWidth(road.getStyleWidth());
        gc.setStroke(road.getStyleColor());

        // Handle blocked roads
        if (road.isBlocked()) {
            gc.setLineWidth(9 * SCALE_FACTOR);
            gc.setStroke(Color.RED);
        } else {
            // Use thinner line for one-way roads and draw arrows
            if (road instanceof OneWayRoad) {
                gc.setLineWidth(4 * SCALE_FACTOR);
                drawArrow(gc, x1, y1, x2, y2);
            } else {
                gc.setLineWidth(9 * SCALE_FACTOR);
            }
            gc.setStroke(road.getStyleColor());
        }

        // Draw the road line
        gc.strokeLine(x1, y1, x2, y2);

        // Draw road weight label
        double textX = (x1 + x2) / 2;
        double textY = (y1 + y2) / 2;
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 18 * SCALE_FACTOR));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.format("Weight: %.2f", road.calculateWeight()), textX, textY);
    }

    // Draw an intersection with optional traffic light
    private void drawIntersection(GraphicsContext gc, Intersection inter) {
        double x = inter.getX() * SCALE_FACTOR;
        double y = inter.getY() * SCALE_FACTOR;

        // Drop shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(1.5 * SCALE_FACTOR);
        shadow.setOffsetY(1.5 * SCALE_FACTOR);
        shadow.setColor(Color.gray(0.2));
        gc.applyEffect(shadow);

        // Draw intersection circle
        gc.setFill(Color.web("#3b82f6"));
        gc.fillOval(x - RADIUS * SCALE_FACTOR, y - RADIUS * SCALE_FACTOR,
                RADIUS * 2 * SCALE_FACTOR, RADIUS * 2 * SCALE_FACTOR);

        gc.setStroke(Color.web("#f3f4f6"));
        gc.setLineWidth(2 * SCALE_FACTOR);
        gc.strokeOval(x - RADIUS * SCALE_FACTOR, y - RADIUS * SCALE_FACTOR,
                RADIUS * 2 * SCALE_FACTOR, RADIUS * 2 * SCALE_FACTOR);

        gc.applyEffect(null);

        // Draw intersection ID
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 18 * SCALE_FACTOR));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(inter.getId(), x, y + 6 * SCALE_FACTOR);

        // Draw traffic light (if exists)
        if (inter.hasTrafficLight()) {
            TrafficLight light = inter.getTrafficLight();
            gc.setFill(light.isGreen() ? Color.LIMEGREEN : Color.RED);
            gc.fillOval(x + RADIUS * SCALE_FACTOR - 8, y - RADIUS * SCALE_FACTOR + 8,
                    14 * SCALE_FACTOR, 14 * SCALE_FACTOR);
        }
    }

    // Draw arrow indicating one-way road direction
    private void drawArrow(GraphicsContext gc, double x1, double y1, double x2, double y2) {
        double midX = (x1 + x2) / 2;
        double midY = (y1 + y2) / 2;

        double angle = Math.atan2(y2 - y1, x2 - x1);
        double len = 30 * SCALE_FACTOR;

        double arrowX1 = midX - len * Math.cos(angle - Math.PI / 6);
        double arrowY1 = midY - len * Math.sin(angle - Math.PI / 6);
        double arrowX2 = midX - len * Math.cos(angle + Math.PI / 6);
        double arrowY2 = midY - len * Math.sin(angle + Math.PI / 6);

        gc.setLineWidth(4 * SCALE_FACTOR);
        gc.setStroke(Color.WHITE);
        gc.strokeLine(midX, midY, arrowX1, arrowY1);
        gc.strokeLine(midX, midY, arrowX2, arrowY2);
    }

    // Periodically simulate vehicles being added to the roads
    private void startVehicleUpdate() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            for (Road road : cityMap.getRoads()) {
                if (!road.isBlocked() && random.nextDouble() < 0.1) {
                    road.addVehicle();
                }
            }
            drawCity(); // Redraw to reflect changes
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Periodically toggle traffic lights at intersections
    private void startTrafficLightUpdate() {
        Timeline trafficLightTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            for (Intersection intersection : cityMap.getIntersections()) {
                if (intersection.hasTrafficLight()) {
                    intersection.getTrafficLight().toggle();
                }
            }
            drawCity(); // Redraw to reflect traffic light changes
        }));
        trafficLightTimeline.setCycleCount(Timeline.INDEFINITE);
        trafficLightTimeline.play();
    }
}
