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
    private final String[] options = new String[] {
            "► Tutorial starten",
            "► Überspringen"
    };

    private GameManager gameManager;

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void initialize() {
        // nothing special
    }

    @Override
    public void render(TextGraphics g) {
        TerminalSize size = screenManager.getSize();

        // Title
        g.setForegroundColor(ScreenManager.SECONDARY_COLOR);
        drawCentered(g, "TUTORIAL", 5);

        // Description
        g.setForegroundColor(TextColor.ANSI.WHITE);
        drawCentered(g, "Möchtest du das Tutorial starten?", 7);

        int boxWidth = 44;
        int boxHeight = options.length + 4;
        int boxX = (size.getColumns() - boxWidth) / 2;
        int boxY = 11;

        // Box
        drawBox(g, boxX, boxY, boxWidth, boxHeight, ScreenManager.PRIMARY_COLOR, ScreenManager.BACKGROUND_COLOR);

        // Options
        for (int i = 0; i < options.length; i++) {
            int y = boxY + 2 + i;
            String option = options[i];

            if (i == selectedOption) {
                g.setBackgroundColor(new TextColor.RGB(0, 50, 0));
                g.setForegroundColor(TextColor.ANSI.YELLOW);
                String arrow = (animationFrame % 10 < 5) ? "►►► " : ">>>>";
                g.putString(new TerminalPosition(boxX + 2, y), arrow + option.substring(1) + " " + arrow);
            } else {
                g.setBackgroundColor(ScreenManager.BACKGROUND_COLOR);
                g.setForegroundColor(ScreenManager.TEXT_COLOR);
                g.putString(new TerminalPosition(boxX + 5, y), option);
            }
        }

        // Footer
        g.setBackgroundColor(ScreenManager.BACKGROUND_COLOR);
        g.setForegroundColor(TextColor.ANSI.YELLOW);
        drawCentered(g, "↑↓ Navigation | ENTER Auswählen | ESC Zurück", size.getRows() - 2);
    }

    @Override
    public void handleInput(KeyStroke keyStroke) {
        if (keyStroke == null || keyStroke.getKeyType() == null) return;
        switch (keyStroke.getKeyType()) {
            case ArrowUp -> selectedOption = (selectedOption - 1 + options.length) % options.length;
            case ArrowDown -> selectedOption = (selectedOption + 1) % options.length;
            case Enter -> executeOption();
            default -> {}
        }
    }

    private void executeOption() {
        if (selectedOption == 0) {
            if (gameManager != null) gameManager.setTutorialModeEnabled(true);
            screenManager.switchToScreen("characterSelection");
        } else {
            if (gameManager != null) gameManager.setTutorialModeEnabled(false);
            screenManager.switchToScreen("characterSelection");
        }
    }

    @Override
    public boolean onEscape() {
        screenManager.switchToScreen("menu");
        return false;
    }
}
