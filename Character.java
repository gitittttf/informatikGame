import java.lang.Math;
import java.util.*;

public class Character {
    int lifeTotal;
    int maxLife;
    int armourValue;
    int initiative;
    int randomizedInitiative;
    int attack;
    int defense;
    int damage;
    int numW6;
    int finteLevel;
    int wuchtschlagLevel;
    String type;

    // Constructor
    public Character(int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage, int numW6, int finteLevel, int wuchtschlagLevel, String type) {
        this.lifeTotal = lifeTotal;
        this.maxLife = lifeTotal;
        this.armourValue = armourValue;
        this.initiative = initiative;
        this.randomizedInitiative = 0;
        this.attack = attack;
        this.defense = defense;
        this.damage = damage;
        this.numW6 = numW6;
        this.finteLevel = finteLevel;
        this.wuchtschlagLevel = wuchtschlagLevel;
        this.type = type;
    }
    
    //is alive
    public boolean isAlive() {
        return this.lifeTotal > 0;
    }

    //Attack target, Finte und Wuchtschlag Level 0-3 (Finte erschwert Angriff um 1, Verteidigung Gegner um 2, Wuchtschlag erschwert um 2 für +2 Schaden)
    public void attack(Character target, int finte, int wuchtschlag) {
        finte = clamp(finte, 0, this.finteLevel);
        wuchtschlag = clamp(wuchtschlag, 0, this.wuchtschlagLevel);
        int diceRoll = (int)(Math.random() * 19) + 1;
        System.out.println("");
        if (diceRoll <= this.attack - finte - wuchtschlag * 2) {
            int defenseDebuff = 2 * finte;
            int damageBonus = 2 * wuchtschlag;
            
            //numW6 Würfelwürfe für Schaden
            for (int i = 0; i < this.numW6; i++)
            {
                damageBonus += (int)Math.round(Math.random() * 5 + 1);
            }
            
            System.out.println(this.type + " greift " + target.type + " erfolgreich für "+ (this.damage + damageBonus) + " Schaden an (Finte: " + finte + ", Wuchtschlag: " + wuchtschlag + ").");
            target.defense(this.damage + damageBonus, defenseDebuff);
        }
        else {
            System.out.println(this.type + " scheiterte " + target.type + " anzugreifen.");
        }
    }

    //Defense dmageTaken und Verteidigungsdebuff durch Finte
    public void defense(int damageTaken, int defenseDebuff) {
        int diceRoll = (int)(Math.random() * 20);
        if (diceRoll <= this.defense - defenseDebuff) {
            System.out.println(this.type + " parriert erfolgreich.");
            return;
        }
        this.lifeTotal -= damageTaken - this.armourValue;
        System.out.println(this.type + " konnte nicht parieren.");
        System.out.println(this.type + " nimmt " + (damageTaken - this.armourValue) + " Schaden.");
    }
    
    //Upgrade Character
    public void upgrade(int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage, int finteLevel, int wuchtschlagLevel) {
        this.lifeTotal = Math.max(this.lifeTotal + lifeTotal, this.maxLife);
        this.armourValue += armourValue;
        this.initiative += initiative;
        this.attack += attack;
        this.defense += defense;
        this.damage += damage;
        this.finteLevel += finteLevel;
        this.wuchtschlagLevel += wuchtschlagLevel;
    }
    
    //Upgrade Character with Upgrade Type
    public void upgrade(UpgradeType upgradeType)
    {
        upgrade(upgradeType.lifeTotal, upgradeType.armourValue, upgradeType.initiative, upgradeType.attack, upgradeType.defense, upgradeType.damage, upgradeType.finteLevel, upgradeType.wuchtschlagLevel);
    }

    //Utilities
    int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
    
    public static Comparator<Character> initiativeComparator = new Comparator<Character>() {
        @Override
        public int compare(Character c1, Character c2) {
            return Integer.compare(c2.randomizedInitiative, c1.randomizedInitiative);
        }
    };

}