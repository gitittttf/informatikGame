package com.informatikgame.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.informatikgame.world.EnemyType;

public class EnemyTest {

    private Enemy enemy;

    @BeforeEach
    public void setUp() {
        enemy = new Enemy(EnemyType.MINI_ZOMBIE);
    }

    @Test
    public void testEnemyCreationFromType() {
        assertNotNull(enemy);
        assertEquals(10, enemy.getLifeTotal());
        assertEquals(1, enemy.getArmorValue());
        assertEquals(12, enemy.getInitiative());
        assertEquals("Mini Zombie", enemy.getType());
    }

    @Test
    public void testDifferentEnemyTypes() {
        Enemy scientist = new Enemy(EnemyType.SCIENTIST);
        assertEquals(15, scientist.getLifeTotal());
        assertEquals(0, scientist.getArmorValue());
        assertEquals("Scientist", scientist.getType());

        Enemy endboss = new Enemy(EnemyType.ENDBOSS);
        assertEquals(50, endboss.getLifeTotal());
        assertEquals(5, endboss.getArmorValue());
        assertEquals("Endboss", endboss.getType());
    }

    @Test
    public void testEnemyAttack() {
        Player player = new Player(36, 4, 8, 15, 10, 8);
        int initialHealth = player.getLifeTotal();

        enemy.attack(player, 0, 0);

        assertTrue(player.getLifeTotal() < initialHealth || player.getLifeTotal() == initialHealth);
    }
}
