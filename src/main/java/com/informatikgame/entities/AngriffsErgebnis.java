package com.informatikgame.entities;

/**
 * Was bei einem Angriff passiert ist. Die Texte fürs Log macht der
 * GameManager daraus, hier stehen nur die Fakten drin.
 *
 * @param rohSchaden Schaden vor Rüstung (0 wenn nicht getroffen)
 * @param erlittenerSchaden Schaden der wirklich abgezogen wurde
 */
public record AngriffsErgebnis(
        String angreifer,
        String ziel,
        boolean angreiferIstSpieler,
        int finte,
        int wuchtschlag,
        boolean getroffen,
        boolean pariert,
        int rohSchaden,
        int erlittenerSchaden,
        boolean zielBesiegt) {

    public boolean mitSpezialangriff() {
        return finte > 0 || wuchtschlag > 0;
    }
}
