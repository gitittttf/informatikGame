package com.informatikgame.ui.gameplay;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.informatikgame.core.AnzeigeZustand;
import com.informatikgame.core.Ereignis;
import com.informatikgame.core.NachrichtenTyp;
import com.informatikgame.core.Spielphase;

public class KampfLogTest {

    private long zeit;
    private KampfLog log;
    private final List<Ereignis> angewendet = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        zeit = 1000;
        log = new KampfLog(() -> zeit);
        angewendet.clear();
    }

    private void nachricht(String text, long pause) {
        log.hinzufuegen(new Ereignis.Nachricht(text, NachrichtenTyp.INFO, pause));
    }

    private void aktualisieren() {
        log.aktualisieren(angewendet::add);
    }

    private List<String> texte() {
        return log.getZeilen().stream().map(KampfLog.Zeile::text).toList();
    }

    @Test
    public void nachrichtenErscheinenMitPause() {
        nachricht("a", 0);
        nachricht("b", 500);
        aktualisieren();
        assertEquals(List.of("a"), texte());

        zeit += 499;
        aktualisieren();
        assertEquals(List.of("a"), texte());

        zeit += 1;
        aktualisieren();
        assertEquals(List.of("a", "b"), texte());
        assertTrue(log.istLeer());
    }

    @Test
    public void phaseErstWennAlleNachrichtenDavorDurchSind() {
        // das war der alte bug: kampfende hat die letzten nachrichten gelöscht
        nachricht("Spieler greift an", 0);
        nachricht("Zombie wurde besiegt!", 400);
        log.hinzufuegen(new Ereignis.Phase(Spielphase.RAUM_ENDE_STORY, "story", 1000));

        aktualisieren();
        assertTrue(angewendet.isEmpty());

        zeit += 400;
        aktualisieren();
        assertEquals(List.of("Spieler greift an", "Zombie wurde besiegt!"), texte());
        assertTrue(angewendet.isEmpty());

        zeit += 1000;
        aktualisieren();
        assertEquals(1, angewendet.size());
        assertTrue(angewendet.get(0) instanceof Ereignis.Phase);
    }

    @Test
    public void zustandKommtZusammenMitDerNachricht() {
        AnzeigeZustand neu = new AnzeigeZustand(10, 36, List.of());
        nachricht("Spieler nimmt 5 Schaden.", 300);
        log.hinzufuegen(new Ereignis.Zustand(neu, 0));

        aktualisieren();
        assertTrue(angewendet.isEmpty()); // hp noch nicht vorher updaten

        zeit += 300;
        aktualisieren();
        assertEquals(List.of("Spieler nimmt 5 Schaden."), texte());
        assertEquals(1, angewendet.size());
    }

    @Test
    public void allesAnzeigenUeberspringtPausen() {
        nachricht("a", 1000);
        nachricht("b", 1000);
        log.hinzufuegen(new Ereignis.Phase(Spielphase.GAME_OVER, null, 1000));
        log.allesAnzeigen(angewendet::add);
        assertEquals(List.of("a", "b"), texte());
        assertEquals(1, angewendet.size());
        assertTrue(log.istLeer());
    }

    @Test
    public void leerenFuerNeuesSpiel() {
        nachricht("altes spiel", 0);
        nachricht("noch was", 5000);
        aktualisieren();
        log.leeren();
        assertTrue(log.getZeilen().isEmpty());
        assertTrue(log.istLeer());
    }

    @Test
    public void logWirdNichtUnendlichLang() {
        for (int i = 0; i < 200; i++) {
            nachricht("n" + i, 0);
        }
        aktualisieren();
        assertEquals(50, log.getZeilen().size());
        assertEquals("n199", log.getZeilen().get(49).text());
    }
}
