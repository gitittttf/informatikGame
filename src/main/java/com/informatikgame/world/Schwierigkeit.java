package com.informatikgame.world;

import java.util.Arrays;

import com.informatikgame.entities.Werte;

/**
 * Schwierigkeitsgrad: je leichter, desto weniger Gegner pro Raum, desto mehr
 * Heilung nach jedem Raum und desto schwächer die Gegner.
 */
public enum Schwierigkeit {
    // name, max gegner, heilung, gegner-hp in prozent, gegner-angriff, beschreibung
    LEICHT("Leicht", 3, 1.0, 50, -3, "Höchstens 3 Gegner pro Raum, die deutlich schwächer sind. Nach jedem Raum wirst du voll geheilt."),
    NORMAL("Normal", 4, 1.0, 75, -2, "Höchstens 4 Gegner pro Raum, etwas schwächer. Nach jedem Raum wirst du voll geheilt."),
    SCHWER("Schwer", Integer.MAX_VALUE, 0.75, 80, -2, "Alle Gegner in fast voller Stärke. Nach jedem Raum heilst du nur einen Teil.");

    public final String anzeigeName;
    public final int maxGegnerProRaum;
    /**
     * Anteil der fehlenden HP der nach einem Raum geheilt wird (0 bis 1)
     */
    public final double heilungNachRaum;
    public final int gegnerLebenProzent;
    public final int gegnerAngriffBonus;
    public final String beschreibung;

    Schwierigkeit(String anzeigeName, int maxGegnerProRaum, double heilungNachRaum,
            int gegnerLebenProzent, int gegnerAngriffBonus, String beschreibung) {
        this.anzeigeName = anzeigeName;
        this.maxGegnerProRaum = maxGegnerProRaum;
        this.heilungNachRaum = heilungNachRaum;
        this.gegnerLebenProzent = gegnerLebenProzent;
        this.gegnerAngriffBonus = gegnerAngriffBonus;
        this.beschreibung = beschreibung;
    }

    /**
     * Werte eines Gegners an die Schwierigkeit anpassen
     */
    public Werte gegnerWerte(Werte basis) {
        int leben = Math.max(1, (int) Math.round(basis.lebenspunkte() * gegnerLebenProzent / 100.0));
        return new Werte(leben, basis.ruestung(), basis.initiative(),
                Math.max(1, basis.angriff() + gegnerAngriffBonus), basis.verteidigung(), basis.schaden(),
                basis.anzahlW6(), basis.finteLevel(), basis.wuchtschlagLevel());
    }

    /**
     * Gegner für den Raum, hinten abgeschnitten wenn es zu viele sind
     */
    public EnemyType[] gegnerFuer(RoomType raum) {
        EnemyType[] alle = raum.getEnemiesInRoom();
        return Arrays.copyOf(alle, Math.min(alle.length, maxGegnerProRaum));
    }

    /**
     * Wie viel nach dem Raum geheilt wird
     */
    public int heilungFuer(int aktuelleHp, int maxHp) {
        return (int) Math.round((maxHp - aktuelleHp) * heilungNachRaum);
    }
}
