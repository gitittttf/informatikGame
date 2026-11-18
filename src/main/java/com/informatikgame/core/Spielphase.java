package com.informatikgame.core;

/**
 * In welcher Phase das Spiel gerade ist
 */
public enum Spielphase {
    STORY, // Raum-Story wird angezeigt
    KAMPF, // Kampf läuft
    RAUM_ENDE_STORY, // Raum geschafft, End-Story wird angezeigt
    WARTE_AUF_WEITER, // Warten auf Enter für den nächsten Raum
    GAME_OVER,
    SIEG
}
