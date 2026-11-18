package com.informatikgame.entities;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Würfel für Tests, gibt vorher festgelegte Werte zurück
 */
public class FesterWuerfel extends Wuerfel {

    private final Deque<Integer> w20 = new ArrayDeque<>();
    private final Deque<Integer> w6 = new ArrayDeque<>();
    private int standardW20 = 20;
    private int standardW6 = 1;

    public FesterWuerfel w20(Integer... werte) {
        w20.addAll(java.util.List.of(werte));
        return this;
    }

    public FesterWuerfel w6(Integer... werte) {
        w6.addAll(java.util.List.of(werte));
        return this;
    }

    /**
     * Wert wenn keine festgelegten mehr übrig sind
     */
    public FesterWuerfel standard(int w20Wert, int w6Wert) {
        standardW20 = w20Wert;
        standardW6 = w6Wert;
        return this;
    }

    @Override
    public int w20() {
        return w20.isEmpty() ? standardW20 : w20.poll();
    }

    @Override
    public int w6() {
        return w6.isEmpty() ? standardW6 : w6.poll();
    }

    @Override
    public int zahlBis(int max) {
        return 0;
    }
}
