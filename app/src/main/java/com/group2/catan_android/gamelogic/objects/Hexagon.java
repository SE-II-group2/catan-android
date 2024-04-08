package com.group2.catan_android.gamelogic.objects;

public class Hexagon {
    private String type;
    private int[] resourceValue;
    private int rollValue;
    private Building[] buildings;
    private int numOfBuildings=0;

    public Hexagon(String type, int[] resourceValue, int rollValue) {
        this.type = type;
        this.resourceValue = resourceValue;
        this.rollValue = rollValue;
        this.buildings = new Building[3];
    }

    public void distributeResources() {
        for (Building building : buildings) {
            if (building != null) {
                building.giveResources(resourceValue);
            }
        }
    }

    public void addBuilding(Building building) {
        for (int i = 0; i < buildings.length; i++) {
            if (buildings[i] == null) {
                buildings[i] = building;
                numOfBuildings++;
                break;
            }
        }
    }
    public String getType() {
        return type;
    }

    public int[] getResourceValue() {
        return resourceValue;
    }

    public int getRollValue() {
        return rollValue;
    }

    public Building[] getBuildings() {
        return buildings;
    }

    public int getNumOfBuildings(){
        return numOfBuildings;
    }

    @Override
    public String toString() {
        return String.format("Hexagon Type: %s; Rollvalue: %d; Number of Buildings adjecent: %d\n",type, rollValue, numOfBuildings);
    }
}
