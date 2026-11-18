package com.informatikgame.world;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class WorldTest {

    @Test
    public void durchlaufen() {
        World world = new World(List.of(RoomType.INTRO_ROOM, RoomType.FINAL_ROOM));
        assertEquals(RoomType.INTRO_ROOM, world.getAktuellerRaum());
        assertTrue(world.hasNextRoom());
        world.naechsterRaum();
        assertEquals(RoomType.FINAL_ROOM, world.getAktuellerRaum());
        assertEquals(1, world.getAktuelleRaumNummer());
        assertFalse(world.hasNextRoom());
        assertThrows(IllegalStateException.class, world::naechsterRaum);
    }

    @Test
    public void raumListeNichtVeraenderbar() {
        World world = World.standardWelt();
        assertEquals(RoomType.values().length, world.getRaumAnzahl());
        assertThrows(UnsupportedOperationException.class, () -> world.getRaeume().clear());
    }
}
