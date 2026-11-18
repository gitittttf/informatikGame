package com.informatikgame.entities;

import com.informatikgame.world.PlayerType;

public class Player extends Character {

    private final PlayerType playerType;

    public Player(PlayerType playerType) {
        super(playerType.werte, "Spieler");
        this.playerType = playerType;
    }

    @Override
    public boolean istSpieler() {
        return true;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }
}
