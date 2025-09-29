package com.informatikgame.entities;

import com.informatikgame.world.EnemyType;

public class Enemy extends Character {

    // Constructor
    public Enemy(EnemyType enemyType) {
        super(enemyType.lifeTotal, enemyType.armourValue, enemyType.initiative,
                enemyType.attack, enemyType.defense, enemyType.damage, enemyType.numW6,
                enemyType.finteLevel, enemyType.wuchtschlagLevel, enemyType.enemyType);
    }

    public Enemy(int lifeTotal, int armourValue, int initiative, int attack,
            int defense, int damage, int numW6, int finteLevel,
            int wuchtschlagLevel, String enemyType) {
        super(lifeTotal, armourValue, initiative, attack, defense, damage,
                numW6, finteLevel, wuchtschlagLevel, enemyType);
    }
}
