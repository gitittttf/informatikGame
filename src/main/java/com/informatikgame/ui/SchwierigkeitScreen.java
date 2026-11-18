package com.informatikgame.ui;

import java.util.List;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.informatikgame.core.GameManager;
import com.informatikgame.world.PlayerType;
import com.informatikgame.world.Schwierigkeit;

/**
 * Auswahl der Schwierigkeit nach der Charakterwahl
 */
public class SchwierigkeitScreen extends MenueScreen {

    private static final String[] TITLE_ART = {
        "╔══════════════════════════════════════╗",
        "║            SCHWIERIGKEIT             ║",
        "╚══════════════════════════════════════╝"
    };

    private final Schwierigkeit[] auswahl = Schwierigkeit.values();
    private final String[] optionen;
    private GameManager gameManager;
    private PlayerType playerType = PlayerType.SWORD_FIGHTER;

    public SchwierigkeitScreen() {
        optionen = new String[auswahl.length];
        for (int i = 0; i < auswahl.length; i++) {
            optionen[i] = "► " + auswahl[i].anzeigeName;
        }
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void setPlayerType(PlayerType playerType) {
        this.playerType = playerType;
    }

    @Override
    protected String[] getTitelArt() {
        return TITLE_ART;
    }

    @Override
    protected String getUntertitel() {
        return playerType.anzeigeName + " - wie schwer soll es werden?";
    }

    @Override
    protected String[] getOptionen() {
        return optionen;
    }

    @Override
    protected int getStartOption() {
        return Schwierigkeit.NORMAL.ordinal();
    }

    @Override
    protected void zeichneZusatz(TextGraphics graphics, int y) {
        graphics.setForegroundColor(ScreenManager.SECONDARY_COLOR);
        List<String> zeilen = Zeichnen.umbrechen(auswahl[selectedOption].beschreibung, 60);
        for (int i = 0; i < zeilen.size(); i++) {
            drawCentered(graphics, zeilen.get(i), y + i);
        }
    }

    @Override
    protected void optionAusgewaehlt(int index) {
        gameManager.initialisiereSpiel(playerType, auswahl[index]);
        screenManager.switchToScreen("game");
    }

    @Override
    public boolean onEscape() {
        screenManager.switchToScreen("characterSelection");
        return false;
    }
}
