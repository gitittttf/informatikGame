package com.informatikgame.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class RoomTypeTest {

    @Test
    public void testAllRoomTypesExist() {
        assertNotNull(RoomType.ZOMBIE_ROOM);
        assertNotNull(RoomType.INTRO_ROOM);
        assertNotNull(RoomType.LIBRARY_ROOM);
        assertNotNull(RoomType.DINING_HALL);
        assertNotNull(RoomType.LABORATORY);
        assertNotNull(RoomType.CORRIDOR);
        assertNotNull(RoomType.PANTRY);
        assertNotNull(RoomType.FINAL_ROOM);
    }

    @Test
    public void testRoomEnemyComposition() {
        // format: assertEquals(erwarteterWert, tatsächlicherWert)
        // man kann auch andere räume prüfen mit dem format
        assertEquals(2, RoomType.ZOMBIE_ROOM.enemiesInRoom.length);
        assertEquals(EnemyType.MINI_ZOMBIE, RoomType.ZOMBIE_ROOM.enemiesInRoom[0]);
        assertEquals(EnemyType.MINI_ZOMBIE, RoomType.ZOMBIE_ROOM.enemiesInRoom[1]);

        assertEquals(1, RoomType.INTRO_ROOM.enemiesInRoom.length);
        assertEquals(EnemyType.MINI_ZOMBIE, RoomType.INTRO_ROOM.enemiesInRoom[0]);

        assertEquals(1, RoomType.FINAL_ROOM.enemiesInRoom.length);
        assertEquals(EnemyType.ENDBOSS, RoomType.FINAL_ROOM.enemiesInRoom[0]);
    }

    @Test
    public void testRoomDifficultyProgression() {
        // testen ob räume schwieriger werden
        // z.b.:
        assertTrue(RoomType.INTRO_ROOM.enemiesInRoom.length
                <= RoomType.LIBRARY_ROOM.enemiesInRoom.length);

        // Final room sollte endboss haben
        assertEquals(EnemyType.ENDBOSS, RoomType.FINAL_ROOM.enemiesInRoom[0]);
    }
}
