package com.group2.catan_android.gamelogic;

import java.util.ArrayList;
import java.util.List;

public class CurrentGameState {
    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    private List<Player> players;

    public CurrentGameState(List<Player> players, Board board) {
        this.players = players;
        this.board = board;
    }
    public CurrentGameState(){
        this.board=new Board();
        this.players = new ArrayList<>();
    }

   // public void setValue()
    private Board board;

}
