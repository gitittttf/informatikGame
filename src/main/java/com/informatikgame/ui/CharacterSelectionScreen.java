package com.informatikgame.ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.informatikgame.core.GameManager;
import com.informatikgame.world.PlayerType;

public class CharacterSelectionScreen extends GameScreen {

    private GameManager gameManager;

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }
    private int selectedOption = 0;

    // Tutorial mode state
    private enum TutorialState {
        NOT_IN_TUTORIAL,
        EXPLAINING_SELECTION,
        EXPLAINING_STATS,
        AWAITING_CONFIRMATION,
        NORMAL_SELECTION
    }
    private TutorialState tutorialState = TutorialState.NOT_IN_TUTORIAL;
    private String tutorialText = "";
    private int tutorialStartFrame = 0;
    private boolean tutorialFirstStageCompleted = false;

    // Modal state for stats window
    private boolean showStatsModal = false;
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
            speedY = 1 + (int) (Math.random() * 0.2);
            speedX = 1 + (int) (Math.random() * 0.2);

            // Symbole
            char[] symbols = {'*', '·', '•'};
            symbol = symbols[(int) (Math.random() * symbols.length)];

            // Farbe
            int white = 50 + (int) (Math.random() * 150);
            color = new TextColor.RGB(white, white, white);
        }

        void update() {
            y += speedY;
            x += speedX;
            TerminalSize size = screenManager.getSize();
            if (y >= size.getRows() || x >= size.getColumns()) {
                reset();
            }
        }
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

        // Setup tutorial state if enabled
        if (gameManager != null && gameManager.isTutorialModeEnabled()) {
            tutorialState = TutorialState.EXPLAINING_SELECTION;
            tutorialFirstStageCompleted = false;
            tutorialText = "Wähle deinen Charakter für das Spiel. Nutze die Pfeiltasten, um zwischen den Charakteren zu wechseln. Jeder Charakter hat unterschiedliche Stärken und Schwächen.";
            tutorialStartFrame = animationFrame; // baseline for typewriter
        } else {
            tutorialState = TutorialState.NOT_IN_TUTORIAL;
        }
        showStatsModal = false;
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = screenManager.getSize();

        // Hintergrund-Partikel rendern (Matrix-Regen-Effekt)
        for (Particle p : particles) {
            graphics.setForegroundColor(p.color);
            graphics.setCharacter(p.x, p.y, p.symbol);
        }
        // Zusätzlicher, dezenter Scanline-Effekt
        int scanY = (animationFrame % Math.max(1, size.getRows()));
        graphics.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
        for (int x = 0; x < size.getColumns(); x++) {
            if (x % 2 == 0) {
                graphics.setCharacter(x, scanY, '·');
            }
        }

        // Titel mit Glitch-Effekt
        int titleY = 5;
        for (int i = 0; i < titleArt.length; i++) {
            // Zufälliger Glitch-Effekt
            if (animationFrame % 30 == 0 && Math.random() < 0.1) {
                // Glitch: verschiebe Zeile leicht
                int offset = (int) (Math.random() * 3) - 1;
                graphics.setForegroundColor(new TextColor.RGB(153, 153, 0));
                drawCentered(graphics, titleArt[i], titleY + i + offset);
            } else {
                // Normal: grüne Farbe mit Pulsieren
                // int brightness2 = 150 + (int) (Math.cos(animationFrame * 0.2 + i) * 70);
                int brightness = 150 + (int) (Math.sin(animationFrame * 0.2 + i) * 70);
                graphics.setForegroundColor(new TextColor.RGB(0, brightness, 0));
                drawCentered(graphics, titleArt[i], titleY + i);
            }
            drawCentered(graphics, titleArt[i], titleY + i);
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

        // Menü Optionen (deaktiviert, wenn Tutorialtext läuft)
        boolean enableSelection = (tutorialState == TutorialState.NOT_IN_TUTORIAL || tutorialState == TutorialState.NORMAL_SELECTION);
        for (int i = 0; i < characterSelectionOptions.length; i++) {
            int optionY = menuY + 2 + i;
            String option = characterSelectionOptions[i];

            if (i == selectedOption) {
                // Ausgewählte Option ist animiert und hervorgehoben
                graphics.setBackgroundColor(enableSelection ? new TextColor.RGB(0, 50, 0) : new TextColor.RGB(20, 20, 20));
                graphics.setForegroundColor(enableSelection ? TextColor.ANSI.YELLOW : TextColor.ANSI.BLACK_BRIGHT);

                // Animierter Pfeil
                String arrow = (animationFrame % 10 < 5) ? "►►► " : ">>>>";
                graphics.putString(new TerminalPosition(menuX + 2, optionY),
                        arrow + option.substring(1) + " " + arrow);
            } else {
                // Normale Option
                graphics.setBackgroundColor(ScreenManager.BACKGROUND_COLOR);
                graphics.setForegroundColor(enableSelection ? ScreenManager.TEXT_COLOR : TextColor.ANSI.BLACK_BRIGHT);
                graphics.putString(new TerminalPosition(menuX + 5, optionY), option);
            }
        }


        // Footer mit Steuerungshinweisen
        graphics.setBackgroundColor(ScreenManager.BACKGROUND_COLOR);
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        String controls = "↑↓ Navigation | ENTER Auswählen | → Stats | ESC Zurück";
        drawCentered(graphics, controls, size.getRows() - 2);

        // Tutorial-Textbox unten anzeigen, falls aktiv
        if (tutorialState == TutorialState.EXPLAINING_SELECTION || tutorialState == TutorialState.EXPLAINING_STATS || tutorialState == TutorialState.AWAITING_CONFIRMATION) {
            drawTutorialBox(graphics, size);
        }

        // Stats modal overlay
        if (showStatsModal) {
            drawStatsModal(graphics, size);
        }
    }

    @Override
    public void handleInput(KeyStroke keyStroke) {
        if (keyStroke == null || keyStroke.getKeyType() == null) {
            return;
        }

        // Modal interaction has priority
        if (showStatsModal) {
            if (keyStroke.getKeyType() == com.googlecode.lanterna.input.KeyType.Enter ||
                keyStroke.getKeyType() == com.googlecode.lanterna.input.KeyType.Escape ||
                keyStroke.getKeyType() == com.googlecode.lanterna.input.KeyType.ArrowLeft) {
                showStatsModal = false;
            }
            return;
        }

        // Block input during tutorial explanation (non-skippable)
        if (tutorialState == TutorialState.EXPLAINING_SELECTION || tutorialState == TutorialState.EXPLAINING_STATS) {
            return;
        }

        // Only allow ENTER to continue after tutorial text finished
        if (tutorialState == TutorialState.AWAITING_CONFIRMATION) {
            if (keyStroke.getKeyType() == com.googlecode.lanterna.input.KeyType.Enter) {
                if (!tutorialFirstStageCompleted) {
                    // Start second tutorial phase (explain stats open)
                    tutorialFirstStageCompleted = true;
                    tutorialState = TutorialState.EXPLAINING_STATS;
                    tutorialText = "Tipp: Öffne die Charakter-Statistiken mit der rechten Pfeiltaste (→). Dort siehst du Stärken und Schwächen deines Charakters.";
                    tutorialStartFrame = animationFrame;
                } else {
                    tutorialState = TutorialState.NORMAL_SELECTION;
                }
            }
            return;
        }

        switch (keyStroke.getKeyType()) {
            case ArrowUp ->
                selectedOption = (selectedOption - 1 + characterSelectionOptions.length) % characterSelectionOptions.length;
            case ArrowDown ->
                selectedOption = (selectedOption + 1) % characterSelectionOptions.length;
            case ArrowRight ->
                showStatsModal = true;
            case Enter ->
                executeOption();
            default -> {
            }
        }
    }

    private void executeOption() {
        PlayerType selectedType;
        selectedType = switch (selectedOption) {
            case 0 ->
                PlayerType.SWORD_FIGHTER;
            case 1 ->
                PlayerType.SHIELD_FIGHTER;
            default ->
                PlayerType.SWORD_FIGHTER;
        };

        // Initialize game with selected player type
        gameManager.initializeGameWithPlayer(selectedType);
        screenManager.switchToScreen("game");
    }

    @Override
    public void update() {
        super.update();

        // Update Partikel
        for (Particle p : particles) {
            p.update();
        }

        // Advance tutorial state when typewriter finished
        if (tutorialState == TutorialState.EXPLAINING_SELECTION || tutorialState == TutorialState.EXPLAINING_STATS) {
            int charsShown = Math.max(0, (animationFrame - tutorialStartFrame)) * 2; // ~20 cps
            if (tutorialText != null && charsShown >= tutorialText.length()) {
                tutorialState = TutorialState.AWAITING_CONFIRMATION;
            }
        }
    }

    @Override
    public boolean onEscape() {
        screenManager.switchToScreen("menu");
        return false;
    }

    // ===== Helper UI methods =====
    private PlayerType getSelectedPlayerType() {
        return switch (selectedOption) {
            case 0 -> PlayerType.SWORD_FIGHTER;
            case 1 -> PlayerType.SHIELD_FIGHTER;
            default -> PlayerType.SWORD_FIGHTER;
        };
    }

    private void drawStatsPanel(TextGraphics graphics, int menuX, int menuY, int menuWidth, int menuHeight, TerminalSize size) {
        int statsX = Math.min(size.getColumns() - 42, menuX + menuWidth + 6);
        int statsY = menuY;
        int statsWidth = Math.min(40, size.getColumns() - statsX - 2);
        int statsHeight = Math.max(menuHeight, 14);

        drawBox(graphics, statsX, statsY, statsWidth, statsHeight, ScreenManager.PRIMARY_COLOR, ScreenManager.BACKGROUND_COLOR);
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(new TerminalPosition(statsX + 2, statsY), "[ CHARAKTER-STATS ]");

        PlayerType type = getSelectedPlayerType();
        int y = statsY + 2;
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(new TerminalPosition(statsX + 2, y++), selectedOption == 0 ? "Schwertkrieger" : "Schildkrieger");
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(new TerminalPosition(statsX + 2, y++), String.format("HP: %d", type.lifeTotal));
        graphics.putString(new TerminalPosition(statsX + 2, y++), String.format("Rüstung: %d", type.armourValue));
        graphics.putString(new TerminalPosition(statsX + 2, y++), String.format("Initiative: %d", type.initiative));
        graphics.putString(new TerminalPosition(statsX + 2, y++), String.format("Angriff: %d", type.attack));
        graphics.putString(new TerminalPosition(statsX + 2, y++), String.format("Verteidigung: %d", type.defense));
        graphics.putString(new TerminalPosition(statsX + 2, y++), String.format("Schaden: %dW%d", type.damage, type.numW6));
        graphics.putString(new TerminalPosition(statsX + 2, y++), String.format("Finte Level: %d", type.finteLevel));
        graphics.putString(new TerminalPosition(statsX + 2, y++), String.format("Wuchtschlag Level: %d", type.wuchtschlagLevel));
    }

    private void drawTutorialBox(TextGraphics graphics, TerminalSize size) {
        // Typewriter effect calculation
        int charsShown = Math.max(0, (animationFrame - tutorialStartFrame)) * 2; // ~20 cps
        charsShown = Math.min(charsShown, tutorialText.length());
        String textToShow = tutorialText.substring(0, charsShown);

        // Calculate box width
        int boxWidth = Math.min(70, size.getColumns() - 10);
        int maxTextWidth = boxWidth - 8; // 4 chars padding on each side

        // Word-wrap the text to calculate required height
        String[] words = textToShow.split(" ");
        java.util.List<String> wrappedLines = new java.util.ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String w : words) {
            if (line.length() + w.length() + 1 > maxTextWidth) {
                if (line.length() > 0) {
                    wrappedLines.add(line.toString());
                    line = new StringBuilder();
                }
            }
            if (line.length() > 0) line.append(' ');
            line.append(w);
        }
        if (line.length() > 0) {
            wrappedLines.add(line.toString());
        }

        // Calculate dynamic box height based on text content (4 padding top/bottom + text lines)
        int boxHeight = Math.max(5, wrappedLines.size() + 4);
        int boxX = (size.getColumns() - boxWidth) / 2;
        int boxY = size.getRows() - boxHeight - 3;

        // Fill background area of the box with a dark shade (story-style)
        graphics.setBackgroundColor(new TextColor.RGB(10, 10, 20));
        for (int y = 0; y < boxHeight; y++) {
            for (int x = 0; x < boxWidth; x++) {
                graphics.setCharacter(boxX + x, boxY + y, ' ');
            }
        }
        // Story-style border for consistency with GameplayScreen
        drawStoryBorder(graphics, boxX, boxY, boxWidth, boxHeight);

        // Draw wrapped text with consistent padding (4 chars from left, 2 lines from top)
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        int tx = boxX + 4;
        int ty = boxY + 2;

        for (String wrappedLine : wrappedLines) {
            if (ty < boxY + boxHeight - 2) { // Keep 2 lines padding from bottom
                graphics.putString(new TerminalPosition(tx, ty++), wrappedLine);
            }
        }

        // Prompt when finished
        if (tutorialState == TutorialState.AWAITING_CONFIRMATION) {
            String prompt = "Drücke ENTER um fortzufahren";
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            drawCentered(graphics, prompt, boxY + boxHeight);
        }
    }

    private void drawDarkenedTutorialBox(TextGraphics graphics, TerminalSize size) {
        // Similar to drawTutorialBox but with darkened colors
        int charsShown = Math.max(0, (animationFrame - tutorialStartFrame)) * 2;
        charsShown = Math.min(charsShown, tutorialText.length());
        String textToShow = tutorialText.substring(0, charsShown);

        int boxWidth = Math.min(70, size.getColumns() - 10);
        int maxTextWidth = boxWidth - 8;

        String[] words = textToShow.split(" ");
        java.util.List<String> wrappedLines = new java.util.ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String w : words) {
            if (line.length() + w.length() + 1 > maxTextWidth) {
                if (line.length() > 0) {
                    wrappedLines.add(line.toString());
                    line = new StringBuilder();
                }
            }
            if (line.length() > 0) line.append(' ');
            line.append(w);
        }
        if (line.length() > 0) {
            wrappedLines.add(line.toString());
        }

        int boxHeight = Math.max(5, wrappedLines.size() + 4);
        int boxX = (size.getColumns() - boxWidth) / 2;
        int boxY = size.getRows() - boxHeight - 3;

        // Darkened background
        graphics.setBackgroundColor(new TextColor.RGB(3, 3, 7));
        for (int y = 0; y < boxHeight; y++) {
            for (int x = 0; x < boxWidth; x++) {
                graphics.setCharacter(boxX + x, boxY + y, ' ');
            }
        }
        // Darkened border
        drawDarkenedStoryBorder(graphics, boxX, boxY, boxWidth, boxHeight);

        // Darkened text
        graphics.setForegroundColor(new TextColor.RGB(70, 70, 70));
        int tx = boxX + 4;
        int ty = boxY + 2;

        for (String wrappedLine : wrappedLines) {
            if (ty < boxY + boxHeight - 2) {
                graphics.putString(new TerminalPosition(tx, ty++), wrappedLine);
            }
        }

        if (tutorialState == TutorialState.AWAITING_CONFIRMATION) {
            String prompt = "Drücke ENTER um fortzufahren";
            graphics.setForegroundColor(new TextColor.RGB(80, 80, 0));
            drawCentered(graphics, prompt, boxY + boxHeight);
        }
    }

    private void drawDarkenedStoryBorder(TextGraphics graphics, int x, int y, int width, int height) {
        graphics.setForegroundColor(new TextColor.RGB(20, 80, 80)); // Darkened cyan
        for (int i = 0; i < width; i++) {
            graphics.setCharacter(x + i, y, '═');
            graphics.setCharacter(x + i, y + height - 1, '═');
        }
        for (int i = 0; i < height; i++) {
            graphics.setCharacter(x, y + i, '║');
            graphics.setCharacter(x + width - 1, y + i, '║');
        }
        graphics.setCharacter(x, y, '╔');
        graphics.setCharacter(x + width - 1, y, '╗');
        graphics.setCharacter(x, y + height - 1, '╚');
        graphics.setCharacter(x + width - 1, y + height - 1, '╝');
        graphics.setForegroundColor(new TextColor.RGB(60, 60, 0)); // Darkened yellow
        for (int i = 2; i < width - 2; i += 4) {
            graphics.setCharacter(x + i, y, '◈');
            graphics.setCharacter(x + i, y + height - 1, '◈');
        }
    }

    // Copied style from GameplayScreen.drawStoryBorder for consistency
    private void drawStoryBorder(TextGraphics graphics, int x, int y, int width, int height) {
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        // Draw decorative border
        for (int i = 0; i < width; i++) {
            graphics.setCharacter(x + i, y, '═');
            graphics.setCharacter(x + i, y + height - 1, '═');
        }
        for (int i = 0; i < height; i++) {
            graphics.setCharacter(x, y + i, '║');
            graphics.setCharacter(x + width - 1, y + i, '║');
        }
        // Corners
        graphics.setCharacter(x, y, '╔');
        graphics.setCharacter(x + width - 1, y, '╗');
        graphics.setCharacter(x, y + height - 1, '╚');
        graphics.setCharacter(x + width - 1, y + height - 1, '╝');
        // Decorative elements
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        for (int i = 2; i < width - 2; i += 4) {
            graphics.setCharacter(x + i, y, '◈');
            graphics.setCharacter(x + i, y + height - 1, '◈');
        }
    }

    private void drawStatsModal(TextGraphics graphics, TerminalSize size) {
        // Re-render the background with darkened colors to simulate opacity
        // Darken particles
        for (Particle p : particles) {
            TextColor.RGB originalColor = (TextColor.RGB) p.color;
            int darkenFactor = 3; // Divide RGB values by this factor
            graphics.setForegroundColor(new TextColor.RGB(
                originalColor.getRed() / darkenFactor,
                originalColor.getGreen() / darkenFactor,
                originalColor.getBlue() / darkenFactor
            ));
            graphics.setCharacter(p.x, p.y, p.symbol);
        }

        // Darken the scanline effect
        int scanY = (animationFrame % Math.max(1, size.getRows()));
        graphics.setForegroundColor(new TextColor.RGB(20, 20, 20)); // Very dark gray
        for (int x = 0; x < size.getColumns(); x++) {
            if (x % 2 == 0) {
                graphics.setCharacter(x, scanY, '·');
            }
        }

        // Darken the title
        int titleY = 5;
        for (int i = 0; i < titleArt.length; i++) {
            int brightness = 150 + (int) (Math.sin(animationFrame * 0.2 + i) * 70);
            graphics.setForegroundColor(new TextColor.RGB(0, brightness / 4, 0)); // Divide by 4 to darken
            drawCentered(graphics, titleArt[i], titleY + i);
        }

        // Darken subtitle
        graphics.setForegroundColor(new TextColor.RGB(40, 40, 40)); // Darkened gray
        String displaySubtitle = subtitle;
        if (animationFrame < subtitle.length()) {
            displaySubtitle = subtitle.substring(0, animationFrame);
        }
        drawCentered(graphics, displaySubtitle, titleY + titleArt.length + 2);

        // Darken menu box
        int menuY = titleY + titleArt.length + 5;
        int menuWidth = 40;
        int menuHeight = characterSelectionOptions.length + 4;
        int menuX = (size.getColumns() - menuWidth) / 2;

        drawBox(graphics, menuX, menuY, menuWidth, menuHeight,
                new TextColor.RGB(30, 30, 30), new TextColor.RGB(5, 5, 5)); // Darkened colors

        // Darken menu options
        boolean enableSelection = (tutorialState == TutorialState.NOT_IN_TUTORIAL || tutorialState == TutorialState.NORMAL_SELECTION);
        for (int i = 0; i < characterSelectionOptions.length; i++) {
            int optionY = menuY + 2 + i;
            String option = characterSelectionOptions[i];

            if (i == selectedOption) {
                graphics.setBackgroundColor(enableSelection ? new TextColor.RGB(0, 15, 0) : new TextColor.RGB(7, 7, 7));
                graphics.setForegroundColor(enableSelection ? new TextColor.RGB(100, 100, 0) : new TextColor.RGB(30, 30, 30));
                String arrow = (animationFrame % 10 < 5) ? "►►► " : ">>>>";
                graphics.putString(new TerminalPosition(menuX + 2, optionY),
                        arrow + option.substring(1) + " " + arrow);
            } else {
                graphics.setBackgroundColor(new TextColor.RGB(5, 5, 5));
                graphics.setForegroundColor(enableSelection ? new TextColor.RGB(60, 60, 60) : new TextColor.RGB(30, 30, 30));
                graphics.putString(new TerminalPosition(menuX + 5, optionY), option);
            }
        }

        // Darken footer
        graphics.setBackgroundColor(new TextColor.RGB(5, 5, 5));
        graphics.setForegroundColor(new TextColor.RGB(80, 80, 0)); // Darkened yellow
        String controls = "↑↓ Navigation | ENTER Auswählen | → Stats | ESC Zurück";
        drawCentered(graphics, controls, size.getRows() - 2);

        // Darken tutorial box if visible
        if (tutorialState == TutorialState.EXPLAINING_SELECTION || tutorialState == TutorialState.EXPLAINING_STATS || tutorialState == TutorialState.AWAITING_CONFIRMATION) {
            drawDarkenedTutorialBox(graphics, size);
        }

        // Fixed modal size and centered position
        int modalWidth = Math.min(50, size.getColumns() - 10);
        int modalHeight = 16;
        int mx = (size.getColumns() - modalWidth) / 2;
        int my = (size.getRows() - modalHeight) / 2;

        // Modal background
        graphics.setBackgroundColor(new TextColor.RGB(10, 10, 20));
        for (int y = 0; y < modalHeight; y++) {
            for (int x = 0; x < modalWidth; x++) {
                graphics.setCharacter(mx + x, my + y, ' ');
            }
        }
        // Modal border styled like story box
        drawStoryBorder(graphics, mx, my, modalWidth, modalHeight);

        // Content: character stats
        PlayerType type = getSelectedPlayerType();
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        String title = selectedOption == 0 ? "Schwertkrieger" : "Schildkrieger";
        graphics.putString(new TerminalPosition(mx + 2, my + 1), title);

        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        int y = my + 3;
        graphics.putString(new TerminalPosition(mx + 2, y++), String.format("HP: %d", type.lifeTotal));
        graphics.putString(new TerminalPosition(mx + 2, y++), String.format("Rüstung: %d", type.armourValue));
        graphics.putString(new TerminalPosition(mx + 2, y++), String.format("Initiative: %d", type.initiative));
        graphics.putString(new TerminalPosition(mx + 2, y++), String.format("Angriff: %d", type.attack));
        graphics.putString(new TerminalPosition(mx + 2, y++), String.format("Verteidigung: %d", type.defense));
        graphics.putString(new TerminalPosition(mx + 2, y++), String.format("Schaden: %dW%d", type.damage, type.numW6));
        graphics.putString(new TerminalPosition(mx + 2, y++), String.format("Finte Level: %d", type.finteLevel));
        graphics.putString(new TerminalPosition(mx + 2, y++), String.format("Wuchtschlag Level: %d", type.wuchtschlagLevel));

        // Close button at bottom
        String closeText = "[ ENTER ] Schließen";
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(new TerminalPosition(mx + (modalWidth - closeText.length()) / 2, my + modalHeight - 2), closeText);
    }
}
