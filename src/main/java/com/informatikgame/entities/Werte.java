package com.informatikgame.entities;

/**
 * Grundwerte eines Kämpfers (Spieler oder Gegner)
 */
public record Werte(
        int lebenspunkte,
        int ruestung,
        int initiative,
        int angriff,
        int verteidigung,
        int schaden,
        int anzahlW6,
        int finteLevel,
        int wuchtschlagLevel) {

}
