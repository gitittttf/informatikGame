package com.informatikgame.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

public class EnemyTypeTest {

    @Test
    public void testEnemyTypeValues() {
        assertEquals(10, EnemyType.MINI_ZOMBIE.lifeTotal);
        assertEquals(1, EnemyType.MINI_ZOMBIE.armourValue);
        assertEquals("Mini Zombie", EnemyType.MINI_ZOMBIE.enemyType);

        assertEquals(50, EnemyType.ENDBOSS.lifeTotal);
        assertEquals(5, EnemyType.ENDBOSS.armourValue);
        assertEquals("Endboss", EnemyType.ENDBOSS.enemyType);
    }

    @Test
    public void testAllEnemyTypesExist() {
        assertNotNull(EnemyType.MINI_ZOMBIE);
        assertNotNull(EnemyType.SCIENTIST);
        assertNotNull(EnemyType.BIG_ZOMBIE);
        assertNotNull(EnemyType.ENDBOSS);
    }
}
