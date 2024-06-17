package com.group2.catan_android.gamelogic;

import java.util.ArrayList;
import java.util.List;

public class CurrentGameState {
    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    private List<Player> players;

    public Player getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(Player activePlayer) {
        this.activePlayer = activePlayer;
    }

    private Player activePlayer;

    public CurrentGameState(List<Player> players, Board board, Player activePlayer) {
        this.players = players;
        this.board = board;
        this.activePlayer = activePlayer;
    }
    public CurrentGameState(){
        this.board=new Board();
        this.players = new ArrayList<>();
    }

   // public void setValue()
    private Board board;

}
