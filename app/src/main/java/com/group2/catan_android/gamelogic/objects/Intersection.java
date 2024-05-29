package com.group2.catan_android.gamelogic.objects;

import com.group2.catan_android.gamelogic.Board;
import com.group2.catan_android.gamelogic.Player;
import com.group2.catan_android.gamelogic.enums.BuildingType;

public class Intersection {
    Player player;
    BuildingType type = BuildingType.EMPTY;

    public Player getPlayer() {
        return player;
    }

    public BuildingType getType() {
        return type;
    }

    public boolean isNextToOwnRoad(Board board, Player player, int intersectionID){
        Connection[][] adjacencyMatrix = board.getAdjacencyMatrix();
        //check the specific intersection in the adjacencyMatrix if there are any roads, and if it belongs to the playerID who wants to build
        for(int i = 0; i < 54; i++){
            if((adjacencyMatrix[i][intersectionID] instanceof Road) && (adjacencyMatrix[i][intersectionID].getPlayer() == player)){
                return true;
            }
        }
        return false;
    }

    public boolean notNextToBuilding(Board board, int row, int col){
        Intersection[][] intersections = board.getIntersections();

        boolean evenCol = col % 2 == 0;
        boolean evenRow = row % 2 == 0;
        boolean nextToBuilding;

        if(col == 0){
            nextToBuilding = (intersections[row][col + 1] instanceof Building);
        } else if (col == intersections[0].length-1) {
            nextToBuilding = (intersections[row][col - 1] instanceof Building);
        }else nextToBuilding = (intersections[row][col - 1] instanceof Building || intersections[row][col + 1] instanceof Building);

        if(nextToBuilding) {
            return false;
        }

        //if even even or uneven uneven check below, else above if there is a building
        if((evenRow && evenCol) || (!evenRow && !evenCol)){
            if(row != intersections.length-1 && intersections[row + 1][col] instanceof Building){
                nextToBuilding = true;
            }
        } else{
            if(row != 0 && intersections[row - 1][col] instanceof Building) {
                nextToBuilding = true;
            }
        }

        return !nextToBuilding;
    }

}
