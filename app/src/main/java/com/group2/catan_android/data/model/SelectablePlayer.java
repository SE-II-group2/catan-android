package com.group2.catan_android.data.model;

import com.group2.catan_android.gamelogic.Player;

public class SelectablePlayer {
    private boolean isSelected;
    private Player player;

    public SelectablePlayer(Player player, boolean isSelected){
        this.player=player;
        this.isSelected=isSelected;
    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
