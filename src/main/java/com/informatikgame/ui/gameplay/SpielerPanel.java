package com.informatikgame.ui.gameplay;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.informatikgame.core.AnzeigeZustand;
import com.informatikgame.entities.Player;
import com.informatikgame.ui.ScreenManager;
import com.informatikgame.ui.Zeichnen;

/**
 * Linkes Panel: HP, Werte und die Kampf-Eingabe
 */
final class SpielerPanel {

    private SpielerPanel() {
    }

    static void zeichne(TextGraphics graphics, int x, int y, int width, int height,
            AnzeigeZustand zustand, Player player, boolean imKampf, boolean eingabeMoeglich, KampfEingabe eingabe) {
        Zeichnen.box(graphics, x, y, width - 1, height, ScreenManager.PRIMARY_COLOR, ScreenManager.BACKGROUND_COLOR);

        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(new TerminalPosition(x + 2, y), "[ " + player.getPlayerType().anzeigeName.toUpperCase() + " ]");

        Zeichnen.lebensbalken(graphics, x + 2, y + 2, width - 6, "HP",
                zustand.spielerHp(), zustand.spielerMaxHp(), TextColor.ANSI.RED);

        graphics.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
        graphics.putString(new TerminalPosition(x + 2, y + 4), String.format("Angriff %d   Verteidigung %d",
                player.getAttack(), player.getDefense()));
        graphics.putString(new TerminalPosition(x + 2, y + 5), String.format("Rüstung %d   Schaden %d+%dW6",
                player.getArmourValue(), player.getDamage(), player.getNumW6()));
        graphics.putString(new TerminalPosition(x + 2, y + 6), String.format("Finte %d   Wuchtschlag %d",
                player.getFinteLevel(), player.getWuchtschlagLevel()));

        if (!imKampf) {
            return;
        }

        int instructionY = y + 8;
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        if (!eingabeMoeglich) {
            graphics.putString(new TerminalPosition(x + 2, instructionY), "Warte" + ".".repeat((int) (System.currentTimeMillis() / 300 % 4)));
            graphics.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
            graphics.putString(new TerminalPosition(x + 2, instructionY + 1), "ENTER: Log überspringen");
            return;
        }

        int anzahlGegner = zustand.gegner().size();
        switch (eingabe.getSchritt()) {
            case ZIEL -> {
                graphics.putString(new TerminalPosition(x + 2, instructionY), "Wähle Gegner:");
                graphics.putString(new TerminalPosition(x + 2, instructionY + 1), "← → oder 1-" + anzahlGegner);
                graphics.putString(new TerminalPosition(x + 2, instructionY + 2), "ENTER: Weiter");
            }
            case FINTE -> {
                graphics.putString(new TerminalPosition(x + 2, instructionY), "Finte Level:");
                graphics.putString(new TerminalPosition(x + 2, instructionY + 1), "← → oder 0-" + player.getFinteLevel());
                graphics.putString(new TerminalPosition(x + 2, instructionY + 2), "ENTER: Weiter");
                graphics.putString(new TerminalPosition(x + 2, instructionY + 3), "ESC: Zurück");
            }
            case WUCHTSCHLAG -> {
                graphics.putString(new TerminalPosition(x + 2, instructionY), "Wuchtschlag Level:");
                graphics.putString(new TerminalPosition(x + 2, instructionY + 1), "← → oder 0-" + player.getWuchtschlagLevel());
                graphics.putString(new TerminalPosition(x + 2, instructionY + 2), "ENTER: Angriff!");
                graphics.putString(new TerminalPosition(x + 2, instructionY + 3), "ESC: Zurück");
            }
        }

        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(new TerminalPosition(x + 2, instructionY + 5),
                String.format("Ziel: %d, Finte: %d, Wuchtschlag: %d",
                        eingabe.getZiel() + 1, eingabe.getFinte(), eingabe.getWuchtschlag()));
    }
}
