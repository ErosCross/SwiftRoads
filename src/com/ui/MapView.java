package com.ui;

import com.model.CityMap;
import com.model.Intersection;
import com.model.Road;
import com.model.TrafficLight;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.layout.AnchorPane;

public class MapView extends Canvas {
    private static final double RADIUS = 18; // Default node radius
    private static final double SCALE_FACTOR = 1.5; // Scale everything up by this factor
    private CityMap cityMap;
    private double currentOffsetX = 0;
    private double currentOffsetY = 0;
    public Button centerButton;

    public MapView(CityMap cityMap) {
        this.cityMap = cityMap;
        adjustCanvasSize();
        drawCity();
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

        double width = maxX - minX + 300;
        double height = maxY - minY + 300;
        setWidth(width);
        setHeight(height);
    }

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
        double x1 = road.getFrom().getX() * SCALE_FACTOR;
        double y1 = road.getFrom().getY() * SCALE_FACTOR;
        double x2 = road.getTo().getX() * SCALE_FACTOR;
        double y2 = road.getTo().getY() * SCALE_FACTOR;

        // Apply offset for parallel roads to prevent overlap
        double offsetX = 0;
        double offsetY = 0;

        if (Math.abs(x1 - x2) < 10) { // Vertical roads
            offsetX = road.isOneWay() ? 5 : -5;
        } else if (Math.abs(y1 - y2) < 10) { // Horizontal roads
            offsetY = road.isOneWay() ? 5 : -5;
        }

        // Adjust coordinates for parallel roads to prevent overlap
        x1 += offsetX * SCALE_FACTOR;
        y1 += offsetY * SCALE_FACTOR;
        x2 += offsetX * SCALE_FACTOR;
        y2 += offsetY * SCALE_FACTOR;

        // Set line width first before drawing road
        if (road.isOneWay()) {
            gc.setLineWidth(2.5 * SCALE_FACTOR);  // Thin line for one-way roads
            drawArrow(gc, x1, y1, x2, y2);  // Draw arrow indicating one-way direction
        } else {
            gc.setLineWidth(7 * SCALE_FACTOR);  // Thicker line for two-way roads
        }

        // Set road color (red if blocked, light gray otherwise)
        gc.setStroke(road.isBlocked() ? Color.web("#ff4d6d") : Color.web("#9ca3af"));
        gc.strokeLine(x1, y1, x2, y2);
    }

    private void drawIntersection(GraphicsContext gc, Intersection inter) {
        double x = inter.getX() * SCALE_FACTOR;
        double y = inter.getY() * SCALE_FACTOR;

        // Shadow
        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(1.5 * SCALE_FACTOR);
        shadow.setOffsetY(1.5 * SCALE_FACTOR);
        shadow.setColor(Color.gray(0.2));
        gc.applyEffect(shadow);

        // Node circle with scaling applied
        gc.setFill(Color.web("#3b82f6")); // Blue-ish
        gc.fillOval(x - RADIUS * SCALE_FACTOR, y - RADIUS * SCALE_FACTOR, RADIUS * 2 * SCALE_FACTOR, RADIUS * 2 * SCALE_FACTOR);

        // Border with scaling applied
        gc.setStroke(Color.web("#f3f4f6")); // Light border
        gc.setLineWidth(1.5 * SCALE_FACTOR);
        gc.strokeOval(x - RADIUS * SCALE_FACTOR, y - RADIUS * SCALE_FACTOR, RADIUS * 2 * SCALE_FACTOR, RADIUS * 2 * SCALE_FACTOR);

        gc.applyEffect(null);

        // Label inside node with scaling
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 13 * SCALE_FACTOR));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(inter.getId(), x, y + 4 * SCALE_FACTOR);

        // Traffic light
        if (inter.hasTrafficLight()) {
            TrafficLight light = inter.getTrafficLight();
            gc.setFill(light.isGreen() ? Color.LIMEGREEN : Color.RED);
            gc.fillOval(x + RADIUS * SCALE_FACTOR - 6, y - RADIUS * SCALE_FACTOR + 6, 10 * SCALE_FACTOR, 10 * SCALE_FACTOR);
        }
    }

    private void drawArrow(GraphicsContext gc, double x1, double y1, double x2, double y2) {
        // Calculate the midpoint of the road (center)
        double midX = (x1 + x2) / 2;
        double midY = (y1 + y2) / 2;

        // Calculate the angle for the arrowhead direction
        double angle = Math.atan2(y2 - y1, x2 - x1);
        double len = 20 * SCALE_FACTOR; // Length of the arrow

        // Coordinates of the two arrowhead lines, based on the midpoint
        double arrowX1 = midX - len * Math.cos(angle - Math.PI / 6);
        double arrowY1 = midY - len * Math.sin(angle - Math.PI / 6);
        double arrowX2 = midX - len * Math.cos(angle + Math.PI / 6);
        double arrowY2 = midY - len * Math.sin(angle + Math.PI / 6);

        // Ensure the arrow lines are visible
        gc.setLineWidth(3 * SCALE_FACTOR); // Make sure arrow lines are not too thin
        gc.setStroke(Color.WHITE); // Ensure the arrow color contrasts with the background

        // Draw the two arrow lines from the midpoint
        gc.strokeLine(midX, midY, arrowX1, arrowY1);
        gc.strokeLine(midX, midY, arrowX2, arrowY2);
    }



}
