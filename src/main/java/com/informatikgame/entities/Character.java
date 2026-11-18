package com.informatikgame.entities;

import java.util.Comparator;

import com.informatikgame.world.UpgradeType;

public abstract class Character {

    protected int lifeTotal;
    protected int maxLife;
    protected int armourValue;
    protected int initiative;
    private int randomizedInitiative;
    protected int attack;
    protected int defense;
    protected int damage;
    protected int numW6;
    protected int finteLevel;
    protected int wuchtschlagLevel;
    protected final String type;

    protected Character(Werte werte, String type) {
        this.lifeTotal = werte.lebenspunkte();
        this.maxLife = werte.lebenspunkte();
        this.armourValue = werte.ruestung();
        this.initiative = werte.initiative();
        this.attack = werte.angriff();
        this.defense = werte.verteidigung();
        this.damage = werte.schaden();
        this.numW6 = werte.anzahlW6();
        this.finteLevel = werte.finteLevel();
        this.wuchtschlagLevel = werte.wuchtschlagLevel();
        this.type = type;
    }

    public boolean isAlive() {
        return lifeTotal > 0;
    }

    public abstract boolean istSpieler();

    /**
     * Greift das Ziel an. Finte erschwert den eigenen Angriff um 1 pro Level
     * und die Parade vom Gegner um 2. Wuchtschlag erschwert um 2 pro Level und
     * macht +2 Schaden pro Level.
     */
    public AngriffsErgebnis attack(Character target, int finte, int wuchtschlag, Wuerfel wuerfel) {
        finte = clamp(finte, 0, finteLevel);
        wuchtschlag = clamp(wuchtschlag, 0, wuchtschlagLevel);

        boolean getroffen = wuerfel.w20() <= attack - finte - wuchtschlag * 2;
        if (!getroffen) {
            return new AngriffsErgebnis(type, target.type, istSpieler(), finte, wuchtschlag,
                    false, false, 0, 0, false);
        }

        int rohSchaden = damage + 2 * wuchtschlag;
        for (int i = 0; i < numW6; i++) {
            rohSchaden += wuerfel.w6();
        }

        boolean pariert = wuerfel.w20() <= target.defense - 2 * finte;
        int erlitten = 0;
        if (!pariert) {
            erlitten = target.nimmSchaden(rohSchaden);
        }

        return new AngriffsErgebnis(type, target.type, istSpieler(), finte, wuchtschlag,
                true, pariert, rohSchaden, erlitten, !target.isAlive());
    }

    /**
     * Zieht Schaden minus Rüstung ab, nie unter 0 HP
     *
     * @return wie viel wirklich abgezogen wurde
     */
    protected int nimmSchaden(int schaden) {
        int abzug = Math.min(lifeTotal, Math.max(0, schaden - armourValue));
        lifeTotal -= abzug;
        return abzug;
    }

    /**
     * Heilt, aber nie über die Max HP
     *
     * @return wie viel wirklich geheilt wurde
     */
    public int heal(int menge) {
        int vorher = lifeTotal;
        lifeTotal = Math.min(maxLife, lifeTotal + Math.max(0, menge));
        return lifeTotal - vorher;
    }

    public void upgrade(UpgradeType upgrade) {
        maxLife += upgrade.maxLife;
        armourValue += upgrade.armourValue;
        initiative += upgrade.initiative;
        attack += upgrade.attack;
        defense += upgrade.defense;
        damage += upgrade.damage;
        finteLevel += upgrade.finteLevel;
        wuchtschlagLevel += upgrade.wuchtschlagLevel;
        heal(upgrade.lifeTotal);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static final Comparator<Character> initiativeComparator
            = (c1, c2) -> Integer.compare(c2.getRandomizedInitiative(), c1.getRandomizedInitiative());

    // ===== GETTER =====
    public int getRandomizedInitiative() {
        return randomizedInitiative;
    }

    public void setRandomizedInitiative(int randomizedInitiative) {
        this.randomizedInitiative = randomizedInitiative;
    }

    public int getLifeTotal() {
        return lifeTotal;
    }

    public int getMaxLife() {
        return maxLife;
    }

    public int getArmourValue() {
        return armourValue;
    }

    public int getInitiative() {
        return initiative;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getDamage() {
        return damage;
    }

    public int getNumW6() {
        return numW6;
    }

    public int getFinteLevel() {
        return finteLevel;
    }

    public int getWuchtschlagLevel() {
        return wuchtschlagLevel;
    }

    public String getType() {
        return type;
    }
}
