package com.informatikgame.world;

import com.informatikgame.entities.Werte;

public enum EnemyType {
    // lebenspunkte, ruestung, initiative, angriff, verteidigung, schaden, anzahlW6, finteLevel, wuchtschlagLevel
    MINI_ZOMBIE("Mini Zombie", new Werte(10, 1, 12, 10, 6, 1, 1, 1, 0)),
    SCIENTIST("Scientist", new Werte(15, 0, 10, 12, 10, 4, 1, 2, 1)),
    BIG_ZOMBIE("Big Zombie", new Werte(20, 3, 6, 13, 8, 2, 2, 1, 2)),
    ENDBOSS("Endboss", new Werte(50, 5, 4, 14, 10, 1, 4, 3, 3));

    public final String enemyType;
    public final Werte werte;

    EnemyType(String enemyType, Werte werte) {
        this.enemyType = enemyType;
        this.werte = werte;
    }
}
