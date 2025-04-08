package com.model;
import java.util.*;

public class Road {
    private final Intersection from;
    private final Intersection to;
    private final double length;
    private final boolean isBlocked;
    private final boolean isOneWay;


    public Road(Intersection from, Intersection to, double length, boolean isBlocked, boolean isOneWay) {
        this.from = from;
        this.to = to;
        this.length = length;
        this.isBlocked = isBlocked;
        this.isOneWay = isOneWay;

    }

    public Intersection getFrom() { return from; }
    public Intersection getTo() { return to; }
    public double getLength() { return length; }
    public boolean isBlocked() { return isBlocked; }
    public boolean isOneWay() { return isOneWay; }

}
