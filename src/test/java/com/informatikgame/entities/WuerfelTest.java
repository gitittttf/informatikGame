package com.informatikgame.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class WuerfelTest {

    @Test
    public void w6NurEinsBisSechsUndGleichverteilt() {
        Wuerfel wuerfel = new Wuerfel(42);
        int[] anzahl = new int[7];
        int wuerfe = 60000;
        for (int i = 0; i < wuerfe; i++) {
            int wurf = wuerfel.w6();
            assertTrue(wurf >= 1 && wurf <= 6, "Wurf außerhalb: " + wurf);
            anzahl[wurf]++;
        }
        // jede zahl ungefähr 1/6, früher waren 1 und 6 nur halb so oft
        for (int zahl = 1; zahl <= 6; zahl++) {
            assertEquals(wuerfe / 6.0, anzahl[zahl], wuerfe * 0.01, "Zahl " + zahl);
        }
    }

    @Test
    public void w20NurEinsBisZwanzig() {
        Wuerfel wuerfel = new Wuerfel(7);
        boolean einsGesehen = false;
        boolean zwanzigGesehen = false;
        for (int i = 0; i < 10000; i++) {
            int wurf = wuerfel.w20();
            assertTrue(wurf >= 1 && wurf <= 20);
            einsGesehen |= wurf == 1;
            zwanzigGesehen |= wurf == 20;
        }
        assertTrue(einsGesehen && zwanzigGesehen);
    }

    @Test
    public void gleicherSeedGleicheWuerfe() {
        Wuerfel a = new Wuerfel(123);
        Wuerfel b = new Wuerfel(123);
        for (int i = 0; i < 100; i++) {
            assertEquals(a.w20(), b.w20());
        }
    }

    @Test
    public void zahlBisNieUeberMax() {
        Wuerfel wuerfel = new Wuerfel(1);
        assertEquals(0, wuerfel.zahlBis(0));
        for (int i = 0; i < 1000; i++) {
            int z = wuerfel.zahlBis(3);
            assertTrue(z >= 0 && z <= 3);
        }
    }
}
