package com.informatikgame.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CharacterTest {

    private TestCharacter testCharacter;

    // Helper class um abstract Character zu testen
    static class TestCharacter extends Character {

        public TestCharacter(int lifeTotal, int armorValue, int initiative,
                int attack, int defense, int damage, int numW6, int finteLevel, int wuchtschlagLevel, String type) {
            // int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage, int numW6, int finteLevel, int wuchtschlagLevel, String type
            super(lifeTotal, armorValue, initiative, attack, defense, damage, numW6, finteLevel, wuchtschlagLevel, type);
        }

        public void takeDamage(int damageAmount) {
            if (damageAmount > getArmorValue()) {
                lifeTotal -= damageAmount - getArmorValue();
            }
        }

        public void heal(int healAmount) {
            lifeTotal += healAmount;
        }
    }

    @BeforeEach
    public void setUp() {
        testCharacter = new TestCharacter(100, 5, 10, 15, 12, 20, 1, 1, 1, "Test Character");
    }

    @Test
    public void testCharacterCreation() {
        assertNotNull(testCharacter); // es gibt ein character
        assertEquals(100, testCharacter.getLifeTotal()); // volles leben
        assertEquals(5, testCharacter.getArmorValue()); // armour testen
        assertEquals(10, testCharacter.getInitiative()); // initiative testen
        assertEquals("Test Character", testCharacter.getType()); // type testen
    }

    @Test
    public void testTakeDamage() {
        testCharacter.takeDamage(30);
        assertEquals(70, testCharacter.getLifeTotal()); //selbsterklärend
    }

    @Test
    public void testTakeDamageWithArmor() {
        testCharacter.takeDamage(3); // weniger als armorValue
        assertEquals(100, testCharacter.getLifeTotal()); // dann kein damage bekommen testen

        testCharacter.takeDamage(10); // mehr als armorvalue
        assertEquals(95, testCharacter.getLifeTotal()); // dann 5 damage bekommen (differenz) testen
    }

    @Test
    public void testHeal() {
        testCharacter.takeDamage(40);
        testCharacter.heal(20);
        assertEquals(80, testCharacter.getLifeTotal());
    }

    @Test
    public void testIsAlive() {
        assertTrue(testCharacter.isAlive());

        testCharacter.takeDamage(100);
        assertFalse(testCharacter.isAlive());

        testCharacter.takeDamage(50); // mehr damage an schon toten character testen
        assertFalse(testCharacter.isAlive());
    }
}
