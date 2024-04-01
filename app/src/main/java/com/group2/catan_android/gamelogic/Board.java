package com.group2.catan_android.gamelogic;
import com.group2.catan_android.gamelogic.objects.Building;
import com.group2.catan_android.gamelogic.objects.Connection;
import com.group2.catan_android.gamelogic.objects.Hexagon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Board {

    private List<Hexagon> hexagonList;
    private Connection [][] adjacencyMatrix;
    private Building [][] intersections;

    public Board(){
        hexagonList=generateHexagons();
        adjacencyMatrix = new Connection[54][54];
        intersections = new Building[6][11];
    }

    public void distributeResourcesByDiceRoll(int diceRoll) {
        for (Hexagon hexagon : hexagonList) {
            if (hexagon.getRollValue() == diceRoll) {
                hexagon.distributeResources();
            }
        }
    }

    public static List<Hexagon> generateHexagons() {
        List<String> locations = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        // Copy locations and values lists to ensure original lists remain unchanged
        Collections.addAll(locations, "Hills", "Hills", "Hills", "Forest", "Forest", "Forest", "Forest",
                "Mountains", "Mountains", "Mountains", "Fields", "Fields", "Fields", "Fields",
                "Pasture", "Pasture", "Pasture", "Pasture", "Desert");
        Collections.addAll(values, 2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12);

        List<Hexagon> hexagons = new ArrayList<>();

        Collections.shuffle(locations);
        Collections.shuffle(values);

        for (String location : locations) {
            int value;
            if (location.equals("Desert")) {
                value = 0; // Desert location should have value 0
            } else {
                value = values.remove(0);
            }
            switch (location){
                case "Fields":
                    hexagons.add(new Hexagon(location, new int[]{1, 0, 0, 0, 0}, value));
                    break;
                case "Pasture":
                    hexagons.add(new Hexagon(location, new int[]{0, 1, 0, 0, 0}, value));
                    break;
                case "Forest":
                    hexagons.add(new Hexagon(location, new int[]{0, 0, 1, 0, 0}, value));
                    break;
                case "Hills":
                    hexagons.add(new Hexagon(location, new int[]{0, 0, 0, 1, 0}, value));
                    break;
                case "Mountains":
                    hexagons.add(new Hexagon(location, new int[]{0, 0, 0, 0, 1}, value));
                    break;
                default:
                    hexagons.add(new Hexagon(location, new int[]{0, 0, 0, 0, 0}, value));
            }

        }
        return hexagons;
    }

    public List<Hexagon> getHexagonList() {
        return hexagonList;
    }

    public Connection[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    public Building[][] getIntersections() {
        return intersections;
    }

}

