import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.Scanner; 
import java.util.PriorityQueue;

public class FightManager {
    Player player;
    ArrayList<Enemy> enemiesLeftToRight;
    
    public FightManager(Player player) {
        this.player = player;
    }

    public boolean fight(EnemyType[] enemyTypesLeftToRight) {
        //Create list with all enemy objects enemiesLeftToRight for fight and generate randomized initiative
        this.enemiesLeftToRight = new ArrayList(enemyTypesLeftToRight.length);
        for (EnemyType enemy : enemyTypesLeftToRight) {
            this.enemiesLeftToRight.add(new Enemy(enemy));
            Enemy newestEnemy = enemiesLeftToRight.get(enemiesLeftToRight.size()-1);
            newestEnemy.randomizedInitiative = newestEnemy.initiative + (int)Math.round(Math.random() * 5 + 1);
        }
        
        //randomized initiative for player
        this.player.randomizedInitiative = this.player.initiative + (int)Math.round(Math.random() * 5 + 1);
        
        System.out.println("");
        System.out.println("Der Kampf beginnt!");
        
        for (int i = 1; (this.player.lifeTotal > 0 && enemiesLeftToRight.size() > 0); i++)
        {
            fightTurn(i);
        }
        //returns true when fight is won, else false
        if (this.player.lifeTotal > 0) return true;
        return false;
    }
    
    
    public void printCurrentFightStatus(int roundCounter) {
        System.out.println("");
        System.out.println("Runde " + roundCounter + ":");
        System.out.println("");
        
        System.out.println("Du hast " + player.lifeTotal + " Leben.");
        System.out.println("");
        
        StringBuilder sb = new StringBuilder("Vor dir stehen (von links nach rechts): ");
        
        int i = 0;
        for (Character enemy : this.enemiesLeftToRight) {
            if (i == 0) sb.append(enemy.type + " (" + enemy.lifeTotal + " Leben)");
            else sb.append(", " + enemy.type + " (" + enemy.lifeTotal + " Leben)");
            i++;
        }
        
        System.out.println(sb);
    }
    
    int[] getPlayerAction() {
        int[] action = new int[3];
        
        Scanner scanner = new Scanner(System.in);
        boolean validImput = false;
        
        System.out.print("");
        while (!validImput) {
            System.out.println("Wen möchtest du angreifen (1-" + this.enemiesLeftToRight.size() + ", von links):");
            System.out.print(">>  ");
            
            int choice = scanner.nextInt() - 1;
            if (choice >= 0 && choice < this.enemiesLeftToRight.size()) {
                action[0] = choice;
                validImput = true;
            }
            else System.out.print("Ungültige Eingabe. Versuch es nochmal!");
        }
        validImput = false;
        while (!validImput) {
            System.out.println("Finte Level (0-" + this.player.finteLevel + "):");
            System.out.print(">>  ");
            
            int choice = scanner.nextInt();
            if (choice >= 0 && choice <= 3) {
                action[1] = choice;
                validImput = true;
            }
        }
        validImput = false;
        while (!validImput) {
            System.out.println("Wuchtschlag Level (0-" + this.player.wuchtschlagLevel + "):");
            System.out.print(">>  ");
            
            int choice = scanner.nextInt();
            if (choice >= 0 && choice <= 3) {
                action[2] = choice;
                validImput = true;
            }
        }
        return action;
    }
    
    public void fightTurn(int roundCounter) {
        printCurrentFightStatus(roundCounter);
        
        //Priority queue
        PriorityQueue<Character> actionQueue = new PriorityQueue<>(Character.initiativeComparator);
        
        actionQueue.add(this.player);
        for (Character enemy : this.enemiesLeftToRight) {
            actionQueue.add(enemy);
        }
        
        while (!actionQueue.isEmpty()) {
            Character actingFighter = actionQueue.poll();
    
            if (actingFighter instanceof Player) {
                int[] action = getPlayerAction();
                actingFighter.attack(this.enemiesLeftToRight.get(action[0]), action[1], action[2]);
            }
            else {
                int randomNumberFinte = (int)(Math.random() * actingFighter.finteLevel);
                int randomNumberWuchtschlag = (int)(Math.random() * actingFighter.wuchtschlagLevel);
                actingFighter.attack(this.player, randomNumberFinte, randomNumberWuchtschlag);
            }
        }
        this.enemiesLeftToRight.removeIf(character -> !character.isAlive());
    }
}