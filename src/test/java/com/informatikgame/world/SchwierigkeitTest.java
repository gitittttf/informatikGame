package com.informatikgame.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.informatikgame.entities.Werte;

public class SchwierigkeitTest {

    @Test
    public void gegnerWerdenBegrenzt() {
        assertEquals(3, Schwierigkeit.LEICHT.gegnerFuer(RoomType.DINING_HALL).length);
        assertEquals(4, Schwierigkeit.NORMAL.gegnerFuer(RoomType.DINING_HALL).length);
        assertEquals(5, Schwierigkeit.SCHWER.gegnerFuer(RoomType.DINING_HALL).length);
        assertEquals(1, Schwierigkeit.LEICHT.gegnerFuer(RoomType.FINAL_ROOM).length);
    }

    @Test
    public void heilungJeNachSchwierigkeit() {
        assertEquals(20, Schwierigkeit.LEICHT.heilungFuer(20, 40));
        assertEquals(15, Schwierigkeit.SCHWER.heilungFuer(20, 40));
        assertEquals(0, Schwierigkeit.SCHWER.heilungFuer(40, 40));
    }

    @Test
    public void gegnerWerteAngepasst() {
        Werte boss = EnemyType.ENDBOSS.werte;
        Werte leicht = Schwierigkeit.LEICHT.gegnerWerte(boss);
        assertEquals(25, leicht.lebenspunkte());
        assertEquals(boss.angriff() - 3, leicht.angriff());
        assertEquals(boss.ruestung(), leicht.ruestung());
    }

    @Test
    public void leichterHeisstImmerLeichter() {
        Schwierigkeit[] alle = Schwierigkeit.values();
        for (int i = 1; i < alle.length; i++) {
            assertTrue(alle[i - 1].maxGegnerProRaum <= alle[i].maxGegnerProRaum);
            assertTrue(alle[i - 1].heilungNachRaum >= alle[i].heilungNachRaum);
            assertTrue(alle[i - 1].gegnerLebenProzent <= alle[i].gegnerLebenProzent);
        }
    }
}
