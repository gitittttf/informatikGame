package com.informatikgame.entities;
import com.informatikgame.world.PlayerType;

public class Player extends Character
{
    // Constructor mit allen Parametern
    public Player(int lifeTotal, int armourValue, int initiative, int attack, 
                  int defense, int damage, int numW6, int finteLevel, int wuchtschlagLevel) {
        super(lifeTotal, armourValue, initiative, attack, defense, damage, 
              numW6, finteLevel, wuchtschlagLevel, "Spieler");
    }

    // Constructor für alte Tests (ohne numW6, finte, wuchtschlag)
    public Player(int lifeTotal, int armourValue, int initiative, int attack, 
                  int defense, int damage) {
        super(lifeTotal, armourValue, initiative, attack, defense, damage, 
              1, 3, 3, "Spieler");  // Standardwerte für numW6=1, finteLevel=3, wuchtschlagLevel=3
    }

    // Constructor mit PlayerType
    public Player(PlayerType playerType) {
        super(playerType.lifeTotal, playerType.armourValue, playerType.initiative, 
              playerType.attack, playerType.defense, playerType.damage, 
              playerType.numW6, playerType.finteLevel, playerType.wuchtschlagLevel, "Spieler");
    }
}