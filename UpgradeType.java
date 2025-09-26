public enum UpgradeType {
    //int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage, int finteLevel, int wuchtschlagLevel
    FINTE_UPGRADE(0, 0, 0, 0, 0, 0, 0, 1, 0);

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