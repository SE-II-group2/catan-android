package com.group2.catan_android.gamelogic;

import com.group2.catan_android.gamelogic.objects.Building;
import com.group2.catan_android.gamelogic.objects.Connection;
import com.group2.catan_android.gamelogic.objects.Hexagon;
import com.group2.catan_android.gamelogic.objects.Road;
import com.group2.catan_android.gamelogic.objects.Village;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Board {

    private List<Hexagon> hexagonList;
    private Connection [][] adjacencyMatrix;
    private Building [][] intersections;

    public Board(){
        hexagonList=generateHexagons();
        adjacencyMatrix = getAdjacencyMatrix();
        intersections = getIntersections();
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
        Connection emptyConnection = new Road(0);

        int[]rows = {0,0,1,1,2,2,2 ,3,3,4,4,4 ,5,5,6 ,6,7 ,7,8,8,8,9,9 ,9 ,10,10,10,11,11,11,12,12,12,13,13,13,14,14,14,15,15,16,16,17,17,17,18,18,18,19,19,19,20,20,20,21,21,21,22,22,22,23,23,23,24,24,24,25,25,25,26,26,27,27,28,28,28,29,29,29,30,30,30,31,31,31,32,32,32,33,33,33,34,34,34,35,35,35,36,36,36,37,37,38,38,39,39,39,40,40,40,41,41,41,42,42,42,43,43,43,44,44,44,45,45,45,46,46,47,47,48,48,49,49,49,50,50,51,51,51,52,52,53,53};
        int[]cols = {1,8,0,2,1,3,10,2,4,3,5,12,4,6,5,14,8,17,0,7,9,8,10,19,2 ,9 ,11,10,12,21,4 ,11,13,12,14,23,6 ,13,15,14,25,17,27,7 ,16,18,17,19,29,9 ,18,20,19,21,31,11,20,22,21,23,33,13,22,24,23,25,35,15,24,26,25,37,16,28,27,29,38,18,28,30,29,31,40,20,30,32,31,33,42,22,32,34,33,35,44,24,34,36,35,37,46,26,36,28,39,38,40,47,30,39,41,40,42,49,32,41,43,42,44,51,34,43,45,44,46,53,36,45,39,48,47,49,41,48,50,49,51,43,50,52,51,53,45,52};

        for (int i = 0; i < rows.length; i++) {
            adjacencyMatrix[rows[i]][cols[i]] = emptyConnection;
        }

        return adjacencyMatrix;
    }

    public Building[][] getIntersections() {
        Building intersection = new Village(0); //eigene Klasse intersection?? playerID?

        for (int i = 0; i <= 2; i++) {
            for (int j = 2 - i; j <= 8 + i; j++) {
                intersections[i][j] = intersection; //obere Hälfte befüllen
                intersections[intersections.length - 1 - i][j] = intersection; //untere Hälfte befüllen
            }
        }

        return intersections;
    }

    public void addRoad(int playerID, int from, int to){
        //player has enough Ressources

        if(adjacencyMatrix[from][to].getPlayerID()==0){ // PlayerID = 0 means empty connection
            adjacencyMatrix[from][to] = new Road(playerID);
        }
    }





}

