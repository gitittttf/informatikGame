package com.informatikgame.world;

import java.util.List;

/**
 * Die Welt ist eine feste Reihe von Räumen, die man nacheinander durchläuft.
 */
public class World {

    private final List<RoomType> raeume;
    private int aktuelleRaumNummer;

    public World(List<RoomType> raeume) {
        if (raeume.isEmpty()) {
            throw new IllegalArgumentException("Welt braucht mindestens einen Raum");
        }
        this.raeume = List.copyOf(raeume);
        this.aktuelleRaumNummer = 0;
    }

    /**
     * Standardwelt mit allen Räumen in der Reihenfolge vom Enum
     */
    public static World standardWelt() {
        return new World(List.of(RoomType.values()));
    }

    public boolean hasNextRoom() {
        return aktuelleRaumNummer < raeume.size() - 1;
    }

    public void naechsterRaum() {
        if (!hasNextRoom()) {
            throw new IllegalStateException("Kein weiterer Raum vorhanden!");
        }
        aktuelleRaumNummer++;
    }

    public RoomType getAktuellerRaum() {
        return raeume.get(aktuelleRaumNummer);
    }

    public int getAktuelleRaumNummer() {
        return aktuelleRaumNummer;
    }

    public int getRaumAnzahl() {
        return raeume.size();
    }

    public List<RoomType> getRaeume() {
        return raeume;
    }
}
