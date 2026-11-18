package com.informatikgame.core;

import java.util.List;

/**
 * Momentaufnahme von HP und Gegnern. Die UI zeigt immer den Zustand an, der
 * zur gerade sichtbaren Log-Nachricht passt.
 */
public record AnzeigeZustand(int spielerHp, int spielerMaxHp, List<GegnerAnzeige> gegner) {

    public record GegnerAnzeige(String name, int hp, int maxHp) {

    }

    public static AnzeigeZustand leer() {
        return new AnzeigeZustand(0, 0, List.of());
    }
}
