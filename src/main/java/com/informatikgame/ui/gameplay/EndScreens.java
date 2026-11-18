package com.informatikgame.ui.gameplay;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.informatikgame.ui.Zeichnen;

/**
 * Game Over und Sieg
 */
final class EndScreens {

    private static final String[] GAME_OVER_ART = {
        " ▄████  ▄▄▄       ███▄ ▄███▓▓█████     ▒█████   ██▒   █▓▓█████  ██▀███  ",
        "██▒ ▀█▒▒████▄    ▓██▒▀█▀ ██▒▓█   ▀    ▒██▒  ██▒▓██░   █▒▓█   ▀ ▓██ ▒ ██▒",
        "▒██░▄▄▄░▒██  ▀█▄  ▓██    ▓██░▒███      ▒██░  ██▒ ▓██  █▒░▒███   ▓██ ░▄█ ▒",
        "░▓█  ██▓░██▄▄▄▄██ ▒██    ▒██ ▒▓█  ▄    ▒██   ██░  ▒██ █░░▒▓█  ▄ ▒██▀▀█▄  ",
        "░▒▓███▀▒ ▓█   ▓██▒▒██▒   ░██▒░▒████▒   ░ ████▓▒░   ▒▀█░  ░▒████▒░██▓ ▒██▒"
    };

    private static final String[] VICTORY_ART = {
        "██╗   ██╗██╗ ██████╗████████╗ ██████╗ ██████╗ ██╗   ██╗",
        "██║   ██║██║██╔════╝╚══██╔══╝██╔═══██╗██╔══██╗╚██╗ ██╔╝",
        "██║   ██║██║██║        ██║   ██║   ██║██████╔╝ ╚████╔╝ ",
        "╚██╗ ██╔╝██║██║        ██║   ██║   ██║██╔══██╗  ╚██╔╝  ",
        " ╚████╔╝ ██║╚██████╗   ██║   ╚██████╔╝██║  ██║   ██║   ",
        "  ╚═══╝  ╚═╝ ╚═════╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝   ╚═╝   "
    };

    private EndScreens() {
    }

    static void gameOver(TextGraphics graphics, TerminalSize size, String raumName, int animationFrame) {
        Zeichnen.hintergrund(graphics, size, new TextColor.RGB(20, 0, 0));

        graphics.setForegroundColor(TextColor.ANSI.RED);
        int artY = size.getRows() / 2 - 5;
        for (int i = 0; i < GAME_OVER_ART.length; i++) {
            Zeichnen.zentriert(graphics, size, GAME_OVER_ART[i], artY + i);
        }

        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        Zeichnen.zentriert(graphics, size, "Du wurdest von den Zombies überwältigt...", artY + 7);
        graphics.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
        Zeichnen.zentriert(graphics, size, "Gestorben in: " + raumName, artY + 8);

        if (animationFrame % 30 < 15) {
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            Zeichnen.zentriert(graphics, size, "Drücke ENTER oder ESC für das Hauptmenü", artY + 10);
        }
    }

    static void sieg(TextGraphics graphics, TerminalSize size, String schwierigkeit, int animationFrame) {
        Zeichnen.hintergrund(graphics, size, new TextColor.RGB(20, 20, 0));

        // Konfetti
        for (int i = 0; i < 50; i++) {
            int confX = (int) (Math.random() * size.getColumns());
            int confY = (int) (Math.random() * size.getRows());
            graphics.setForegroundColor(new TextColor.RGB(
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255)));
            graphics.setCharacter(confX, confY, '◆');
        }

        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        int artY = size.getRows() / 2 - 8;
        for (int i = 0; i < VICTORY_ART.length; i++) {
            Zeichnen.zentriert(graphics, size, VICTORY_ART[i], artY + i);
        }

        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        Zeichnen.zentriert(graphics, size, "Du hast den Dungeon gemeistert!", artY + 8);
        Zeichnen.zentriert(graphics, size, "Der Endboss wurde besiegt! (" + schwierigkeit + ")", artY + 10);

        if (animationFrame % 30 < 15) {
            graphics.setForegroundColor(TextColor.ANSI.GREEN);
            Zeichnen.zentriert(graphics, size, "Drücke ENTER oder ESC für das Hauptmenü", artY + 13);
        }
    }
}
