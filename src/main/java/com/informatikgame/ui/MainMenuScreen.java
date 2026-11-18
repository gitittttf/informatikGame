package com.informatikgame.ui;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

/**
 * Hauptmenü mit animiertem ASCII-Art und Menü-Optionen
 */
public class MainMenuScreen extends MenueScreen {

    private static final String[] MENU_OPTIONS = {
        "► Neues Spiel starten",
        "► Einstellungen",
        "► Spiel beenden"
    };

    private static final String[] TITLE_ART = {
        " ▓█████▄  █    ██  ███▄    █   ▄████ ▓█████  ▒█████   ███▄    █ ",
        " ▒██▀ ██▌ ██  ▓██▒ ██ ▀█   █  ██▒ ▀█▒▓█   ▀ ▒██▒  ██▒ ██ ▀█   █ ",
        " ░██   █▌▓██  ▒██░▓██  ▀█ ██▒▒██░▄▄▄░▒███   ▒██░  ██▒▓██  ▀█ ██▒",
        " ░▓█▄   ▌▓▓█  ░██░▓██▒  ▐▌██▒░▓█  ██▓▒▓█  ▄ ▒██   ██░▓██▒  ▐▌██▒",
        " ░▒████▓ ▒▒█████▓ ▒██░   ▓██░░▒▓███▀▒░▒████▒░ ████▓▒░▒██░   ▓██░"
    };

    private boolean beendenFragen = false;

    @Override
    public void initialize() {
        super.initialize();
        beendenFragen = false;
    }

    @Override
    protected String[] getTitelArt() {
        return TITLE_ART;
    }

    @Override
    protected String getUntertitel() {
        return "Von Paul, Benedikt und Simon";
    }

    @Override
    protected String[] getOptionen() {
        return MENU_OPTIONS;
    }

    @Override
    protected String getSteuerung() {
        return "↑↓ Navigation | ENTER Auswählen | ESC Beenden";
    }

    @Override
    protected void optionAusgewaehlt(int index) {
        switch (index) {
            case 0 ->
                screenManager.switchToScreen("characterSelection");
            case 1 ->
                screenManager.switchToScreen("settings");
            case 2 ->
                screenManager.stopRunning();
            default -> {
            }
        }
    }

    @Override
    public void render(TextGraphics graphics) {
        super.render(graphics);
        if (beendenFragen) {
            drawDialog(graphics, "Spiel wirklich beenden?");
        }
    }

    @Override
    public void handleInput(KeyStroke keyStroke) {
        if (beendenFragen) {
            if (istJa(keyStroke) || keyStroke.getKeyType() == KeyType.Enter) {
                screenManager.stopRunning();
            } else if (istNein(keyStroke)) {
                beendenFragen = false;
            }
            return;
        }
        super.handleInput(keyStroke);
    }

    @Override
    public boolean onEscape() {
        // erstes ESC fragt nach, zweites ESC bricht die frage ab
        beendenFragen = !beendenFragen;
        return false;
    }
}
