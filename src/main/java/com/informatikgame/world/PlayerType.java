package com.informatikgame.world;

public enum PlayerType {
    //int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage, int numW6, int finteLevel, int wuchtschlagLevel
    SWORD_FIGHTER(36, 3, 10, 17, 8, 6, 1, 2, 2),
    SHIELD_FIGHTER(40, 4, 8, 14, 14, 4, 1, 1, 2);

    public int lifeTotal;
    public int armourValue;
    public int initiative;
    public int attack;
    public int defense;
    public int damage;
    public int numW6;
    public int finteLevel;
    public int wuchtschlagLevel;

    PlayerType(int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage, int numW6, int finteLevel, int wuchtschlagLevel) {
        this.lifeTotal = lifeTotal;
        this.armourValue = armourValue;
        this.initiative = initiative;
        this.attack = attack;
        this.defense = defense;
        this.damage = damage;
        this.numW6 = numW6;
        this.finteLevel = finteLevel;
        this.wuchtschlagLevel = wuchtschlagLevel;
    }
}
