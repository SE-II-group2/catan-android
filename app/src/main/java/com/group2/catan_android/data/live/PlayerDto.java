package com.group2.catan_android.data.live;

public class PlayerDto {
    private String displayName;
    private int inGameID;
    private PlayerState state;
    public PlayerDto(){}

    public PlayerDto(String displayName, int inGameID, PlayerState state) {
        this.displayName = displayName;
        this.inGameID = inGameID;
        this.state = state;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getInGameID() {
        return inGameID;
    }

    public void setInGameID(int inGameID) {
        this.inGameID = inGameID;
    }

    public PlayerState getState() {
        return state;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }
}
