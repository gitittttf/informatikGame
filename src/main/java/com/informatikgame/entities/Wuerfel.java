package com.informatikgame.entities;

import java.util.Random;

/**
 * Alle Zufallswürfe im Spiel laufen hierüber. Mit festem Seed kann man
 * Kämpfe in Tests genau nachspielen.
 */
public class Wuerfel {

    private final Random random;

    public Wuerfel() {
        this(new Random());
    }

    public Wuerfel(long seed) {
        this(new Random(seed));
    }

    public Wuerfel(Random random) {
        this.random = random;
    }

    /**
     * W6: 1 bis 6, alle gleich wahrscheinlich
     */
    public int w6() {
        return random.nextInt(6) + 1;
    }

    /**
     * W20: 1 bis 20
     */
    public int w20() {
        return random.nextInt(20) + 1;
    }

    /**
     * Zufallszahl von 0 bis max (beide inklusive)
     */
    public int zahlBis(int max) {
        if (max <= 0) {
            return 0;
        }
        return random.nextInt(max + 1);
    }
}
