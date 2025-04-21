package com.pathfinding;

import com.model.Intersection;
import com.model.Road;

import java.util.*;

public class PathFinder {

    private static class Node {
        Intersection intersection;
        double distance;

        Node(Intersection intersection, double distance) {
            this.intersection = intersection;
            this.distance = distance;
        }
    }

    public static Map<Intersection, Road> findShortestPath(Intersection start, Intersection destination, List<Road> roads) {
        Map<Intersection, Double> distances = new HashMap<>();
        Map<Intersection, Road> previousRoads = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));

        for (Road road : roads) {
            distances.put(road.getSource(), Double.POSITIVE_INFINITY);
            distances.put(road.getDestination(), Double.POSITIVE_INFINITY);
        }
        distances.put(start, 0.0);
        pq.add(new Node(start, 0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (current.intersection.equals(destination)) break;

            for (Road road : roads) {
                if (road.getSource().equals(current.intersection)) {
                    Intersection neighbor = road.getDestination();
                    double newDist = distances.get(current.intersection) + road.calculateWeight();

                    if (newDist < distances.get(neighbor)) {
                        distances.put(neighbor, newDist);
                        pq.add(new Node(neighbor, newDist));
                        previousRoads.put(neighbor, road);
                    }
                }
            }
        }

        return previousRoads;
    }

    public static List<List<Road>> findTopKPaths(Intersection start, Intersection end, List<Road> allRoads, int k) {
        List<List<Road>> resultPaths = new ArrayList<>();
        Map<Intersection, Road> firstPathMap = findShortestPath(start, end, allRoads);
        if (firstPathMap == null || !firstPathMap.containsKey(end)) return resultPaths;

        List<Road> firstPath = reconstructPath(firstPathMap, start, end);
        resultPaths.add(firstPath);

        PriorityQueue<List<Road>> candidates = new PriorityQueue<>(Comparator.comparingDouble(PathFinder::calculatePathDistance));

        for (int i = 1; i < k; i++) {
            List<Road> previousPath = resultPaths.get(i - 1);

            for (int j = 0; j < previousPath.size(); j++) {
                Intersection spurNode = previousPath.get(j).getSource();
                List<Road> rootPath = previousPath.subList(0, j);

                Set<Road> removedEdges = new HashSet<>();
                for (List<Road> path : resultPaths) {
                    if (path.size() > j && path.subList(0, j).equals(rootPath)) {
                        Road toRemove = path.get(j);
                        allRoads.remove(toRemove);
                        removedEdges.add(toRemove);
                    }
                }

                Map<Intersection, Road> spurPathMap = findShortestPath(spurNode, end, allRoads);
                if (spurPathMap != null && spurPathMap.containsKey(end)) {
                    List<Road> spurPath = reconstructPath(spurPathMap, spurNode, end);

                    List<Road> totalPath = new ArrayList<>(rootPath);
                    totalPath.addAll(spurPath);

                    if (!containsPath(resultPaths, totalPath) && !containsPath(candidates, totalPath)) {
                        candidates.add(totalPath);
                    }
                }

                allRoads.addAll(removedEdges);
            }

            if (candidates.isEmpty()) break;
            resultPaths.add(candidates.poll());
        }

        return resultPaths;
    }

    private static List<Road> reconstructPath(Map<Intersection, Road> previousRoads, Intersection start, Intersection end) {
        List<Road> path = new ArrayList<>();
        Intersection current = end;

        while (previousRoads.containsKey(current)) {
            Road road = previousRoads.get(current);
            path.add(road);
            current = road.getSource();
        }

        Collections.reverse(path);
        return path;
    }

    private static boolean containsPath(Collection<List<Road>> paths, List<Road> newPath) {
        for (List<Road> path : paths) {
            if (path.equals(newPath)) return true;
        }
        return false;
    }

    public static double calculatePathDistance(List<Road> path) {
        double total = 0.0;
        for (Road road : path) {
            total += road.getLength();
        }
        return total;
    }
}
