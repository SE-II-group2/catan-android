package com.group2.catan_android.data.live.game;


import com.group2.catan_android.data.live.game.IngamePlayerDto;

public class ConnectionDto {
    public IngamePlayerDto getOwner() {
        return owner;
    }

    public ConnectionDto(IngamePlayerDto owner, int id) {
        this.owner = owner;
        this.id = id;
    }

    public ConnectionDto() {
    }

    public void setOwner(IngamePlayerDto owner) {
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private IngamePlayerDto owner;
    private int id;
}
