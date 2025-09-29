package com.informatikgame.combat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.informatikgame.entities.Player;

// WICHTIG: alle tests mit inputs funktionieren noch nicht
// TODO: tests mit inputs fixen
public class FightManagerTest {

    private FightManager fightManager;
    private Player player;

    @BeforeEach
    public void setUp() {
        // Use the current PlayerType enum constructor
        player = new Player(com.informatikgame.world.PlayerType.SWORD_FIGHTER);
        fightManager = new FightManager(player);
    }

    @AfterEach
    public void tearDown() {
        if (fightManager != null) {
            // fightManager.close(); // TODO
        }
    }

    private void provideInput(String data) {
        InputStream in = new ByteArrayInputStream(data.getBytes());
        System.setIn(in);
    }

    @Test
    public void testFightManagerCreation() {
        assertNotNull(fightManager);
        assertNotNull(fightManager.getPlayer());
        assertEquals(player, fightManager.getPlayer());
    }
}
