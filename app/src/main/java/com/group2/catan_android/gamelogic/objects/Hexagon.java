package com.group2.catan_android.gamelogic.objects;

import androidx.annotation.NonNull;

import com.group2.catan_android.gamelogic.enums.Location;
import com.group2.catan_android.gamelogic.enums.ResourceDistribution;

import java.util.Locale;

public class Hexagon {
    private final int id;
    private final Location location;
    private final ResourceDistribution distribution;
    private final int rollValue;
    private Building[] buildings;
    private int numOfAdjacentBuildings = 0;

    public Hexagon(Location location, ResourceDistribution distribution, int rollValue, int id) {
        this.location = location;
        this.distribution = distribution;
        this.rollValue = rollValue;
        this.buildings = new Building[3];
        this.id = id;
    }

    public void distributeResources() {
        for (Building building : buildings) {
            if (building != null) {
                building.giveResources(distribution);
            }
        }
    }

    public void addBuilding(Building building) {
        for (int i = 0; i < buildings.length; i++) {
            if (buildings[i] == null) {
                buildings[i] = building;
                numOfAdjacentBuildings++;
                break;
            }
        }
    }

    public void removeBuilding(Building building) {
        for (int i = 0; i < buildings.length; i++) {
            if (buildings[i] == building) {
                buildings[i] = null;
                numOfAdjacentBuildings--;
                break;
            }
        }
    }

    public Location getLocation() {
        return location;
    }

    public ResourceDistribution getDistribution() {
        return distribution;
    }

    public int getRollValue() {
        return rollValue;
    }

    public Building[] getBuildings() {
        return buildings;
    }

    public int getNumOfAdjacentBuildings(){
        return numOfAdjacentBuildings;
    }

    public int getId() {
        return id;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.US,"Hexagon Type: %s; Rollvalue: %d; Number of Buildings adjecent: %d\n",location, rollValue, numOfAdjacentBuildings);
    }
}
