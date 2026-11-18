package com.informatikgame.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.informatikgame.world.EnemyType;
import com.informatikgame.world.PlayerType;
import com.informatikgame.world.UpgradeType;

public class CharacterTest {

    private Player spieler;
    private Enemy zombie;

    @BeforeEach
    public void setUp() {
        spieler = new Player(PlayerType.SWORD_FIGHTER); // 36 HP, Rüstung 3, Angriff 15, Schaden 6 + 1W6
        zombie = new Enemy(EnemyType.MINI_ZOMBIE); // 10 HP, Rüstung 1, Verteidigung 6
    }

    @Test
    public void werteAusDemTyp() {
        assertEquals(36, spieler.getLifeTotal());
        assertEquals(36, spieler.getMaxLife());
        assertEquals(3, spieler.getArmourValue());
        assertEquals("Spieler", spieler.getType());
        assertTrue(spieler.istSpieler());
        assertEquals("Mini Zombie", zombie.getType());
        assertFalse(zombie.istSpieler());
    }

    @Test
    public void treffenMitRuestung() {
        // angriff 5 <= 15 trifft, schaden 6 + 4, parade 20 > 6 klappt nicht
        Wuerfel wuerfel = new FesterWuerfel().w20(5, 20).w6(4);
        AngriffsErgebnis ergebnis = spieler.attack(zombie, 0, 0, wuerfel);

        assertTrue(ergebnis.getroffen());
        assertFalse(ergebnis.pariert());
        assertEquals(10, ergebnis.rohSchaden());
        assertEquals(9, ergebnis.erlittenerSchaden()); // minus 1 rüstung
        assertEquals(1, zombie.getLifeTotal());
        assertFalse(ergebnis.zielBesiegt());
    }

    @Test
    public void danebenMachtKeinenSchaden() {
        AngriffsErgebnis ergebnis = spieler.attack(zombie, 0, 0, new FesterWuerfel().w20(16));
        assertFalse(ergebnis.getroffen());
        assertEquals(0, ergebnis.erlittenerSchaden());
        assertEquals(10, zombie.getLifeTotal());
    }

    @Test
    public void paradeBlocktAlles() {
        AngriffsErgebnis ergebnis = spieler.attack(zombie, 0, 0, new FesterWuerfel().w20(1, 6));
        assertTrue(ergebnis.getroffen());
        assertTrue(ergebnis.pariert());
        assertEquals(10, zombie.getLifeTotal());
    }

    @Test
    public void finteErschwertDieParade() {
        // verteidigung 6 - 2*2 = 2, eine 6 reicht nicht mehr zum parieren
        AngriffsErgebnis ergebnis = spieler.attack(zombie, 2, 0, new FesterWuerfel().w20(1, 6).w6(1));
        assertTrue(ergebnis.getroffen());
        assertFalse(ergebnis.pariert());
        assertEquals(2, ergebnis.finte());
    }

    @Test
    public void wuchtschlagMachtMehrSchaden() {
        // wuchtschlag 1: angriff -2, schaden +2
        AngriffsErgebnis ergebnis = spieler.attack(zombie, 0, 1, new FesterWuerfel().w20(13, 20).w6(1));
        assertTrue(ergebnis.getroffen());
        assertEquals(6 + 2 + 1, ergebnis.rohSchaden());

        AngriffsErgebnis daneben = spieler.attack(new Enemy(EnemyType.MINI_ZOMBIE), 0, 1, new FesterWuerfel().w20(14));
        assertFalse(daneben.getroffen());
    }

    @Test
    public void spezialLevelWirdBegrenzt() {
        AngriffsErgebnis ergebnis = spieler.attack(zombie, 99, 99, new FesterWuerfel().w20(20));
        assertEquals(2, ergebnis.finte());
        assertEquals(1, ergebnis.wuchtschlag());
    }

    @Test
    public void hpNieUnterNull() {
        AngriffsErgebnis ergebnis = spieler.attack(zombie, 0, 0, new FesterWuerfel().w20(1, 20).w6(6));
        assertEquals(0, zombie.getLifeTotal());
        assertEquals(10, ergebnis.erlittenerSchaden());
        assertTrue(ergebnis.zielBesiegt());
        assertFalse(zombie.isAlive());
    }

    @Test
    public void heilenNichtUeberMaxHp() {
        zombie.attack(spieler, 0, 0, new FesterWuerfel().w20(1, 20).w6(6)); // 1 + 6 - 3 = 4 schaden
        assertEquals(32, spieler.getLifeTotal());
        assertEquals(4, spieler.heal(100));
        assertEquals(36, spieler.getLifeTotal());
        assertEquals(0, spieler.heal(5));
    }

    @Test
    public void upgradeErhoehtWerte() {
        spieler.upgrade(UpgradeType.LIFE_UPGRADE);
        assertEquals(41, spieler.getMaxLife());
        assertEquals(41, spieler.getLifeTotal());

        spieler.upgrade(UpgradeType.SKILL_UPGRADE);
        assertEquals(3, spieler.getFinteLevel());
        assertEquals(2, spieler.getWuchtschlagLevel());

        spieler.upgrade(UpgradeType.ARMOUR_UPGRADE);
        assertEquals(6, spieler.getArmourValue());
    }

    @Test
    public void upgradeBeschreibung() {
        assertEquals("+5 Max-HP, +2 Schaden", UpgradeType.PANTRY_COMPOUND_UPGRADE.beschreibung());
        assertEquals("+1 Finte-Level, +1 Wuchtschlag-Level", UpgradeType.SKILL_UPGRADE.beschreibung());
    }
}
