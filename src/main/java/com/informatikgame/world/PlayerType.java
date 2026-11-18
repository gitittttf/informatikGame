package com.informatikgame.world;

import com.informatikgame.entities.Werte;

public enum PlayerType {
    // lebenspunkte, ruestung, initiative, angriff, verteidigung, schaden, anzahlW6, finteLevel, wuchtschlagLevel
    SWORD_FIGHTER("Schwertkrieger", new Werte(36, 3, 8, 15, 10, 6, 1, 2, 1)),
    SHIELD_FIGHTER("Schildkrieger", new Werte(40, 4, 6, 12, 14, 4, 1, 1, 1)),
    // nur zum debuggen, kann man im menü nicht auswählen
    TEST_PLAYER("Tester", new Werte(100, 100, 100, 100, 100, 6, 1, 100, 100));

    public final String anzeigeName;
    public final Werte werte;

    PlayerType(String anzeigeName, Werte werte) {
        this.anzeigeName = anzeigeName;
        this.werte = werte;
    }
}
