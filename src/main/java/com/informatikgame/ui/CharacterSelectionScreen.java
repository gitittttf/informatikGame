package com.informatikgame.ui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.informatikgame.entities.Werte;
import com.informatikgame.world.PlayerType;

public class CharacterSelectionScreen extends MenueScreen {

    private static final PlayerType[] AUSWAHL = {PlayerType.SWORD_FIGHTER, PlayerType.SHIELD_FIGHTER};

    private static final String[] TITLE_ART = {
        " ▄████▄   ██░ ██  ▄▄▄       ██▀███   ▄▄▄       ▄████▄  ▄▄▄█████▓▓█████  ██▀███    ██████ ",
        "▒██▀ ▀█  ▓██░ ██▒▒████▄    ▓██ ▒ ██▒▒████▄    ▒██▀ ▀█  ▓  ██▒ ▓▒▓█   ▀ ▓██ ▒ ██▒▒██    ▒ ",
        "▒▓█    ▄ ▒██▀▀██░▒██  ▀█▄  ▓██ ░▄█ ▒▒██  ▀█▄  ▒▓█    ▄ ▒ ▓██░ ▒░▒███   ▓██ ░▄█ ▒░ ▓██▄   ",
        "▒▓▓▄ ▄██▒░▓█ ░██ ░██▄▄▄▄██ ▒██▀▀█▄  ░██▄▄▄▄██ ▒▓▓▄ ▄██▒░ ▓██▓ ░ ▒▓█  ▄ ▒██▀▀█▄    ▒   ██▒",
        "▒ ▓███▀ ░░▓█▒░██▓ ▓█   ▓██▒░██▓ ▒██▒ ▓█   ▓██▒▒ ▓███▀ ░  ▒██▒ ░ ░▒████▒░██▓ ▒██▒▒██████▒▒"
    };

    private final String[] optionen;
    private SchwierigkeitScreen schwierigkeitScreen;

    public CharacterSelectionScreen() {
        optionen = new String[AUSWAHL.length];
        for (int i = 0; i < AUSWAHL.length; i++) {
            optionen[i] = "► " + AUSWAHL[i].anzeigeName;
        }
    }

    public void setSchwierigkeitScreen(SchwierigkeitScreen schwierigkeitScreen) {
        this.schwierigkeitScreen = schwierigkeitScreen;
    }

    @Override
    protected String[] getTitelArt() {
        return TITLE_ART;
    }

    @Override
    protected String getUntertitel() {
        return "Wähle einen Charakter aus";
    }

    @Override
    protected String[] getOptionen() {
        return optionen;
    }

    @Override
    protected int getPartikelAnzahl() {
        return 50;
    }

    @Override
    protected void partikelZuruecksetzen(Partikel p, boolean start) {
        // weiße sterne die schräg durchs bild fliegen
        TerminalSize size = screenManager.getSize();
        p.x = (int) (Math.random() * size.getColumns());
        p.y = (int) (Math.random() * size.getRows());
        p.dx = 1 + (int) (Math.random() * 2);
        p.dy = 1 + (int) (Math.random() * 2);

        char[] symbols = {'*', '·', '•'};
        p.symbol = symbols[(int) (Math.random() * symbols.length)];
        int white = 50 + (int) (Math.random() * 150);
        p.color = new TextColor.RGB(white, white, white);
    }

    @Override
    protected void zeichneZusatz(TextGraphics graphics, int y) {
        // werte vom ausgewählten charakter anzeigen
        Werte w = AUSWAHL[selectedOption].werte;
        graphics.setForegroundColor(ScreenManager.SECONDARY_COLOR);
        drawCentered(graphics, String.format("Leben %d  |  Rüstung %d  |  Initiative %d  |  Angriff %d  |  Verteidigung %d",
                w.lebenspunkte(), w.ruestung(), w.initiative(), w.angriff(), w.verteidigung()), y);
        drawCentered(graphics, String.format("Schaden %d + %dW6  |  Finte bis Level %d  |  Wuchtschlag bis Level %d",
                w.schaden(), w.anzahlW6(), w.finteLevel(), w.wuchtschlagLevel()), y + 1);
    }

    @Override
    protected void optionAusgewaehlt(int index) {
        schwierigkeitScreen.setPlayerType(AUSWAHL[index]);
        screenManager.switchToScreen("schwierigkeit");
    }

    @Override
    public boolean onEscape() {
        screenManager.switchToScreen("menu");
        return false;
    }
}
