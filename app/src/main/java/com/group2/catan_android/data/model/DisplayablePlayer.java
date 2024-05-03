package com.group2.catan_android.data.model;

import com.group2.catan_android.data.live.PlayerState;

public class DisplayablePlayer {
    private boolean isAdmin;
    private int inGameID;
    private String displayName;
    private PlayerState state;

    public DisplayablePlayer(){}
    public DisplayablePlayer(boolean isAdmin, int inGameID, String displayName, PlayerState state) {
        this.isAdmin = isAdmin;
        this.inGameID = inGameID;
        this.displayName = displayName;
        this.state = state;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public int getInGameID() {
        return inGameID;
    }

    public void setInGameID(int inGameID) {
        this.inGameID = inGameID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public PlayerState getState() {
        return state;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }
}
