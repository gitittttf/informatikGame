package com.informatikgame.ui;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;

/**
 * Abstrakte Basisklasse für alle Game-Screens
 */
public abstract class GameScreen {

    protected ScreenManager screenManager;
    protected int animationFrame = 0;
    protected long lastUpdateTime = System.currentTimeMillis();

    public void setScreenManager(ScreenManager manager) {
        this.screenManager = manager;
    }

    /**
     * Wird beim Screenwechsel aufgerufen
     */
    public abstract void initialize();

    /**
     * Rendert den Screen
     */
    public abstract void render(TextGraphics graphics);

    /**
     * Verarbeitet Eingaben (ESC kommt über onEscape)
     */
    public abstract void handleInput(KeyStroke keyStroke);

    /**
     * Update methode für Animationen
     */
    public void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime > 100) { // Alle 100ms
            animationFrame++;
            lastUpdateTime = currentTime;
        }
    }

    /**
     * Wird bei ESC aufgerufen
     *
     * @return true wenn das Spiel beendet werden soll
     */
    public boolean onEscape() {
        return false; // Standard: nicht beenden
    }

    /**
     * Hilfsmethode: Zeichnet Text zentriert
     */
    protected void drawCentered(TextGraphics graphics, String text, int y) {
        Zeichnen.zentriert(graphics, screenManager.getSize(), text, y);
    }

    /**
     * Hilfsmethode: Zeichnet eine Box mit Rahmen
     */
    protected void drawBox(TextGraphics graphics, int x, int y, int width, int height,
            TextColor borderColor, TextColor fillColor) {
        Zeichnen.box(graphics, x, y, width, height, borderColor, fillColor);
    }

    /**
     * Hilfsmethode: J/N Dialog
     */
    protected void drawDialog(TextGraphics graphics, String frage) {
        Zeichnen.dialog(graphics, screenManager.getSize(), frage);
    }

    /**
     * true wenn die Taste J (ja) war
     */
    protected static boolean istJa(KeyStroke keyStroke) {
        Character c = keyStroke.getCharacter();
        return c != null && (c == 'j' || c == 'J' || c == 'y' || c == 'Y');
    }

    /**
     * true wenn die Taste N (nein) war
     */
    protected static boolean istNein(KeyStroke keyStroke) {
        Character c = keyStroke.getCharacter();
        return c != null && (c == 'n' || c == 'N');
    }
}
