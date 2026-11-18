package com.informatikgame.ui.gameplay;

import java.util.List;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.informatikgame.core.AnzeigeZustand;
import com.informatikgame.ui.ScreenManager;
import com.informatikgame.ui.Zeichnen;
import com.informatikgame.world.RoomType;

/**
 * Mittleres Panel: im Kampf Spieler gegen den ausgewählten Gegner, sonst der
 * Raum mit Beschreibung und Bild
 */
final class KampfSzene {

    private static final String[] SPIELER_ART = {
        " ╔═╗ ",
        " ║♦║ ",
        " ╠═╣ ",
        " ║║║ ",
        " ╚╩╝ "
    };

    private KampfSzene() {
    }

    static void zeichne(TextGraphics graphics, int x, int y, int width, int height,
            boolean imKampf, AnzeigeZustand zustand, int ziel, RoomType raum, int animationFrame) {
        Zeichnen.box(graphics, x, y, width - 1, height, ScreenManager.PRIMARY_COLOR, ScreenManager.BACKGROUND_COLOR);

        if (imKampf) {
            graphics.setForegroundColor(TextColor.ANSI.RED);
            graphics.putString(new TerminalPosition(x + 2, y), "[ KAMPFZONE ]");
            zeichneKampf(graphics, x, y, width, height, zustand, ziel, animationFrame);
            return;
        }

        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(new TerminalPosition(x + 2, y), "[ " + raum.anzeigeName.toUpperCase() + " ]");

        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        List<String> lines = Zeichnen.umbrechen(raum.beschreibung, width - 6);
        for (int i = 0; i < lines.size() && i < 5; i++) {
            graphics.putString(new TerminalPosition(x + 3, y + 3 + i), lines.get(i));
        }

        String[] bild = raum.getBild();
        graphics.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
        for (int i = 0; i < bild.length && i < height - 10; i++) {
            int centerX = x + (width - bild[i].length()) / 2;
            graphics.putString(new TerminalPosition(centerX, y + 10 + i), bild[i]);
        }
    }

    private static void zeichneKampf(TextGraphics graphics, int x, int y, int width, int height,
            AnzeigeZustand zustand, int ziel, int animationFrame) {
        int centerY = y + height / 2;

        // spieler links, wackelt leicht
        int playerX = x + 5 + (animationFrame % 20 < 10 ? 1 : 0);
        graphics.setForegroundColor(ScreenManager.SECONDARY_COLOR);
        for (int i = 0; i < SPIELER_ART.length; i++) {
            graphics.putString(new TerminalPosition(playerX, centerY - 2 + i), SPIELER_ART[i]);
        }

        // kampf-effekt in der mitte
        if (animationFrame % 15 < 8) {
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            String[] effects = {"⚔", "✶", "✦", "◈"};
            graphics.putString(new TerminalPosition(x + width / 2 - 1, centerY), effects[(animationFrame / 15) % effects.length]);
        }

        // ausgewählter gegner rechts
        if (ziel >= 0 && ziel < zustand.gegner().size()) {
            zeichneGegner(graphics, x + width - 15, centerY - 2, zustand.gegner().get(ziel));
        }
    }

    private static void zeichneGegner(TextGraphics graphics, int x, int y, AnzeigeZustand.GegnerAnzeige gegner) {
        String[] art;
        if (gegner.name().contains("Mini")) {
            art = new String[]{
                " ╔═╗",
                " ║☠║",
                " ╚╬╝",
                "  ║ ",
                " ╱ ╲"
            };
        } else if (gegner.name().contains("Scientist")) {
            art = new String[]{
                " ╔═╗",
                " ║◉║",
                " ╠═╣",
                " ║║║",
                " ╚╩╝"
            };
        } else if (gegner.name().contains("Big")) {
            art = new String[]{
                "╔═══╗",
                "║ ☠ ║",
                "╠═══╣",
                "║║║║║",
                "╚╩═╩╝"
            };
        } else { // Boss
            art = new String[]{
                " ╔═══╗ ",
                "╔╣ ☠ ╠╗",
                "║╚═══╝║",
                "║ ║║║ ║",
                "╚═╩╩╩═╝"
            };
        }

        // farbe je nach restlichen hp
        TextColor color;
        if (gegner.hp() > gegner.maxHp() * 0.66) {
            color = TextColor.ANSI.RED;
        } else if (gegner.hp() > gegner.maxHp() * 0.33) {
            color = TextColor.ANSI.YELLOW;
        } else {
            color = TextColor.ANSI.BLACK_BRIGHT;
        }

        graphics.setForegroundColor(color);
        for (int i = 0; i < art.length; i++) {
            graphics.putString(new TerminalPosition(x, y + i), art[i]);
        }
    }
}
