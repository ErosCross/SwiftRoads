package com.model;
import java.util.*;


public class TrafficLight {
    private boolean isGreen;
    private int greenDuration;  // How long the light stays green (in time units)
    private int redDuration;    // How long the light stays red (in time units)
    private int currentTime;

    public TrafficLight(int greenDuration, int redDuration) {
        this.greenDuration = greenDuration;
        this.redDuration = redDuration;
        this.currentTime = 0;
        this.isGreen = true; // Starts as green by default
    }

    // Update the traffic light's state (simulate time passing)
    public void update() {
        currentTime++;

        int cycle = greenDuration + redDuration;
        int timeInCycle = currentTime % cycle;

        isGreen = timeInCycle < greenDuration;
    }

    // Check if the light is currently green
    public boolean isGreen() {
        return isGreen;
    }

    // Reset the traffic light to the beginning of its cycle
    public void reset() {
        currentTime = 0;
        isGreen = true;
    }

    @Override
    public String toString() {
        return isGreen ? "GREEN" : "RED";
    }
}
