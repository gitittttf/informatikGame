package com.informatikgame.ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;

/**
 * Gemeinsame Basis für alle Menüs: Titel-Art mit Glitch, Untertitel mit
 * Typewriter-Effekt, Partikel im Hintergrund und eine Box mit Optionen.
 */
public abstract class MenueScreen extends GameScreen {

    protected int selectedOption = 0;
    private Partikel[] partikel = new Partikel[0];

    protected abstract String[] getTitelArt();

    protected abstract String getUntertitel();

    /**
     * Optionen, jeweils mit "► " am Anfang
     */
    protected abstract String[] getOptionen();

    protected abstract void optionAusgewaehlt(int index);

    protected int getStartOption() {
        return 0;
    }

    protected int getPartikelAnzahl() {
        return 30;
    }

    protected String getSteuerung() {
        return "↑↓ Navigation | ENTER Auswählen | ESC Zurück";
    }

    /**
     * Platz unter der Menü-Box für zusätzliche Infos
     */
    protected void zeichneZusatz(TextGraphics graphics, int y) {
    }

    /**
     * Partikel neu setzen. Standard: grüner Matrix-Regen von oben.
     *
     * @param start true beim ersten Mal, dann irgendwo auf dem Bildschirm
     */
    protected void partikelZuruecksetzen(Partikel p, boolean start) {
        TerminalSize size = screenManager.getSize();
        p.x = (int) (Math.random() * size.getColumns());
        p.y = start ? (int) (Math.random() * size.getRows()) : 0;
        p.dx = 0;
        p.dy = 1 + (int) (Math.random() * 3);

        char[] symbols = {'░', '▒', '▓', '█', '*', '·', '•'};
        p.symbol = symbols[(int) (Math.random() * symbols.length)];
        p.color = new TextColor.RGB(0, 50 + (int) (Math.random() * 150), 0);
    }

    protected static class Partikel {

        int x, y, dx, dy;
        char symbol;
        TextColor color;
    }

    @Override
    public void initialize() {
        selectedOption = getStartOption();
        animationFrame = 0;
        partikel = new Partikel[getPartikelAnzahl()];
        for (int i = 0; i < partikel.length; i++) {
            partikel[i] = new Partikel();
            partikelZuruecksetzen(partikel[i], true);
        }
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = screenManager.getSize();

        // Hintergrund-Partikel
        for (Partikel p : partikel) {
            graphics.setForegroundColor(p.color);
            graphics.setCharacter(p.x, p.y, p.symbol);
        }

        // Titel mit Glitch-Effekt
        String[] titelArt = getTitelArt();
        int titleY = 5;
        for (int i = 0; i < titelArt.length; i++) {
            if (animationFrame % 30 == 0 && Math.random() < 0.1) {
                // Glitch: zeile kurz verschoben und gelb
                int offset = (int) (Math.random() * 3) - 1;
                graphics.setForegroundColor(new TextColor.RGB(153, 153, 0));
                drawCentered(graphics, titelArt[i], titleY + i + offset);
            } else {
                // Normal: grün mit Pulsieren
                int brightness = 150 + (int) (Math.sin(animationFrame * 0.2 + i) * 70);
                graphics.setForegroundColor(new TextColor.RGB(0, brightness, 0));
                drawCentered(graphics, titelArt[i], titleY + i);
            }
        }

        // Untertitel mit Typewriter-Effekt
        String untertitel = getUntertitel();
        graphics.setForegroundColor(ScreenManager.SECONDARY_COLOR);
        drawCentered(graphics, untertitel.substring(0, Math.min(animationFrame, untertitel.length())),
                titleY + titelArt.length + 2);

        // Menü Box
        String[] optionen = getOptionen();
        int menuY = titleY + titelArt.length + 5;
        int menuWidth = 40;
        int menuHeight = optionen.length + 4;
        int menuX = (size.getColumns() - menuWidth) / 2;

        TextColor borderColor = animationFrame % 20 < 10
                ? ScreenManager.PRIMARY_COLOR : ScreenManager.SECONDARY_COLOR;
        drawBox(graphics, menuX, menuY, menuWidth, menuHeight, borderColor, ScreenManager.BACKGROUND_COLOR);

        for (int i = 0; i < optionen.length; i++) {
            int optionY = menuY + 2 + i;
            String option = optionen[i];

            if (i == selectedOption) {
                graphics.setBackgroundColor(new TextColor.RGB(0, 50, 0));
                graphics.setForegroundColor(TextColor.ANSI.YELLOW);
                String arrow = (animationFrame % 10 < 5) ? "►►► " : ">>>>";
                graphics.putString(new TerminalPosition(menuX + 2, optionY), arrow + option.substring(1) + " " + arrow);
            } else {
                graphics.setBackgroundColor(ScreenManager.BACKGROUND_COLOR);
                graphics.setForegroundColor(ScreenManager.TEXT_COLOR);
                graphics.putString(new TerminalPosition(menuX + 5, optionY), option);
            }
        }
        graphics.setBackgroundColor(ScreenManager.BACKGROUND_COLOR);

        zeichneZusatz(graphics, menuY + menuHeight + 2);

        // Footer mit Steuerungshinweisen
        graphics.setBackgroundColor(ScreenManager.BACKGROUND_COLOR);
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        drawCentered(graphics, getSteuerung(), size.getRows() - 2);
    }

    @Override
    public void handleInput(KeyStroke keyStroke) {
        int anzahl = getOptionen().length;
        switch (keyStroke.getKeyType()) {
            case ArrowUp ->
                selectedOption = (selectedOption - 1 + anzahl) % anzahl;
            case ArrowDown ->
                selectedOption = (selectedOption + 1) % anzahl;
            case Enter ->
                optionAusgewaehlt(selectedOption);
            default -> {
            }
        }
    }

    @Override
    public void update() {
        super.update();

        TerminalSize size = screenManager.getSize();
        for (Partikel p : partikel) {
            p.x += p.dx;
            p.y += p.dy;
            if (p.y >= size.getRows() || p.x >= size.getColumns()) {
                partikelZuruecksetzen(p, false);
            }
        }
    }
}
