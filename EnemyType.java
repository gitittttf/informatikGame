public enum EnemyType {
    //int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage, String enemyType
    MINI_ZOMBIE(10, 1, 12, 10, 6, 4, "Mini Zombie"),
    SCIENTIST(15, 0, 10, 12, 10, 8, "Scientist"),
    BIG_ZOMBIE(20, 3, 6, 13, 8, 10, "Big Zombie"),
    ENDBOSS(50, 5, 4, 14, 10, 15, "Endboss");

    public int lifeTotal;
    public int armourValue;
    public int initiative;
    public int attack;
    public int defense;
    public int damage;
    public String enemyType;

    EnemyType(int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage, String enemyType) {
        this.lifeTotal = lifeTotal;
        this.armourValue = armourValue;
        this.initiative = initiative;
        this.attack = attack;
        this.defense = defense;
        this.damage = damage;
        this.enemyType = enemyType;
    }
}