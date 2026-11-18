package com.informatikgame.ui.gameplay;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.informatikgame.core.AnzeigeZustand;
import com.informatikgame.ui.ScreenManager;
import com.informatikgame.ui.Zeichnen;

/**
 * Rechtes Panel: ausgewählter Gegner oben, alle Gegner unten
 */
final class InfoPanel {

    private InfoPanel() {
    }

    static void zeichne(TextGraphics graphics, int x, int y, int width, int height,
            AnzeigeZustand zustand, int ziel, boolean imKampf) {
        Zeichnen.box(graphics, x, y, width - 1, height, ScreenManager.PRIMARY_COLOR, ScreenManager.BACKGROUND_COLOR);

        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(new TerminalPosition(x + 2, y), "[ INFO ]");

        var gegner = zustand.gegner();
        if (!imKampf || gegner.isEmpty()) {
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(new TerminalPosition(x + 2, y + 2), imKampf ? "Alle Gegner besiegt!" : "Erkunde den Raum...");
            return;
        }

        int topHalfHeight = (height / 2) - 3;
        int bottomHalfY = y + topHalfHeight;

        graphics.setForegroundColor(ScreenManager.PRIMARY_COLOR);
        for (int i = 1; i < width - 2; i++) {
            graphics.setCharacter(x + i, bottomHalfY, '═');
        }
        graphics.setCharacter(x, bottomHalfY, '╠');
        graphics.setCharacter(x + width - 2, bottomHalfY, '╣');

        if (ziel >= 0 && ziel < gegner.size()) {
            AnzeigeZustand.GegnerAnzeige ausgewaehlt = gegner.get(ziel);
            graphics.setForegroundColor(TextColor.ANSI.CYAN);
            graphics.putString(new TerminalPosition(x + 2, y + 2), "Aktueller Gegner:");
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(new TerminalPosition(x + 2, y + 4), ausgewaehlt.name());
            Zeichnen.lebensbalken(graphics, x + 2, y + 6, width - 6, "HP",
                    ausgewaehlt.hp(), ausgewaehlt.maxHp(), TextColor.ANSI.RED);
        }

        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(new TerminalPosition(x + 2, bottomHalfY + 1), "Alle Gegner:");
        for (int i = 0; i < gegner.size(); i++) {
            int lineY = bottomHalfY + 3 + i;
            if (lineY >= y + height - 1) {
                break;
            }
            AnzeigeZustand.GegnerAnzeige g = gegner.get(i);
            graphics.setForegroundColor(i == ziel ? TextColor.ANSI.CYAN : TextColor.ANSI.WHITE);
            graphics.putString(new TerminalPosition(x + 2, lineY),
                    String.format("%d. %s (%d/%d HP)", i + 1, g.name(), g.hp(), g.maxHp()));
        }
    }
}
