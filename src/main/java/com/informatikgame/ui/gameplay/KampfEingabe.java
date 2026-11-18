package com.informatikgame.ui.gameplay;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

/**
 * Eingabe im Kampf in drei Schritten: Ziel → Finte → Wuchtschlag
 */
public class KampfEingabe {

    public enum Schritt {
        ZIEL,
        FINTE,
        WUCHTSCHLAG
    }

    private Schritt schritt = Schritt.ZIEL;
    private int ziel;
    private int finte;
    private int wuchtschlag;

    /**
     * Neuer Zug, Ziel bleibt wenn möglich gleich
     */
    public void neuerZug() {
        schritt = Schritt.ZIEL;
        finte = 0;
        wuchtschlag = 0;
    }

    public void reset() {
        neuerZug();
        ziel = 0;
    }

    /**
     * Ziel im gültigen Bereich halten wenn Gegner sterben
     */
    public void zielBegrenzen(int gegnerAnzahl) {
        ziel = Math.max(0, Math.min(ziel, gegnerAnzahl - 1));
    }

    /**
     * Verarbeitet eine Taste
     *
     * @return true wenn angegriffen werden soll (Enter im letzten Schritt)
     */
    public boolean taste(KeyStroke key, int gegnerAnzahl, int maxFinte, int maxWuchtschlag) {
        if (gegnerAnzahl <= 0) {
            return false;
        }
        int max = switch (schritt) {
            case ZIEL ->
                gegnerAnzahl - 1;
            case FINTE ->
                maxFinte;
            case WUCHTSCHLAG ->
                maxWuchtschlag;
        };
        // beim ziel zählen die zahlentasten ab 1, sonst ab 0
        int zahlVersatz = schritt == Schritt.ZIEL ? 1 : 0;

        if (key.getKeyType() == KeyType.ArrowLeft) {
            setzeWert(Math.max(0, wert() - 1));
        } else if (key.getKeyType() == KeyType.ArrowRight) {
            setzeWert(Math.min(max, wert() + 1));
        } else if (key.getKeyType() == KeyType.Character && Character.isDigit(key.getCharacter())) {
            int zahl = Character.getNumericValue(key.getCharacter()) - zahlVersatz;
            if (zahl >= 0 && zahl <= max) {
                setzeWert(zahl);
            }
        } else if (key.getKeyType() == KeyType.Enter) {
            switch (schritt) {
                case ZIEL ->
                    schritt = Schritt.FINTE;
                case FINTE ->
                    schritt = Schritt.WUCHTSCHLAG;
                case WUCHTSCHLAG -> {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Einen Schritt zurück
     *
     * @return false wenn man schon beim ersten Schritt ist
     */
    public boolean zurueck() {
        switch (schritt) {
            case FINTE -> {
                schritt = Schritt.ZIEL;
                return true;
            }
            case WUCHTSCHLAG -> {
                schritt = Schritt.FINTE;
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private int wert() {
        return switch (schritt) {
            case ZIEL ->
                ziel;
            case FINTE ->
                finte;
            case WUCHTSCHLAG ->
                wuchtschlag;
        };
    }

    private void setzeWert(int wert) {
        switch (schritt) {
            case ZIEL ->
                ziel = wert;
            case FINTE ->
                finte = wert;
            case WUCHTSCHLAG ->
                wuchtschlag = wert;
        }
    }

    public Schritt getSchritt() {
        return schritt;
    }

    public int getZiel() {
        return ziel;
    }

    public int getFinte() {
        return finte;
    }

    public int getWuchtschlag() {
        return wuchtschlag;
    }
}
