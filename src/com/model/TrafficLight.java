package com.model;

public class TrafficLight {
    public enum State {
        GREEN,
        YELLOW,
        RED
    }

    private State currentState;
    private int greenDuration;  // How long the light stays green (in time units)
    private int yellowDuration; // How long the light stays yellow (in time units)
    private int redDuration;    // How long the light stays red (in time units)
    private int currentTime;    // Current time within the cycle

    public TrafficLight(int greenDuration, int yellowDuration, int redDuration) {
        this.greenDuration = greenDuration;
        this.yellowDuration = yellowDuration;
        this.redDuration = redDuration;
        this.currentTime = 0;
        this.currentState = State.GREEN; // Starts as green by default
    }

    // Update the traffic light's state (simulate time passing)
    public void update() {
        currentTime++;

        int cycle = greenDuration + yellowDuration + redDuration;
        int timeInCycle = currentTime % cycle;

        // Manage the state based on the time in the cycle
        if (timeInCycle < greenDuration) {
            currentState = State.GREEN;
        } else if (timeInCycle < greenDuration + yellowDuration) {
            currentState = State.YELLOW;
        } else {
            currentState = State.RED;
        }
    }

    // Check if the light is currently green
    public boolean isGreen() {
        return currentState == State.GREEN;
    }

    // Check if the light is currently yellow
    public boolean isYellow() {
        return currentState == State.YELLOW;
    }

    // Check if the light is currently red
    public boolean isRed() {
        return currentState == State.RED;
    }

    // Toggle the traffic light between GREEN and RED states
    public void toggle() {
        if (currentState == State.GREEN) {
            currentState = State.RED;
        } else if (currentState == State.RED) {
            currentState = State.GREEN;
        }
        currentTime = 0; // Reset the timer when toggling
    }

    // Reset the traffic light to the beginning of its cycle
    public void reset() {
        currentTime = 0;
        currentState = State.GREEN; // Default start state
    }

    // Getters and setters for the durations
    public int getGreenDuration() {
        return greenDuration;
    }

    public void setGreenDuration(int greenDuration) {
        this.greenDuration = greenDuration;
    }

    public int getYellowDuration() {
        return yellowDuration;
    }

    public void setYellowDuration(int yellowDuration) {
        this.yellowDuration = yellowDuration;
    }

    public int getRedDuration() {
        return redDuration;
    }

    public void setRedDuration(int redDuration) {
        this.redDuration = redDuration;
    }

    // Override toString to provide more information
    @Override
    public String toString() {
        return String.format("%s (Green: %d, Yellow: %d, Red: %d)", currentState.name(),
                greenDuration, yellowDuration, redDuration);
    }

    // Optionally, to get the time left in the current state
    public int timeLeftInCurrentState() {
        int cycle = greenDuration + yellowDuration + redDuration;
        int timeInCycle = currentTime % cycle;

        if (currentState == State.GREEN) {
            return greenDuration - timeInCycle;
        } else if (currentState == State.YELLOW) {
            return yellowDuration - (timeInCycle - greenDuration);
        } else {
            return redDuration - (timeInCycle - (greenDuration + yellowDuration));
        }
    }

    // Optionally, change the entire cycle duration dynamically
    public void setCycleDuration(int green, int yellow, int red) {
        this.greenDuration = green;
        this.yellowDuration = yellow;
        this.redDuration = red;
        this.reset(); // Reset after changing the cycle
    }
}
