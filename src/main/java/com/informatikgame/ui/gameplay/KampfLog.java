package com.informatikgame.ui.gameplay;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.LongSupplier;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.informatikgame.core.Ereignis;
import com.informatikgame.core.NachrichtenTyp;
import com.informatikgame.ui.ScreenManager;
import com.informatikgame.ui.Zeichnen;

/**
 * Warteschlange für alle Ereignisse vom GameManager. Der Kampf wird sofort
 * ausgerechnet, hier wird er dann Stück für Stück mit Pausen angezeigt.
 * Zustands- und Phasenwechsel werden erst angewendet, wenn sie dran sind,
 * damit HP-Leisten und Screens zum Log passen.
 */
public class KampfLog {

    public record Zeile(String text, NachrichtenTyp typ) {

    }

    private static final int MAX_ZEILEN = 50;

    private final LongSupplier uhr;
    private final Deque<Ereignis> warteschlange = new ArrayDeque<>();
    private final List<Zeile> zeilen = new ArrayList<>();
    private long letzteAnzeige;

    public KampfLog() {
        this(System::currentTimeMillis);
    }

    public KampfLog(LongSupplier uhr) {
        this.uhr = uhr;
        this.letzteAnzeige = uhr.getAsLong();
    }

    public void hinzufuegen(Ereignis ereignis) {
        warteschlange.addLast(ereignis);
    }

    /**
     * Alles anzeigen was schon dran ist. Nachrichten landen im Log, alles
     * andere geht an anwenden.
     */
    public void aktualisieren(Consumer<Ereignis> anwenden) {
        long jetzt = uhr.getAsLong();
        while (!warteschlange.isEmpty() && jetzt >= letzteAnzeige + warteschlange.peekFirst().pauseMs()) {
            verarbeiten(warteschlange.pollFirst(), anwenden);
            letzteAnzeige = jetzt;
        }
    }

    /**
     * Alles sofort anzeigen (zum überspringen)
     */
    public void allesAnzeigen(Consumer<Ereignis> anwenden) {
        while (!warteschlange.isEmpty()) {
            verarbeiten(warteschlange.pollFirst(), anwenden);
        }
        letzteAnzeige = uhr.getAsLong();
    }

    private void verarbeiten(Ereignis ereignis, Consumer<Ereignis> anwenden) {
        if (ereignis instanceof Ereignis.Nachricht n) {
            zeilen.add(new Zeile(n.text(), n.typ()));
            if (zeilen.size() > MAX_ZEILEN) {
                zeilen.remove(0);
            }
        } else {
            anwenden.accept(ereignis);
        }
    }

    public boolean istLeer() {
        return warteschlange.isEmpty();
    }

    public void leeren() {
        warteschlange.clear();
        zeilen.clear();
        letzteAnzeige = uhr.getAsLong();
    }

    public List<Zeile> getZeilen() {
        return Collections.unmodifiableList(zeilen);
    }

    public void zeichne(TextGraphics graphics, int x, int y, int width, int height) {
        Zeichnen.box(graphics, x, y, width, height, ScreenManager.SECONDARY_COLOR, ScreenManager.BACKGROUND_COLOR);

        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(new TerminalPosition(x + 2, y), "[ KAMPF-LOG ]");

        int maxLines = height - 2;
        int startIndex = Math.max(0, zeilen.size() - maxLines);
        for (int i = startIndex; i < zeilen.size(); i++) {
            Zeile zeile = zeilen.get(i);
            String text = zeile.text();
            if (text.length() > width - 4) {
                text = text.substring(0, width - 7) + "...";
            }
            graphics.setForegroundColor(farbe(zeile.typ()));
            graphics.putString(new TerminalPosition(x + 2, y + 1 + (i - startIndex)), text);
        }
    }

    static TextColor farbe(NachrichtenTyp typ) {
        return switch (typ) {
            case RUNDE ->
                TextColor.ANSI.CYAN;
            case SPIELER ->
                TextColor.ANSI.GREEN;
            case GEGNER ->
                TextColor.ANSI.RED;
            case UPGRADE ->
                TextColor.ANSI.MAGENTA;
            case SPEZIAL ->
                TextColor.ANSI.YELLOW;
            case SCHADEN ->
                new TextColor.RGB(255, 140, 0); // Orange
            case PARADE ->
                TextColor.ANSI.BLUE;
            case KAMPF_START ->
                new TextColor.RGB(255, 215, 0); // Gold
            case KAMPF_ENDE ->
                new TextColor.RGB(0, 255, 127);
            case INFO ->
                TextColor.ANSI.WHITE;
        };
    }
}
