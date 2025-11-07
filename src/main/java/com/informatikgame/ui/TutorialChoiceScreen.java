package com.informatikgame.ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.informatikgame.core.GameManager;

/**
 * Screen to choose whether to start the tutorial or skip it
 */
public class TutorialChoiceScreen extends GameScreen {

    private int selectedOption = 0;
    private final String[] options = new String[]{
        "► Tutorial starten",
        "► Überspringen"
    };

    private final String[] titleArt = {
        "▄▄▄█████▓ █    ██ ▄▄▄█████▓ ▒█████   ██▀███   ██▓ ▄▄▄       ██▓    ",
        "▓  ██▒ ▓▒ ██  ▓██▒▓  ██▒ ▓▒▒██▒  ██▒▓██ ▒ ██▒▓██▒▒████▄    ▓██▒    ",
        "▒ ▓██░ ▒░▓██  ▒██░▒ ▓██░ ▒░▒██░  ██▒▓██ ░▄█ ▒▒██▒▒██  ▀█▄  ▒██░    ",
        "░ ▓██▓ ░ ▓▓█  ░██░░ ▓██▓ ░ ▒██   ██░▒██▀▀█▄  ░██░░██▄▄▄▄██ ▒██░    ",
        "  ▒██▒ ░ ▒▒█████▓   ▒██▒ ░ ░ ████▓▒░░██▓ ▒██▒░██░ ▓█   ▓██▒░██████▒"
    };

    private final String subtitle = "Möchtest du das Tutorial starten?";

    private GameManager gameManager;

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void initialize() {
        // Initialisiere Partikelsystem
        particles = new Particle[50];
        for (int i = 0; i < particles.length; i++) {
            particles[i] = new Particle();
            // Verteile über den Bildschirm
            particles[i].y = (int) (Math.random() * screenManager.getSize().getRows());
        }
    }

    // Partikelsystem für Hintergrundanimation
    private Particle[] particles;

    class Particle {

        int x, y;
        char symbol;
        TextColor color;
        int speedY;
        int speedX;

        @SuppressWarnings("OverridableMethodCallInConstructor")
        Particle() {
            reset();
        }

        void reset() {
            TerminalSize size = screenManager.getSize();
            // Startpositionen
            x = (int) (Math.random() * size.getColumns());
            y = (int) (Math.random() * size.getRows());
            // Geschwindigkeit
            speedY = 1 + (int) (Math.random() * 0.02);
            speedX = 1 + (int) (Math.random() * 0.02);

            // Symbole
            char[] symbols = {'*', '·', '•'};
            symbol = symbols[(int) (Math.random() * symbols.length)];

            // Farbe
            int affected = 50 + (int) (Math.random() * 150);
            int zero = 0;
            color = new TextColor.RGB(affected, zero, affected);
        }

        void update() {
            y += speedY;
            x -= speedX;
            TerminalSize size = screenManager.getSize();
            if (y >= size.getRows() || x >= size.getColumns()) {
                reset();
            }
        }
    }

    @Override
    public void render(TextGraphics g) {
        TerminalSize size = screenManager.getSize();

        // Hintergrund-Partikel rendern (Matrix-Regen-Effekt)
        for (Particle p : particles) {
            g.setForegroundColor(p.color);
            g.setCharacter(p.x, p.y, p.symbol);
        }

        // Titel mit Glitch-Effekt
        int titleY = 5;
        for (int i = 0; i < titleArt.length; i++) {
            // Zufälliger Glitch-Effekt
            if (animationFrame % 30 == 0 && Math.random() < 0.1) {
                // Glitch: verschiebe Zeile leicht
                int offset = (int) (Math.random() * 3) - 1;
                g.setForegroundColor(new TextColor.RGB(153, 153, 0));
                drawCentered(g, titleArt[i], titleY + i + offset);
            } else {
                // Normal: grüne Farbe mit Pulsieren
                // int brightness2 = 150 + (int) (Math.cos(animationFrame * 0.2 + i) * 70);
                int brightness = 150 + (int) (Math.sin(animationFrame * 0.2 + i) * 70);
                g.setForegroundColor(new TextColor.RGB(brightness, 0, brightness));
                drawCentered(g, titleArt[i], titleY + i);
            }
            drawCentered(g, titleArt[i], titleY + i);
        }

        // Untertitel mit Typewriter-Effekt
        g.setForegroundColor(new TextColor.RGB(255, 0, 255));
        String displaySubtitle = subtitle;
        if (animationFrame < subtitle.length()) {
            displaySubtitle = subtitle.substring(0, animationFrame);
        }
        drawCentered(g, displaySubtitle, titleY + titleArt.length + 2);

        int boxWidth = 40;
        int boxHeight = options.length + 4;
        int boxX = (size.getColumns() - boxWidth) / 2;
        int boxY = titleY + titleArt.length + 5;

        // Box
        TextColor borderColor = animationFrame % 20 < 10
                ? new TextColor.RGB(255, 0, 255) : new TextColor.RGB(0, 0, 255);
        drawBox(g, boxX, boxY, boxWidth, boxHeight, borderColor, ScreenManager.BACKGROUND_COLOR);

        // Options
        for (int i = 0; i < options.length; i++) {
            int y = boxY + 2 + i;
            String option = options[i];

            if (i == selectedOption) {
                // Ausgewählte Option ist animiert und hervorgehoben
                g.setBackgroundColor(new TextColor.RGB(0, 0, 50));
                g.setForegroundColor(new TextColor.RGB(255, 0, 255));

                // Animierter Pfeil
                String arrow = (animationFrame % 10 < 5) ? "►►► " : ">>>>";
                g.putString(new TerminalPosition(boxX + 2, y),
                        arrow + option.substring(1) + " " + arrow);
            } else {
                g.setBackgroundColor(ScreenManager.BACKGROUND_COLOR);
                g.setForegroundColor(ScreenManager.TEXT_COLOR);
                g.putString(new TerminalPosition(boxX + 5, y), option);
            }
        }

        // Footer
        g.setBackgroundColor(new TextColor.RGB(0, 0, 0));
        g.setForegroundColor(new TextColor.RGB(255, 0, 255));
        String controls = "↑↓ Navigation | ENTER Auswählen | ESC Zurück";
        drawCentered(g, controls, size.getRows() - 2);
    }

    @Override
    public void handleInput(KeyStroke keyStroke) {
        if (keyStroke == null || keyStroke.getKeyType() == null) {
            return;
        }
        switch (keyStroke.getKeyType()) {
            case ArrowUp ->
                selectedOption = (selectedOption - 1 + options.length) % options.length;
            case ArrowDown ->
                selectedOption = (selectedOption + 1) % options.length;
            case Enter ->
                executeOption();
            default -> {
            }
        }
    }

    @Override
    public void update() {
        super.update();
        // Update Partikel
        for (Particle p : particles) {
            p.update();
        }
    }

    private void executeOption() {
        if (selectedOption == 0) {
            if (gameManager != null) {
                gameManager.setTutorialModeEnabled(true);
            }
            screenManager.switchToScreen("characterSelection");
        } else {
            if (gameManager != null) {
                gameManager.setTutorialModeEnabled(false);
            }
            screenManager.switchToScreen("characterSelection");
        }
    }

    @Override
    public boolean onEscape() {
        screenManager.switchToScreen("menu");
        return false;
    }
}
