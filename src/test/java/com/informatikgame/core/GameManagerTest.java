package com.informatikgame.core;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.informatikgame.entities.Wuerfel;
import com.informatikgame.world.PlayerType;
import com.informatikgame.world.RoomType;
import com.informatikgame.world.Schwierigkeit;

public class GameManagerTest {

    private final List<Ereignis> ereignisse = new ArrayList<>();
    private GameManager gm;

    @BeforeEach
    public void setUp() {
        ereignisse.clear();
        gm = new GameManager(new Wuerfel(99));
        gm.setListener(ereignisse::add);
    }

    private List<Spielphase> phasen() {
        List<Spielphase> phasen = new ArrayList<>();
        for (Ereignis e : ereignisse) {
            if (e instanceof Ereignis.Phase p) {
                phasen.add(p.phase());
            }
        }
        return phasen;
    }

    private Ereignis.Phase letztePhase() {
        for (int i = ereignisse.size() - 1; i >= 0; i--) {
            if (ereignisse.get(i) instanceof Ereignis.Phase p) {
                return p;
            }
        }
        return null;
    }

    /**
     * Kampf zu Ende spielen, immer den ersten Gegner angreifen
     */
    private void kaempfen() {
        int sicherheit = 0;
        while (gm.getPhase() == Spielphase.KAMPF && sicherheit++ < 1000) {
            assertTrue(gm.wartetAufSpieler());
            assertTrue(gm.spielerAngriff(0, 0, 0));
        }
    }

    @Test
    public void startenOhneInitialisierenGehtNicht() {
        assertThrows(IllegalStateException.class, gm::starteSpiel);
    }

    @Test
    public void spielStartetMitStory() {
        gm.initialisiereSpiel(PlayerType.SWORD_FIGHTER, Schwierigkeit.NORMAL);
        gm.starteSpiel();
        assertEquals(Spielphase.STORY, gm.getPhase());
        assertEquals(RoomType.INTRO_ROOM.story.story, letztePhase().text());
        // erster zustand kommt vor der story, damit die hp gleich stimmen
        assertTrue(ereignisse.get(0) instanceof Ereignis.Zustand);
    }

    @Test
    public void kompletterDurchlaufMitDemTester() {
        gm.initialisiereSpiel(PlayerType.TEST_PLAYER, Schwierigkeit.SCHWER);
        gm.starteSpiel();

        for (int raum = 0; raum < RoomType.values().length; raum++) {
            assertEquals(Spielphase.STORY, gm.getPhase());
            assertEquals(RoomType.values()[raum], gm.getAktuellerRaum());
            gm.weiter();
            assertEquals(Spielphase.KAMPF, gm.getPhase());
            kaempfen();
            assertEquals(Spielphase.RAUM_ENDE_STORY, gm.getPhase());
            assertTrue(letztePhase().text().startsWith(RoomType.values()[raum].endStory.story));
            gm.weiter();
            if (raum < RoomType.values().length - 1) {
                assertEquals(Spielphase.WARTE_AUF_WEITER, gm.getPhase());
                gm.weiter();
            }
        }
        assertEquals(Spielphase.SIEG, gm.getPhase());

        // enter nach dem sieg macht nichts mehr
        gm.weiter();
        assertEquals(Spielphase.SIEG, gm.getPhase());
    }

    @Test
    public void phasenReihenfolgeErsterRaum() {
        gm.initialisiereSpiel(PlayerType.TEST_PLAYER, Schwierigkeit.LEICHT);
        gm.starteSpiel();
        gm.weiter();
        kaempfen();
        gm.weiter();
        gm.weiter();
        assertEquals(List.of(Spielphase.STORY, Spielphase.KAMPF, Spielphase.RAUM_ENDE_STORY,
                Spielphase.WARTE_AUF_WEITER, Spielphase.STORY), phasen());
    }

    @Test
    public void upgradeUndHeilungNachDemRaum() {
        gm.initialisiereSpiel(PlayerType.SWORD_FIGHTER, Schwierigkeit.LEICHT);
        gm.starteSpiel();
        gm.weiter();
        kaempfen();
        if (gm.getPhase() == Spielphase.GAME_OVER) {
            return; // mit dem seed nicht passiert, aber sicher ist sicher
        }
        // intro gibt +5 max hp und auf leicht wird voll geheilt
        assertEquals(41, gm.getPlayer().getMaxLife());
        assertEquals(41, gm.getPlayer().getLifeTotal());
        assertTrue(letztePhase().text().contains("+5 Max-HP"));
    }

    @Test
    public void schwierigkeitBegrenztGegner() {
        gm.initialisiereSpiel(PlayerType.TEST_PLAYER, Schwierigkeit.LEICHT);
        gm.starteSpiel();
        // bis zum speisesaal (5 gegner) durchspielen
        while (gm.getAktuellerRaum() != RoomType.DINING_HALL) {
            gm.weiter();
            kaempfen();
            gm.weiter();
            gm.weiter();
        }
        gm.weiter();
        AnzeigeZustand zustand = gm.aktuellerZustand();
        assertEquals(3, zustand.gegner().size());
        assertEquals(5, zustand.gegner().get(0).maxHp()); // mini zombie 10 hp, auf leicht 50%
    }

    @Test
    public void angriffNurImKampf() {
        gm.initialisiereSpiel(PlayerType.SWORD_FIGHTER, Schwierigkeit.NORMAL);
        gm.starteSpiel();
        assertFalse(gm.spielerAngriff(0, 0, 0));
    }

    @Test
    public void neuesSpielSetztAllesZurueck() {
        gm.initialisiereSpiel(PlayerType.TEST_PLAYER, Schwierigkeit.NORMAL);
        gm.starteSpiel();
        gm.weiter();
        kaempfen();
        gm.weiter();
        gm.weiter();
        assertEquals(1, gm.getWorld().getAktuelleRaumNummer());

        gm.initialisiereSpiel(PlayerType.SHIELD_FIGHTER, Schwierigkeit.SCHWER);
        gm.starteSpiel();
        assertEquals(0, gm.getWorld().getAktuelleRaumNummer());
        assertEquals(PlayerType.SHIELD_FIGHTER, gm.getPlayer().getPlayerType());
        assertEquals(Schwierigkeit.SCHWER, gm.getSchwierigkeit());
        assertEquals(Spielphase.STORY, gm.getPhase());
    }

    @Test
    public void hpZustandKommtNachDerSchadensNachricht() {
        gm.initialisiereSpiel(PlayerType.SWORD_FIGHTER, Schwierigkeit.NORMAL);
        gm.starteSpiel();
        gm.weiter();
        kaempfen();
        // jedes "nimmt X Schaden." muss direkt von einem zustand gefolgt werden
        for (int i = 0; i < ereignisse.size(); i++) {
            if (ereignisse.get(i) instanceof Ereignis.Nachricht n && n.text().contains(" Schaden.")) {
                assertTrue(ereignisse.get(i + 1) instanceof Ereignis.Zustand, "kein zustand nach: " + n.text());
            }
        }
    }

    @Test
    public void verlorenGibtGameOver() {
        // auf schwer verliert der schwertkrieger fast immer, ersten seed nehmen bei dem er stirbt
        for (long seed = 1; seed < 100; seed++) {
            ereignisse.clear();
            gm = new GameManager(new Wuerfel(seed));
            gm.setListener(ereignisse::add);
            gm.initialisiereSpiel(PlayerType.SWORD_FIGHTER, Schwierigkeit.SCHWER);
            gm.starteSpiel();
            while (gm.getPhase() != Spielphase.GAME_OVER && gm.getPhase() != Spielphase.SIEG) {
                gm.weiter();
                kaempfen();
            }
            if (gm.getPhase() == Spielphase.GAME_OVER) {
                assertEquals(0, gm.getPlayer().getLifeTotal());
                assertEquals(Spielphase.GAME_OVER, letztePhase().phase());
                gm.weiter();
                assertEquals(Spielphase.GAME_OVER, gm.getPhase());
                return;
            }
        }
        throw new AssertionError("kein einziges game over gefunden");
    }
}
