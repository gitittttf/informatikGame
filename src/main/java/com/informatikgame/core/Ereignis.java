package com.informatikgame.core;

/**
 * Alles was der GameManager an die UI schickt. Die UI arbeitet die Ereignisse
 * der Reihe nach ab und wartet vor jedem pauseMs, damit man mitlesen kann.
 */
public sealed interface Ereignis {

    long pauseMs();

    record Nachricht(String text, NachrichtenTyp typ, long pauseMs) implements Ereignis {

    }

    record Zustand(AnzeigeZustand zustand, long pauseMs) implements Ereignis {

    }

    /**
     * text ist die Story bei STORY und RAUM_ENDE_STORY, sonst null
     */
    record Phase(Spielphase phase, String text, long pauseMs) implements Ereignis {

    }
}
