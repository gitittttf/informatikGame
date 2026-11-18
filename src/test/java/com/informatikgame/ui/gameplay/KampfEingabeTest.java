package com.informatikgame.ui.gameplay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class KampfEingabeTest {

    private static final KeyStroke LINKS = new KeyStroke(KeyType.ArrowLeft);
    private static final KeyStroke RECHTS = new KeyStroke(KeyType.ArrowRight);
    private static final KeyStroke ENTER = new KeyStroke(KeyType.Enter);

    private KampfEingabe eingabe;

    @BeforeEach
    public void setUp() {
        eingabe = new KampfEingabe();
    }

    private boolean taste(KeyStroke key) {
        return eingabe.taste(key, 3, 2, 1);
    }

    private static KeyStroke zahl(char c) {
        return new KeyStroke(c, false, false);
    }

    @Test
    public void dreiSchritteBisZumAngriff() {
        assertEquals(KampfEingabe.Schritt.ZIEL, eingabe.getSchritt());
        assertFalse(taste(RECHTS));
        assertFalse(taste(ENTER));
        assertEquals(KampfEingabe.Schritt.FINTE, eingabe.getSchritt());
        assertFalse(taste(RECHTS));
        assertFalse(taste(ENTER));
        assertEquals(KampfEingabe.Schritt.WUCHTSCHLAG, eingabe.getSchritt());
        assertTrue(taste(ENTER));
        assertEquals(1, eingabe.getZiel());
        assertEquals(1, eingabe.getFinte());
        assertEquals(0, eingabe.getWuchtschlag());
    }

    @Test
    public void grenzenWerdenEingehalten() {
        taste(LINKS);
        assertEquals(0, eingabe.getZiel());
        for (int i = 0; i < 10; i++) {
            taste(RECHTS);
        }
        assertEquals(2, eingabe.getZiel());
        taste(ENTER);
        taste(zahl('9'));
        assertEquals(0, eingabe.getFinte());
        taste(zahl('2'));
        assertEquals(2, eingabe.getFinte());
    }

    @Test
    public void zahlenBeimZielAbEins() {
        taste(zahl('3'));
        assertEquals(2, eingabe.getZiel());
        taste(zahl('0'));
        assertEquals(2, eingabe.getZiel());
    }

    @Test
    public void escGehtEinenSchrittZurueck() {
        assertFalse(eingabe.zurueck());
        taste(ENTER);
        taste(ENTER);
        assertTrue(eingabe.zurueck());
        assertEquals(KampfEingabe.Schritt.FINTE, eingabe.getSchritt());
        assertTrue(eingabe.zurueck());
        assertEquals(KampfEingabe.Schritt.ZIEL, eingabe.getSchritt());
        assertFalse(eingabe.zurueck());
    }

    @Test
    public void zielRutschtNachWennGegnerSterben() {
        taste(zahl('3'));
        eingabe.zielBegrenzen(2);
        assertEquals(1, eingabe.getZiel());
        eingabe.zielBegrenzen(0);
        assertEquals(0, eingabe.getZiel());
    }

    @Test
    public void neuerZugBehaeltZiel() {
        taste(zahl('2'));
        taste(ENTER);
        taste(RECHTS);
        eingabe.neuerZug();
        assertEquals(KampfEingabe.Schritt.ZIEL, eingabe.getSchritt());
        assertEquals(1, eingabe.getZiel());
        assertEquals(0, eingabe.getFinte());
    }
}
