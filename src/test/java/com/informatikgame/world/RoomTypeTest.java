package com.informatikgame.world;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class RoomTypeTest {

    @Test
    public void reihenfolgeDerRaeume() {
        RoomType[] raeume = RoomType.values();
        assertEquals(8, raeume.length);
        assertEquals(RoomType.INTRO_ROOM, raeume[0]);
        assertEquals(RoomType.PANTRY_1, raeume[2]);
        assertEquals(RoomType.FINAL_ROOM, raeume[raeume.length - 1]);
    }

    @Test
    public void jederRaumHatAllesDabei() {
        Set<String> namen = new HashSet<>();
        for (RoomType raum : RoomType.values()) {
            assertNotNull(raum.story, raum.name());
            assertNotNull(raum.endStory, raum.name());
            assertFalse(raum.anzeigeName.isBlank());
            assertTrue(raum.getBild().length > 0);
            assertTrue(raum.getEnemiesInRoom().length > 0, raum + " hat keine gegner");
            assertTrue(namen.add(raum.anzeigeName), "name doppelt: " + raum.anzeigeName);
        }
    }

    @Test
    public void nameUndStoryPassenZusammen() {
        // früher hatte raum 3 den namen "Bibliothek" aber die story von der speisekammer
        assertEquals("Speisekammer", RoomType.PANTRY_1.anzeigeName);
        assertEquals(StoryDatabank.PANTRY_1, RoomType.PANTRY_1.story);
        assertEquals(StoryDatabank.LIBRARY_ROOM, RoomType.LIBRARY_ROOM.story);
    }

    @Test
    public void endbossImLetztenRaum() {
        assertEquals(1, RoomType.FINAL_ROOM.getEnemiesInRoom().length);
        assertEquals(EnemyType.ENDBOSS, RoomType.FINAL_ROOM.getEnemiesInRoom()[0]);
    }

    @Test
    public void gegnerListeKannNichtVeraendertWerden() {
        RoomType.INTRO_ROOM.getEnemiesInRoom()[0] = EnemyType.ENDBOSS;
        assertEquals(EnemyType.MINI_ZOMBIE, RoomType.INTRO_ROOM.getEnemiesInRoom()[0]);
    }

    @Test
    public void jederRaumIstAufDerKarte() {
        for (RoomType raum : RoomType.values()) {
            assertTrue(raum.kartenLabel.startsWith("[") && raum.kartenLabel.endsWith("]"));
        }
    }
}
