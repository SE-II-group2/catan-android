package com.group2.catan_android.data.live.game;


import com.group2.catan_android.data.live.PlayerDto;

public class ConnectionDto {
    public PlayerDto getOwner() {
        return owner;
    }

    public ConnectionDto(PlayerDto owner, int id) {
        this.owner = owner;
        this.id = id;
    }

    public ConnectionDto() {
    }

    public void setOwner(PlayerDto owner) {
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private PlayerDto owner;
    private int id;
}
