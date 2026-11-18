package com.informatikgame.entities;

import com.informatikgame.world.EnemyType;

public class Enemy extends Character {

    public Enemy(EnemyType enemyType) {
        this(enemyType, enemyType.werte);
    }

    /**
     * Gegner mit angepassten Werten (z.B. durch die Schwierigkeit)
     */
    public Enemy(EnemyType enemyType, Werte werte) {
        super(werte, enemyType.enemyType);
    }

    @Override
    public boolean istSpieler() {
        return false;
    }
}
