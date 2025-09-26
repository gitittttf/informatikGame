package com.informatikgame.ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
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
     * Verarbeitet Eingaben
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
        TerminalSize size = screenManager.getSize();
        int x = (size.getColumns() - text.length()) / 2;
        graphics.putString(new TerminalPosition(x, y), text);
    }

    /**
     * Hilfsmethode: Zeichnet eine Box mit Rahmen
     */
    protected void drawBox(TextGraphics graphics, int x, int y, int width, int height,
            TextColor borderColor, TextColor fillColor) {
        graphics.setForegroundColor(borderColor);
        graphics.setBackgroundColor(fillColor);

        // Ecken
        graphics.setCharacter(x, y, '╔');
        graphics.setCharacter(x + width - 1, y, '╗');
        graphics.setCharacter(x, y + height - 1, '╚');
        graphics.setCharacter(x + width - 1, y + height - 1, '╝');

        // Horizontale Linien
        for (int i = x + 1; i < x + width - 1; i++) {
            graphics.setCharacter(i, y, '═');
            graphics.setCharacter(i, y + height - 1, '═');
        }

        // Vertikale Linien
        for (int i = y + 1; i < y + height - 1; i++) {
            graphics.setCharacter(x, i, '║');
            graphics.setCharacter(x + width - 1, i, '║');
        }

        // Innenraum füllen
        graphics.setBackgroundColor(fillColor);
        for (int i = y + 1; i < y + height - 1; i++) {
            for (int j = x + 1; j < x + width - 1; j++) {
                graphics.setCharacter(j, i, ' ');
            }
        }
    }

    /**
     * Zeichnet animierten Text (pulsierend)
     */
    protected void drawAnimatedText(TextGraphics graphics, String text, int x, int y,
            TextColor baseColor) {
        // Pulsierender Effekt basierend auf animationFrame
        int brightness = 100 + (int) (Math.sin(animationFrame * 0.3) * 100);
        brightness = Math.max(50, Math.min(255, brightness));

        if (baseColor == TextColor.ANSI.GREEN) {
            graphics.setForegroundColor(new TextColor.RGB(0, brightness, 0));
        } else if (baseColor == TextColor.ANSI.RED) {
            graphics.setForegroundColor(new TextColor.RGB(brightness, 0, 0));
        } else {
            graphics.setForegroundColor(new TextColor.RGB(brightness, brightness, brightness));
        }

        graphics.putString(new TerminalPosition(x, y), text);
    }
}
