package com.informatikgame.ui.gameplay;

import java.util.List;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.informatikgame.ui.ScreenManager;
import com.informatikgame.ui.Zeichnen;
import com.informatikgame.world.RoomType;

/**
 * Karte vom Dungeon. Aktueller Raum ist gelb, geschaffte Räume grau.
 */
final class KartenAnzeige {

    private static final String KARTE
            = """
              +-------------------------------------------------[KARTE]----------------------------------------------------+
              |                                                                                                            |
              |    +--------------+                          +------------------------------+                              |
              |   /                \\     +--------------+    |                              |                              |
              |  +                  +----+              +----+                              |                              |
              |  |    [Eingang]     #    #    [Flur]    #    #        [Bibliothek]          +--------------------+         |
              |  +                  +----+              +----+                              #                    |         |
              |   \\                /     +--------------+    +------------------------------+--------------+     |         |
              |    +--------------+                                                                        |     |         |
              |                                           +--------------------------------------+         |     |         |
              |           +---+                           |                                      |   +-----+--#--+--+      |
              |       +--+     +--+      +----------------+                                      +---+              |      |
              |     -+             +-   /                 #                                      #   #[Speisekammer]|      |
              |    |                 +-+   +--------------+            [Speisesaal]              +---+              |      |
              |    |     [Labor]     #    /               |                                      |   +--------------+      |
              |    |                 +---+                |                                      |                         |
              |     -+             +-                     |                                      |                         |
              |       +--+     +--+                       +--------------------------------------+                         |
              |           +-#-+                                                                                            |
              |           |   |                                                                                            |
              |           |   |                                            +------------------------------------+          |
              |       +---+   +---------------------------------+      +--+                                      +--+      |
              |       |                                         |    -+                                              +-    |
              |       |                                         +---+                                                  |   |
              |       |              [Korridor]                 #   #                   [Boss Raum]                    |   |
              |       |                                         +---+                                                  |   |
              |       |                                         |    -+                                              +-    |
              |       +-----------------------------------------+      +--+                                      +--+      |
              |                                                            +------------------------------------+          |
              +------------------------------------------------------------------------------------------------------------+""";

    private KartenAnzeige() {
    }

    /**
     * @param geschafft Anzahl der Räume die schon geschafft sind (von vorne)
     */
    static void zeichne(TextGraphics graphics, TerminalSize size, List<RoomType> raeume,
            int aktuell, int geschafft, int animationFrame) {
        Zeichnen.hintergrund(graphics, size, new TextColor.RGB(10, 10, 20));

        String[] mapLines = KARTE.split("\n");
        int startY = (size.getRows() - mapLines.length) / 2;
        int startX = (size.getColumns() - mapLines[0].length()) / 2;

        graphics.setForegroundColor(ScreenManager.PRIMARY_COLOR);
        for (int i = 0; i < mapLines.length; i++) {
            graphics.putString(new TerminalPosition(startX, startY + i), mapLines[i]);
        }

        // raum-labels einfärben
        for (int r = 0; r < raeume.size(); r++) {
            String label = raeume.get(r).kartenLabel;
            if (r == aktuell && r >= geschafft) {
                graphics.setForegroundColor(animationFrame % 30 < 20 ? TextColor.ANSI.YELLOW_BRIGHT : TextColor.ANSI.YELLOW);
            } else if (r < geschafft) {
                graphics.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
            } else {
                continue;
            }
            for (int i = 0; i < mapLines.length; i++) {
                int spalte = mapLines[i].indexOf(label);
                if (spalte >= 0) {
                    graphics.putString(new TerminalPosition(startX + spalte, startY + i), label);
                    break;
                }
            }
        }

        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        Zeichnen.zentriert(graphics, size, "Gelb = du bist hier   |   Grau = geschafft", startY + mapLines.length + 1);
        if (animationFrame % 30 < 15) {
            Zeichnen.zentriert(graphics, size, "Drücke X, M oder ESC um zurückzukehren", size.getRows() - 3);
        }
    }
}
