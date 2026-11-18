package com.informatikgame.core;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.informatikgame.entities.Wuerfel;
import com.informatikgame.world.PlayerType;
import com.informatikgame.world.Schwierigkeit;

/**
 * Spielt viele Runden automatisch durch (immer erster Gegner, keine
 * Spezialangriffe) und prüft ob die Schwierigkeiten noch passen. Wenn jemand
 * Werte ändert und das Spiel dadurch unschaffbar wird, fällt das hier auf.
 */
public class BalanceTest {

    private static final int SPIELE = 1000;

    private double siegQuote(PlayerType typ, Schwierigkeit schwierigkeit) {
        int siege = 0;
        for (int i = 0; i < SPIELE; i++) {
            GameManager gm = new GameManager(new Wuerfel(i * 31L + 7));
            gm.initialisiereSpiel(typ, schwierigkeit);
            gm.starteSpiel();
            while (gm.getPhase() != Spielphase.SIEG && gm.getPhase() != Spielphase.GAME_OVER) {
                if (gm.getPhase() == Spielphase.KAMPF) {
                    gm.spielerAngriff(0, 0, 0);
                } else {
                    gm.weiter();
                }
            }
            if (gm.getPhase() == Spielphase.SIEG) {
                siege++;
            }
        }
        return 100.0 * siege / SPIELE;
    }

    private void pruefe(Schwierigkeit schwierigkeit, double min, double max) {
        for (PlayerType typ : new PlayerType[]{PlayerType.SWORD_FIGHTER, PlayerType.SHIELD_FIGHTER}) {
            double quote = siegQuote(typ, schwierigkeit);
            assertTrue(quote >= min && quote <= max,
                    schwierigkeit + " mit " + typ + ": " + quote + "% Siege, erwartet " + min + "-" + max + "%");
        }
    }

    @Test
    public void leichtIstMeistensSchaffbar() {
        pruefe(Schwierigkeit.LEICHT, 70, 97);
    }

    @Test
    public void normalIstEtwaFiftyFifty() {
        pruefe(Schwierigkeit.NORMAL, 30, 65);
    }

    @Test
    public void schwerIstSchwerAberMoeglich() {
        pruefe(Schwierigkeit.SCHWER, 5, 40);
    }
}
