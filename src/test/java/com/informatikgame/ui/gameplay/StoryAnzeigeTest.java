package com.informatikgame.ui.gameplay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class StoryAnzeigeTest {

    @Test
    public void zeilenKommenNacheinander() {
        StoryAnzeige story = new StoryAnzeige();
        story.setzeText("eins\n\nzwei\n\ndrei", 0);
        assertEquals(5, story.getZeilen().size()); // mit leerzeilen zwischen den absätzen
        assertEquals(1, story.sichtbareZeilen(0));
        assertFalse(story.istFertig(0));
        assertTrue(story.istFertig(10_000));
    }

    @Test
    public void enterZeigtSofortAlles() {
        StoryAnzeige story = new StoryAnzeige();
        story.setzeText("ein langer text ".repeat(20), 0);
        story.alleZeigen();
        assertTrue(story.istFertig(0));
        assertEquals(story.getZeilen().size(), story.sichtbareZeilen(0));
    }

    @Test
    public void neuerTextFaengtVonVorneAn() {
        StoryAnzeige story = new StoryAnzeige();
        story.setzeText("a\n\nb", 0);
        story.alleZeigen();
        story.setzeText("c\n\nd", 0);
        assertFalse(story.istFertig(0));
    }
}
