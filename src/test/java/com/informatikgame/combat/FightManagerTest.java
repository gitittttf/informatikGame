package com.informatikgame.combat;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.informatikgame.entities.AngriffsErgebnis;
import com.informatikgame.entities.Enemy;
import com.informatikgame.entities.FesterWuerfel;
import com.informatikgame.entities.Player;
import com.informatikgame.entities.Wuerfel;
import com.informatikgame.world.EnemyType;
import com.informatikgame.world.PlayerType;

public class FightManagerTest {

    /**
     * Schreibt einfach mit was passiert
     */
    static class Mitschrift implements FightManager.CombatEventListener {

        final List<String> ereignisse = new ArrayList<>();
        final List<AngriffsErgebnis> angriffe = new ArrayList<>();
        Boolean gewonnen;

        @Override
        public void onRundenStart(int runde) {
            ereignisse.add("runde " + runde);
        }

        @Override
        public void onAngriff(AngriffsErgebnis ergebnis) {
            ereignisse.add("angriff " + ergebnis.angreifer());
            angriffe.add(ergebnis);
        }

        @Override
        public void onSpielerAmZug() {
            ereignisse.add("spieler");
        }

        @Override
        public void onKampfEnde(boolean gewonnen) {
            ereignisse.add("ende " + gewonnen);
            this.gewonnen = gewonnen;
        }
    }

    private Mitschrift mitschrift;

    @BeforeEach
    public void setUp() {
        mitschrift = new Mitschrift();
    }

    private FightManager kampf(PlayerType typ, Wuerfel wuerfel) {
        FightManager fm = new FightManager(new Player(typ), wuerfel);
        fm.setCombatEventListener(mitschrift);
        return fm;
    }

    @Test
    public void spielerMitHoherInitiativeFaengtAn() {
        // tester hat initiative 100, also ist er immer zuerst dran
        FightManager fm = kampf(PlayerType.TEST_PLAYER, new Wuerfel(1));
        fm.startFight(EnemyType.MINI_ZOMBIE);
        assertEquals(List.of("runde 1", "spieler"), mitschrift.ereignisse);
        assertTrue(fm.isWaitingForPlayerAction());
    }

    @Test
    public void kampfBisZumSieg() {
        FightManager fm = kampf(PlayerType.TEST_PLAYER, new Wuerfel(5));
        fm.startFight(EnemyType.MINI_ZOMBIE, EnemyType.MINI_ZOMBIE);

        int sicherheit = 0;
        while (fm.isKampfLaeuft() && sicherheit++ < 100) {
            assertTrue(fm.executePlayerAction(0, 0, 0));
        }
        assertEquals(Boolean.TRUE, mitschrift.gewonnen);
        assertTrue(fm.getEnemiesLeftToRight().isEmpty());
        assertEquals("ende true", mitschrift.ereignisse.get(mitschrift.ereignisse.size() - 1));
    }

    @Test
    public void besiegteGegnerSindSofortWeg() {
        // spieler trifft immer und macht genug schaden für einen zombie
        FesterWuerfel wuerfel = new FesterWuerfel().standard(1, 6);
        FightManager fm = kampf(PlayerType.TEST_PLAYER, wuerfel);
        fm.startFight(EnemyType.MINI_ZOMBIE, EnemyType.MINI_ZOMBIE);
        wuerfel.w20(1, 20); // treffer, keine parade

        assertTrue(fm.executePlayerAction(0, 0, 0));
        assertEquals(1, fm.getEnemiesLeftToRight().size());
        // der tote zombie greift in der runde auch nicht mehr an und index 1 gibt es nicht mehr
        assertFalse(fm.executePlayerAction(1, 0, 0));
    }

    @Test
    public void ungueltigeEingabenWerdenAbgelehnt() {
        FightManager fm = kampf(PlayerType.SWORD_FIGHTER, new FesterWuerfel().standard(20, 1));
        fm.startFight(EnemyType.MINI_ZOMBIE);
        // spieler hat initiative 8+1, zombie 12+1 → zombie zuerst, dann spieler
        assertTrue(fm.isWaitingForPlayerAction());

        assertFalse(fm.executePlayerAction(-1, 0, 0));
        assertFalse(fm.executePlayerAction(5, 0, 0));
        assertFalse(fm.executePlayerAction(0, 3, 0)); // finte geht nur bis 2
        assertFalse(fm.executePlayerAction(0, 0, 2)); // wuchtschlag nur bis 1
        assertTrue(fm.isWaitingForPlayerAction());
        assertTrue(fm.executePlayerAction(0, 2, 1));
    }

    @Test
    public void nichtDranHeisstNichtAngreifen() {
        FightManager fm = kampf(PlayerType.SWORD_FIGHTER, new Wuerfel(3));
        assertFalse(fm.executePlayerAction(0, 0, 0));
    }

    @Test
    public void reihenfolgeNachInitiative() {
        FightManager fm = kampf(PlayerType.SWORD_FIGHTER, new FesterWuerfel().standard(20, 1));
        fm.startFight(EnemyType.MINI_ZOMBIE);
        // zombie (13) vor spieler (9)
        assertEquals(List.of("runde 1", "angriff Mini Zombie", "spieler"), mitschrift.ereignisse);
    }

    @Test
    public void spielerStirbtKampfVerloren() {
        Player spieler = new Player(PlayerType.SWORD_FIGHTER);
        // gegner trifft immer mit maximalem schaden, spieler verfehlt immer
        FesterWuerfel wuerfel = new FesterWuerfel().standard(1, 6);
        FightManager fm = new FightManager(spieler, wuerfel);
        fm.setCombatEventListener(mitschrift);
        List<Enemy> gegner = List.of(new Enemy(EnemyType.ENDBOSS));
        fm.startFight(gegner);

        int sicherheit = 0;
        while (fm.isKampfLaeuft() && sicherheit++ < 100) {
            wuerfel.w20(20, 1, 20); // spieler verfehlt, boss trifft, spieler pariert nicht
            fm.executePlayerAction(0, 0, 0);
        }
        assertEquals(Boolean.FALSE, mitschrift.gewonnen);
        assertEquals(0, spieler.getLifeTotal());
    }
}
