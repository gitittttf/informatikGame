package com.informatikgame.world;

import java.util.ArrayList;
import java.util.List;

public enum UpgradeType {
    // lifeTotal, maxLife, armourValue, initiative, attack, defense, damage, finteLevel, wuchtschlagLevel
    LIFE_UPGRADE(5, 5, 0, 0, 0, 0, 0, 0, 0),
    SKILL_UPGRADE(0, 0, 0, 0, 0, 0, 0, 1, 1),
    ARMOUR_UPGRADE(0, 0, 3, 0, 0, 0, 0, 0, 0),
    ATTACK_UPGRADE(0, 0, 0, 0, 2, 0, 0, 0, 0),
    PANTRY_COMPOUND_UPGRADE(5, 5, 0, 0, 0, 0, 2, 0, 0);

    public final int lifeTotal;
    public final int maxLife;
    public final int armourValue;
    public final int initiative;
    public final int attack;
    public final int defense;
    public final int damage;
    public final int finteLevel;
    public final int wuchtschlagLevel;

    UpgradeType(int lifeTotal, int maxLife, int armourValue, int initiative, int attack, int defense, int damage, int finteLevel, int wuchtschlagLevel) {
        this.lifeTotal = lifeTotal;
        this.maxLife = maxLife;
        this.armourValue = armourValue;
        this.initiative = initiative;
        this.attack = attack;
        this.defense = defense;
        this.damage = damage;
        this.finteLevel = finteLevel;
        this.wuchtschlagLevel = wuchtschlagLevel;
    }

    /**
     * Kurzer Text für Story und Log, z.B. "+5 Max-HP, +2 Schaden"
     */
    public String beschreibung() {
        List<String> teile = new ArrayList<>();
        hinzu(teile, maxLife, "Max-HP");
        hinzu(teile, armourValue, "Rüstung");
        hinzu(teile, initiative, "Initiative");
        hinzu(teile, attack, "Angriff");
        hinzu(teile, defense, "Verteidigung");
        hinzu(teile, damage, "Schaden");
        hinzu(teile, finteLevel, "Finte-Level");
        hinzu(teile, wuchtschlagLevel, "Wuchtschlag-Level");
        return String.join(", ", teile);
    }

    private static void hinzu(List<String> teile, int wert, String name) {
        if (wert != 0) {
            teile.add("+" + wert + " " + name);
        }
    }
}
