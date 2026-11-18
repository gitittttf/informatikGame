package com.informatikgame.ui.gameplay;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.informatikgame.ui.Zeichnen;

/**
 * Story im Vollbild, Zeile für Zeile eingeblendet. Mit Enter wird sofort
 * alles angezeigt.
 */
public class StoryAnzeige {

    private static final long MS_PRO_ZEILE = 500;
    private static final int ZEILEN_BREITE = 60;

    private final List<String> zeilen = new ArrayList<>();
    private long startZeit;
    private boolean alleSichtbar;

    public void setzeText(String text, long jetzt) {
        zeilen.clear();
        // absätze (leerzeilen) bleiben erhalten
        String[] absaetze = text.strip().split("\\n\\s*\\n");
        for (int i = 0; i < absaetze.length; i++) {
            if (i > 0) {
                zeilen.add("");
            }
            zeilen.addAll(Zeichnen.umbrechen(absaetze[i], ZEILEN_BREITE));
        }
        startZeit = jetzt;
        alleSichtbar = false;
    }

    public int sichtbareZeilen(long jetzt) {
        if (alleSichtbar) {
            return zeilen.size();
        }
        return (int) Math.min(zeilen.size(), 1 + (jetzt - startZeit) / MS_PRO_ZEILE);
    }

    public boolean istFertig(long jetzt) {
        return sichtbareZeilen(jetzt) >= zeilen.size();
    }

    public void alleZeigen() {
        alleSichtbar = true;
    }

    public List<String> getZeilen() {
        return List.copyOf(zeilen);
    }

    public void zeichne(TextGraphics graphics, TerminalSize size, long jetzt, int animationFrame) {
        Zeichnen.hintergrund(graphics, size, new TextColor.RGB(10, 10, 20));

        int sichtbar = sichtbareZeilen(jetzt);
        int storyWidth = Math.min(70, size.getColumns() - 10);
        int storyStartX = (size.getColumns() - storyWidth) / 2;
        int storyStartY = (size.getRows() - zeilen.size()) / 2 - 3;

        zeichneRahmen(graphics, storyStartX - 2, storyStartY - 2, storyWidth + 4, sichtbar + 4);

        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        for (int i = 0; i < sichtbar; i++) {
            String line = zeilen.get(i);
            int lineX = storyStartX + (storyWidth - line.length()) / 2;
            graphics.putString(new TerminalPosition(lineX, storyStartY + i), line);
        }

        zeichneHinweis(graphics, size, istFertig(jetzt), animationFrame);
    }

    private void zeichneRahmen(TextGraphics graphics, int x, int y, int width, int height) {
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
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

        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        for (int i = 2; i < width - 2; i += 4) {
            graphics.setCharacter(x + i, y, '◈');
            graphics.setCharacter(x + i, y + height - 1, '◈');
        }
    }

    private void zeichneHinweis(TextGraphics graphics, TerminalSize size, boolean fertig, int animationFrame) {
        int promptY = size.getRows() - 4;
        int promptX = 4;
        int promptWidth = size.getColumns() - 8;

        Zeichnen.box(graphics, promptX, promptY, promptWidth, 4, TextColor.ANSI.CYAN, new TextColor.RGB(0, 20, 40));

        if (fertig) {
            if (animationFrame % 30 < 15) {
                graphics.setForegroundColor(TextColor.ANSI.YELLOW);
                graphics.putString(new TerminalPosition(promptX + 2, promptY + 1), "►");
            }
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(new TerminalPosition(promptX + 4, promptY + 1), "Drücke ENTER um fortzufahren");
        } else {
            String text = "Story wird geladen ";
            graphics.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
            graphics.putString(new TerminalPosition(promptX + 2, promptY + 1), text);
            int dots = (animationFrame / 10) % 4;
            graphics.putString(new TerminalPosition(promptX + 2 + text.length(), promptY + 1),
                    ".".repeat(dots) + " ".repeat(3 - dots) + "  (ENTER: alles anzeigen)");
        }
        graphics.setBackgroundColor(new TextColor.RGB(10, 10, 20));
    }
}
