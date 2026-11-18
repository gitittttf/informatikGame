package com.informatikgame.ui;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;

/**
 * Zeichen-Hilfsmethoden die von allen Screens und Panels benutzt werden
 */
public final class Zeichnen {

    private Zeichnen() {
    }

    /**
     * Box mit doppeltem Rahmen, innen gefüllt
     */
    public static void box(TextGraphics graphics, int x, int y, int width, int height,
            TextColor borderColor, TextColor fillColor) {
        graphics.setForegroundColor(borderColor);
        graphics.setBackgroundColor(fillColor);

        // Ecken
        graphics.setCharacter(x, y, '╔');
        graphics.setCharacter(x + width - 1, y, '╗');
        graphics.setCharacter(x, y + height - 1, '╚');
        graphics.setCharacter(x + width - 1, y + height - 1, '╝');

        // Horizontale Linien
        for (int i = x + 1; i < x + width - 1; i++) {
            graphics.setCharacter(i, y, '═');
            graphics.setCharacter(i, y + height - 1, '═');
        }

        // Vertikale Linien
        for (int i = y + 1; i < y + height - 1; i++) {
            graphics.setCharacter(x, i, '║');
            graphics.setCharacter(x + width - 1, i, '║');
        }

        // Innenraum füllen
        for (int i = y + 1; i < y + height - 1; i++) {
            for (int j = x + 1; j < x + width - 1; j++) {
                graphics.setCharacter(j, i, ' ');
            }
        }
    }

    public static void zentriert(TextGraphics graphics, TerminalSize size, String text, int y) {
        int x = (size.getColumns() - text.length()) / 2;
        graphics.putString(new TerminalPosition(Math.max(0, x), y), text);
    }

    /**
     * Ganzen Bildschirm mit einer Hintergrundfarbe füllen
     */
    public static void hintergrund(TextGraphics graphics, TerminalSize size, TextColor farbe) {
        graphics.setBackgroundColor(farbe);
        graphics.fillRectangle(new TerminalPosition(0, 0), size, ' ');
    }

    /**
     * HP-Leiste: "HP: ████████░░░░ 20/36"
     */
    public static void lebensbalken(TextGraphics graphics, int x, int y, int width,
            String label, int current, int max, TextColor color) {
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(new TerminalPosition(x, y), label + ":");

        int barWidth = Math.max(1, width - label.length() - 10);
        int begrenzt = Math.max(0, Math.min(current, max));
        int filledWidth = max > 0 ? (begrenzt * barWidth) / max : 0;

        graphics.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
        for (int i = 0; i < barWidth; i++) {
            graphics.setCharacter(x + label.length() + 2 + i, y, '█');
        }

        graphics.setForegroundColor(color);
        for (int i = 0; i < filledWidth; i++) {
            graphics.setCharacter(x + label.length() + 2 + i, y, '█');
        }

        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(new TerminalPosition(x + label.length() + 3 + barWidth, y), begrenzt + "/" + max);
    }

    /**
     * Text an Wortgrenzen umbrechen
     */
    public static List<String> umbrechen(String text, int breite) {
        List<String> zeilen = new ArrayList<>();
        StringBuilder zeile = new StringBuilder();
        for (String wort : text.trim().split("\\s+")) {
            if (wort.isEmpty()) {
                continue;
            }
            if (zeile.length() > 0 && zeile.length() + wort.length() + 1 > breite) {
                zeilen.add(zeile.toString());
                zeile = new StringBuilder();
            }
            if (zeile.length() > 0) {
                zeile.append(' ');
            }
            zeile.append(wort);
        }
        if (zeile.length() > 0) {
            zeilen.add(zeile.toString());
        }
        return zeilen;
    }

    /**
     * Ja/Nein Dialog in der Mitte vom Bildschirm
     */
    public static void dialog(TextGraphics graphics, TerminalSize size, String frage) {
        String hinweis = "J = Ja   |   N / ESC = Nein";
        int width = Math.max(frage.length(), hinweis.length()) + 8;
        int height = 7;
        int x = (size.getColumns() - width) / 2;
        int y = (size.getRows() - height) / 2;

        box(graphics, x, y, width, height, TextColor.ANSI.YELLOW, new TextColor.RGB(20, 20, 0));
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        zentriert(graphics, size, frage, y + 2);
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        zentriert(graphics, size, hinweis, y + 4);
        graphics.setBackgroundColor(ScreenManager.BACKGROUND_COLOR);
    }
}
