package com.informatikgame.ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;

public class CharacterSelectionScreen extends GameScreen {

    private int selectedOption = 0;
    private final String[] characterSelectionOptions = {
        "► Schwertkrieger",
        "► Schildkrieger"
    };

    private final String[] titleArt = {
        " ▄████▄   ██░ ██  ▄▄▄       ██▀███   ▄▄▄       ▄████▄  ▄▄▄█████▓▓█████  ██▀███    ██████ ",
        "▒██▀ ▀█  ▓██░ ██▒▒████▄    ▓██ ▒ ██▒▒████▄    ▒██▀ ▀█  ▓  ██▒ ▓▒▓█   ▀ ▓██ ▒ ██▒▒██    ▒ ",
        "▒▓█    ▄ ▒██▀▀██░▒██  ▀█▄  ▓██ ░▄█ ▒▒██  ▀█▄  ▒▓█    ▄ ▒ ▓██░ ▒░▒███   ▓██ ░▄█ ▒░ ▓██▄   ",
        "▒▓▓▄ ▄██▒░▓█ ░██ ░██▄▄▄▄██ ▒██▀▀█▄  ░██▄▄▄▄██ ▒▓▓▄ ▄██▒░ ▓██▓ ░ ▒▓█  ▄ ▒██▀▀█▄    ▒   ██▒",
        "▒ ▓███▀ ░░▓█▒░██▓ ▓█   ▓██▒░██▓ ▒██▒ ▓█   ▓██▒▒ ▓███▀ ░  ▒██▒ ░ ░▒████▒░██▓ ▒██▒▒██████▒▒"
    };

    private final String subtitle = "Wähle ein Charakter aus";

    // Partikelsystem für Hintergrundanimation
    private Particle[] particles;

    class Particle {

        int x, y;
        char symbol;
        TextColor color;
        int speed;

        @SuppressWarnings("OverridableMethodCallInConstructor")
        Particle() {
            reset();
        }

        void reset() {
            TerminalSize size = screenManager.getSize();
            // Startpositionen
            x = (int) (Math.random() * size.getColumns());
            y = 0;
            // Geschwindigkeit
            speed = 1 + (int) (Math.random() * 5);

            // Symbole
            char[] symbols = {'+', '"', '█', '*', '·', '•'};
            symbol = symbols[(int) (Math.random() * symbols.length)];

            // Farbe
            int green = 50 + (int) (Math.random() * 150);
            color = new TextColor.RGB(0, green, 0);
        }

        void update() {
            y += speed;
            TerminalSize size = screenManager.getSize();
            if (y >= size.getRows()) {
                reset();
            }
        }
    }

    @Override
    public void initialize() {
        // Initialisiere Partikelsystem
        particles = new Particle[30];
        for (int i = 0; i < particles.length; i++) {
            particles[i] = new Particle();
            // Verteile über den Bildschirm
            particles[i].y = (int) (Math.random() * screenManager.getSize().getRows());
        }
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = screenManager.getSize();

        // Hintergrund-Partikel rendern (Matrix-Regen-Effekt)
        for (Particle p : particles) {
            graphics.setForegroundColor(p.color);
            graphics.setCharacter(p.x, p.y, p.symbol);
        }

        // Titel mit Glitch-Effekt
        int titleY = 5;
        for (int i = 0; i < titleArt.length; i++) {
            // Zufälliger Glitch-Effekt
            if (animationFrame % 30 == 0 && Math.random() < 0.1) {
                // Glitch: verschiebe Zeile leicht
                int offset = (int) (Math.random() * 3) - 1;
                graphics.setForegroundColor(new TextColor.RGB(153, 153, 0)); // TODO richtige farbe finden
                drawCentered(graphics, titleArt[i], titleY + i + offset);
            } else {
                // Normal: grüne Farbe mit Pulsieren
                // int brightness2 = 150 + (int) (Math.cos(animationFrame * 0.2 + i) * 70);
                int brightness = 150 + (int) (Math.sin(animationFrame * 0.2 + i) * 70);
                graphics.setForegroundColor(new TextColor.RGB(0, brightness, 0));
                drawCentered(graphics, titleArt[i], titleY + i);
            }
        }

        // Untertitel mit Typewriter-Effekt
        graphics.setForegroundColor(ScreenManager.SECONDARY_COLOR);
        String displaySubtitle = subtitle;
        if (animationFrame < subtitle.length()) {
            displaySubtitle = subtitle.substring(0, animationFrame);
        }
        drawCentered(graphics, displaySubtitle, titleY + titleArt.length + 2);

        // Menü Box
        int menuY = titleY + titleArt.length + 5;
        int menuWidth = 40;
        int menuHeight = characterSelectionOptions.length + 4;
        int menuX = (size.getColumns() - menuWidth) / 2;

        // Box mit animiertem Rahmen
        TextColor borderColor = animationFrame % 20 < 10
                ? ScreenManager.PRIMARY_COLOR : ScreenManager.SECONDARY_COLOR;
        drawBox(graphics, menuX, menuY, menuWidth, menuHeight,
                borderColor, ScreenManager.BACKGROUND_COLOR);

        // Menü Optionen
        for (int i = 0; i < characterSelectionOptions.length; i++) {
            int optionY = menuY + 2 + i;
            String option = characterSelectionOptions[i];

            if (i == selectedOption) {
                // Ausgewählte Option ist animiert und hervorgehoben
                graphics.setBackgroundColor(new TextColor.RGB(0, 50, 0));
                graphics.setForegroundColor(TextColor.ANSI.YELLOW);

                // Animierter Pfeil
                String arrow = (animationFrame % 10 < 5) ? "►►► " : ">>>>";
                graphics.putString(new TerminalPosition(menuX + 2, optionY),
                        arrow + option.substring(1) + " " + arrow);
            } else {
                // Normale Option
                graphics.setBackgroundColor(ScreenManager.BACKGROUND_COLOR);
                graphics.setForegroundColor(ScreenManager.TEXT_COLOR);
                graphics.putString(new TerminalPosition(menuX + 5, optionY), option);
            }
        }

        // Footer mit Steuerungshinweisen
        graphics.setBackgroundColor(ScreenManager.BACKGROUND_COLOR);
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        String controls = "↑↓ Navigation | ENTER Auswählen | ESC Zurück";
        drawCentered(graphics, controls, size.getRows() - 1);
    }

    @Override
    public void handleInput(KeyStroke keyStroke) {
        if (null != keyStroke.getKeyType()) {
            switch (keyStroke.getKeyType()) {
                case ArrowUp ->
                    selectedOption = (selectedOption - 1 + characterSelectionOptions.length) % characterSelectionOptions.length;
                case ArrowDown ->
                    selectedOption = (selectedOption + 1) % characterSelectionOptions.length;
                case Enter ->
                    executeOption();
                default -> {
                }
            }
        }
    }

    // TODO: FALSCHE OPTIONEN, character selection implementieren
    private void executeOption() {
        switch (selectedOption) {
            case 0 -> // Neues Spiel
                screenManager.switchToScreen("game");
            case 1 -> { // Sword
            }
            case 2 -> // Einstellungen
                screenManager.switchToScreen("settings");
            case 5 -> // Beenden
                screenManager.stopRunning();
        }
        // 
    }

    @Override
    public void update() {
        super.update();

        // Update Partikel
        for (Particle p : particles) {
            p.update();
        }
    }

    @Override
    public boolean onEscape() {
        return selectedOption == 5; // Nur wenn "Beenden" ausgewählt
    }
}
