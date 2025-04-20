package com.pathfinding;

import com.model.Intersection;
import com.model.Road;

import java.util.*;

public class PathFinder {

    // Class to represent a node in the priority queue for Dijkstra
    private static class Node {
        Intersection intersection;
        double distance;

        Node(Intersection intersection, double distance) {
            this.intersection = intersection;
            this.distance = distance;
        }
    }

    // Find the shortest path using Dijkstra's Algorithm and return a Map<Intersection, Road>
    public static Map<Intersection, Road> findShortestPath(Intersection start, Intersection destination, List<Road> roads) {
        // Initialize distances and previous node map
        Map<Intersection, Double> distances = new HashMap<>();
        Map<Intersection, Road> previousRoads = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(node -> node.distance));

        // Initialize distances to infinity, except for the start intersection
        for (Road road : roads) {
            distances.put(road.getSource(), Double.POSITIVE_INFINITY);
            distances.put(road.getDestination(), Double.POSITIVE_INFINITY);
        }
        distances.put(start, 0.0);  // Start node has distance 0

        pq.add(new Node(start, 0));

        while (!pq.isEmpty()) {
            Node currentNode = pq.poll();
            Intersection currentIntersection = currentNode.intersection;
            double currentDistance = currentNode.distance;

            if (currentIntersection.equals(destination)) {
                return previousRoads; // Return the map of previous roads
            }

            for (Road road : roads) {
                if (road.getSource().equals(currentIntersection)) {
                    double newDist = currentDistance + road.calculateWeight();

                    if (newDist < distances.get(road.getDestination())) {
                        distances.put(road.getDestination(), newDist);
                        pq.add(new Node(road.getDestination(), newDist));
                        previousRoads.put(road.getDestination(), road);  // Track the road leading to each intersection
                    }
                }
            }
        }

        return previousRoads;  // Return empty map if no path found
    }


    // Method to build the path from previous roads map
    private static List<Road> buildPath(Map<Intersection, Road> previousRoads, Intersection destination) {
        List<Road> path = new ArrayList<>();
        Intersection current = destination;

        while (previousRoads.containsKey(current)) {
            Road road = previousRoads.get(current);
            path.add(road);
            current = road.getSource();
        }

        Collections.reverse(path); // Reverse the path to get it from start to destination
        return path;
    }




    public static List<List<Road>> findTopFastestPaths(Intersection start, Intersection destination, List<Road> roads, int topN) {
        List<List<Road>> topPaths = new ArrayList<>();
        Set<String> excludedRoads = new HashSet<>(); // You may use this for additional logic like excluding specific roads if needed

        for (int i = 0; i < topN; i++) {
            // Find the shortest path and get the previous roads map
            Map<Intersection, Road> previousRoads = findShortestPath(start, destination, roads);

            // Reconstruct the path from the previous roads map
            List<Road> path = new ArrayList<>();
            Intersection currentIntersection = destination;

            while (currentIntersection != null && previousRoads.containsKey(currentIntersection)) {
                Road road = previousRoads.get(currentIntersection);
                path.add(road);
                currentIntersection = road.getSource(); // Move to the source intersection
            }

            // Reverse the path because we reconstructed it backwards
            Collections.reverse(path);

            // Skip if the path is empty or already exists (to avoid duplicates)
            if (path.isEmpty() || isDuplicatePath(topPaths, path)) {
                i--; // Try again for a new unique path
                continue;
            }

            // Add the path to the list of top paths
            topPaths.add(path);
        }

        return topPaths;
    }




    private static boolean arePathsEqual(List<Road> path1, List<Road> path2) {
        if (path1.size() != path2.size()) return false;

        for (int i = 0; i < path1.size(); i++) {
            Road road1 = path1.get(i);
            Road road2 = path2.get(i);
            if (!road1.getSource().equals(road2.getSource()) || !road1.getDestination().equals(road2.getDestination())) {
                return false; // Roads are not equal if source or destination differs
            }
        }

        return true;
    }


    private static boolean isDuplicatePath(List<List<Road>> paths, List<Road> newPath) {
        for (List<Road> path : paths) {
            if (path.equals(newPath)) {
                return true;  // Duplicate path found
            }
        }
        return false;
    }


    // Method to calculate the total distance of a path
    public static double calculatePathDistance(List<Road> path) {
        double totalDistance = 0.0;

        // Loop through each road in the path and add its length to the total distance
        for (Road road : path) {
            totalDistance += road.getLength(); // Assuming getLength() returns the distance of the road
        }

        return totalDistance; // Return the total distance of the path
    }


    // You can also create a method to visualize the path on the map if needed
}
