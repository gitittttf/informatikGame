package com.informatikgame.world;

public enum UpgradeType {
    // lifeTotal, maxLife, armourValue, initiative, attack, defense, damage, finteLevel, wuchtschlagLevel
    FINTE_UPGRADE(0, 0, 0, 0, 0, 0, 0, 1, 0),
    LIFE_UPGRADE(5, 5, 0, 0, 0, 0, 0, 0, 0), // +5 HP
    DAMAGE_UPGRADE(0, 0, 0, 0, 0, 0, 2, 0, 0), // +2 Damage
    SKILL_UPGRADE(0, 0, 0, 0, 0, 0, 0, 1, 1), // +1 Finte-Level und +1 Wuchtschlag-Level
    ARMOUR_UPGRADE(0, 0, 3, 0, 0, 0, 0, 0, 0), // +3 Armour
    ATTACK_UPGRADE(0, 0, 0, 0, 2, 0, 0, 0, 0), // +2 Attack
    PANTRY_COMPOUND_UPGRADE(5, 5, 0, 0, 0, 0, 2, 0, 0); // +5 HP und +2 Damage für PANTRY_1

    public int maxLife;
    public int lifeTotal;
    public int armourValue;
    public int initiative;
    public int attack;
    public int defense;
    public int damage;
    public int finteLevel;
    public int wuchtschlagLevel;

    UpgradeType(int lifeTotal, int maxLife, int armourValue, int initiative, int attack, int defense, int damage, int finteLevel, int wuchtschlagLevel) {
        this.maxLife = maxLife;
        this.lifeTotal = lifeTotal;
        this.armourValue = armourValue;
        this.initiative = initiative;
        this.attack = attack;
        this.defense = defense;
        this.damage = damage;
        this.finteLevel = finteLevel;
        this.wuchtschlagLevel = wuchtschlagLevel;
    }
}
