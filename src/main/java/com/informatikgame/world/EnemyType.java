package com.informatikgame.world;

public enum EnemyType {
    //int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage, int finteLevel, int wuchtschlagLevel String enemyType
    MINI_ZOMBIE(10, 1, 12, 10, 6, 1, 1, 1, 0, "Mini Zombie"),
    SCIENTIST(15, 0, 10, 12, 10, 4, 1, 2, 1, "Scientist"),
    BIG_ZOMBIE(20, 3, 6, 13, 8, 2, 2, 1, 2, "Big Zombie"),
    ENDBOSS(50, 5, 4, 14, 10, 1, 4, 3, 3, "Endboss");

    public int lifeTotal;
    public int armourValue;
    public int initiative;
    public int attack;
    public int defense;
    public int damage;
    public int numW6;
    public int finteLevel;
    public int wuchtschlagLevel;
    public String enemyType;

    EnemyType(int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage, int numW6, int finteLevel, int wuchtschlagLevel, String enemyType) {
        this.lifeTotal = lifeTotal;
        this.armourValue = armourValue;
        this.initiative = initiative;
        this.attack = attack;
        this.defense = defense;
        this.damage = damage;
        this.numW6 = numW6;
        this.finteLevel = finteLevel;
        this.wuchtschlagLevel = wuchtschlagLevel;
        this.enemyType = enemyType;
    }
}
