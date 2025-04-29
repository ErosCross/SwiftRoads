package com.ui;

import com.model.CityMap;
import com.model.Intersection;
import com.model.Road;
import com.model.OneWayRoad;
import com.model.TrafficLight;
import com.simulation.WeightSimulator;
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

/**
 * MapView handles rendering the city map onto a JavaFX Canvas.
 * It draws roads, intersections, traffic lights, and updates traffic dynamically.
 */
public class MapView extends Canvas {
    private static final double RADIUS = 18; // Radius for drawing intersections
    private static final double SCALE_FACTOR = 2.5; // Scale for enlarging the map
    private static final Random random = new Random();

    private final CityMap cityMap;
    private final WeightSimulator weightSimulator; // Simulates traffic changes
    private boolean needsRedraw = true; // Tracks if redraw is necessary

    /**
     * Constructor that initializes the map view and starts simulations.
     * @param cityMap the city map to render
     */
    public MapView(CityMap cityMap) {
        this.cityMap = cityMap;
        this.weightSimulator = new WeightSimulator(cityMap, false); // Avoid reinitializing traffic
        adjustCanvasSize();
        drawCity();
        startVehicleUpdate();
        startTrafficLightUpdate();
    }

    /**
     * Calculates and adjusts canvas size based on city boundaries.
     */
    private void adjustCanvasSize() {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

        for (Intersection intersection : cityMap.getIntersections()) {
            minX = Math.min(minX, intersection.getX() * SCALE_FACTOR);
            minY = Math.min(minY, intersection.getY() * SCALE_FACTOR);
            maxX = Math.max(maxX, intersection.getX() * SCALE_FACTOR);
            maxY = Math.max(maxY, intersection.getY() * SCALE_FACTOR);
        }

        double width = maxX - minX + 500;  // Add margin for better visualization
        double height = maxY - minY + 500;
        setWidth(width);
        setHeight(height);
    }

    /**
     * Redraws the entire city map if needed.
     */
    public void drawCity() {
        if (!needsRedraw) return;
        needsRedraw = false;

        GraphicsContext gc = getGraphicsContext2D();
        gc.setFill(Color.web("#2f2f2f")); // Dark background
        gc.fillRect(0, 0, getWidth(), getHeight());

        for (Road road : cityMap.getRoads()) {
            drawRoad(gc, road);
        }

        for (Intersection intersection : cityMap.getIntersections()) {
            drawIntersection(gc, intersection);
        }
    }

    /**
     * Draws a road on the canvas, including style and weight.
     */
    private void drawRoad(GraphicsContext gc, Road road) {
        double[] coordinates = road.getScaledCoordinates(SCALE_FACTOR);
        double x1 = coordinates[0];
        double y1 = coordinates[1];
        double x2 = coordinates[2];
        double y2 = coordinates[3];

        double offsetX = 0, offsetY = 0;
        // Slight offset for visual distinction
        if (Math.abs(x1 - x2) < 10) { // Vertical
            offsetX = (road instanceof OneWayRoad) ? 5 : -5;
        } else if (Math.abs(y1 - y2) < 10) { // Horizontal
            offsetY = (road instanceof OneWayRoad) ? 5 : -5;
        }

        x1 += offsetX * SCALE_FACTOR;
        y1 += offsetY * SCALE_FACTOR;
        x2 += offsetX * SCALE_FACTOR;
        y2 += offsetY * SCALE_FACTOR;

        // Default road style
        road.setStyle("stroke-width: 4px;");
        gc.setLineWidth(road.getStyleWidth());
        gc.setStroke(road.getStyleColor());

        if (road.isBlocked()) {
            gc.setLineWidth(9 * SCALE_FACTOR);
            gc.setStroke(Color.RED);
        } else {
            if (road instanceof OneWayRoad) {
                gc.setLineWidth(4 * SCALE_FACTOR);
                drawArrow(gc, x1, y1, x2, y2); // Draw arrow for one-way road
            } else {
                gc.setLineWidth(9 * SCALE_FACTOR);
            }
            gc.setStroke(road.getStyleColor());
        }

        gc.strokeLine(x1, y1, x2, y2);

        // Draw weight text
        double textX = (x1 + x2) / 2;
        double textY = (y1 + y2) / 2;
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 18 * SCALE_FACTOR));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(String.format("Weight: %.2f", road.calculateWeight()), textX, textY);
    }

    /**
     * Draws an intersection on the map with optional traffic light.
     */
    private void drawIntersection(GraphicsContext gc, Intersection inter) {
        double x = inter.getX() * SCALE_FACTOR;
        double y = inter.getY() * SCALE_FACTOR;

        // Shadow effect for better look
        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(1.5 * SCALE_FACTOR);
        shadow.setOffsetY(1.5 * SCALE_FACTOR);
        shadow.setColor(Color.gray(0.2));
        gc.applyEffect(shadow);

        // Draw main circle
        gc.setFill(Color.web("#3b82f6")); // Blue
        gc.fillOval(x - RADIUS * SCALE_FACTOR, y - RADIUS * SCALE_FACTOR,
                RADIUS * 2 * SCALE_FACTOR, RADIUS * 2 * SCALE_FACTOR);

        // Draw border
        gc.setStroke(Color.web("#f3f4f6"));
        gc.setLineWidth(2 * SCALE_FACTOR);
        gc.strokeOval(x - RADIUS * SCALE_FACTOR, y - RADIUS * SCALE_FACTOR,
                RADIUS * 2 * SCALE_FACTOR, RADIUS * 2 * SCALE_FACTOR);

        gc.applyEffect(null); // Remove effect for text

        // Draw intersection ID
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 18 * SCALE_FACTOR));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(inter.getId(), x, y + 6 * SCALE_FACTOR);

        // Draw traffic light if exists
        if (inter.hasTrafficLight()) {
            TrafficLight light = inter.getTrafficLight();
            gc.setFill(light.isGreen() ? Color.LIMEGREEN : Color.RED);
            gc.fillOval(x + RADIUS * SCALE_FACTOR - 8, y - RADIUS * SCALE_FACTOR + 8,
                    14 * SCALE_FACTOR, 14 * SCALE_FACTOR);
        }
    }

    /**
     * Draws an arrow in the middle of a one-way road.
     */
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

    /**
     * Starts periodic updates to simulate vehicle movements.
     */
    private void startVehicleUpdate() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            weightSimulator.simulateTraffic(); // Update weights
            needsRedraw = true; // Mark for redraw
            drawCity();
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Starts periodic updates to toggle traffic lights.
     */
    private void startTrafficLightUpdate() {
        Timeline trafficLightTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            for (Intersection intersection : cityMap.getIntersections()) {
                if (intersection.hasTrafficLight()) {
                    intersection.getTrafficLight().toggle();
                }
            }
            needsRedraw = true;
            drawCity();
        }));
        trafficLightTimeline.setCycleCount(Timeline.INDEFINITE);
        trafficLightTimeline.play();
    }
}
