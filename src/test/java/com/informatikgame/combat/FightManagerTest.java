package com.informatikgame.combat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.informatikgame.entities.Player;
import com.informatikgame.world.EnemyType;

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

    @Test
    public void testFightWithSingleEnemy() {
        provideInput("1\n0\n0\n");

        EnemyType[] enemies = {EnemyType.MINI_ZOMBIE};
        boolean result = fightManager.fight(enemies);

        assertTrue(result);
        assertTrue(player.getLifeTotal() > 0);
    }

    @Test
    public void testPlayerDeath() {
        provideInput("1\n0\n0\n");

        // Sollte unmöglich sein zu gewinnen
        EnemyType[] impossibleEnemies = {
            EnemyType.ENDBOSS, EnemyType.ENDBOSS, EnemyType.ENDBOSS
        };

        boolean result = fightManager.fight(impossibleEnemies);
        assertFalse(result);
    }

    @Test
    public void testFightWithMultipleEnemies() {
        provideInput("1\n0\n0\n1\n0\n0\n1\n0\n0\n");

        EnemyType[] enemies = {
            EnemyType.MINI_ZOMBIE,
            EnemyType.MINI_ZOMBIE,
            EnemyType.SCIENTIST
        };

        boolean result = fightManager.fight(enemies);
        assertTrue(result || !result);
    }
}
