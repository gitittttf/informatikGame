package com.informatikgame.combat;

import java.util.ArrayList;
import java.util.PriorityQueue;

import com.informatikgame.entities.Character;
import com.informatikgame.entities.Enemy;
import com.informatikgame.entities.Player;
import com.informatikgame.world.EnemyType;

/**
 * FightManager - Kampf-System und Runden-Management
 *
 * Diese Klasse implementiert das komplette Kampf-System:
 *
 * === KAMPF-ABLAUF === 1. Kampf-Initialisierung: Gegner-Liste erstellen,
 * Initiative sortieren 2. Runden-System: Spieler und Gegner handeln abwechselnd
 * basierend auf Initiative 3. Aktions-Auflösung: Angriffe, Schaden,
 * Eliminierung besiegter Gegner 4. Sieg/Niederlage: Kampf endet wenn eine Seite
 * eliminiert ist
 *
 * === EVENT-INTEGRATION === Verwendet CombatEventListener für UI-Updates: -
 * Runden-Start, Charakterzug-Benachrichtigungen - HP-Updates, Kampf-Log
 * Nachrichten - Kampf-Ende Events
 *
 * === INITIATIVE-SYSTEM === - PriorityQueue sortiert Charaktere nach Initiative
 * (höchste zuerst) - Jede Runde wird neu aufgebaut für dynamische Teilnehmer -
 * Spieler-Input wird asynchron verarbeitet (waitingForPlayerAction)
 *
 * === ASYNCHRONER SPIELER-INPUT === - processNextAction() pausiert bei
 * Spielerzug - executePlayerAction() setzt Kampf fort nach Input - Ermöglicht
 * UI-Integration ohne Blocking
 */
public class FightManager {

    /**
     * Event-Listener Interface für Kampf-Updates an die UI
     *
     * Ermöglicht dem UI (GameplayScreen über GameManager) auf Kampf-Ereignisse
     * zu reagieren und entsprechende Animationen/Anzeigen zu aktualisieren.
     */
    public interface CombatEventListener {

        /**
         * Neue Kampfrunde beginnt
         */
        void onRoundStart(int roundNumber);

        /**
         * Kampflog Nachricht für ui anzeige
         */
        void onCombatMessage(String message);

        /**
         * Spieler ist am Zug -> ui soll input modus aktivieren
         */
        void onPlayerTurn();

        /**
         * Gegner ist am Zug -> ui kann anzeigen das gegner dran ist
         */
        void onEnemyTurn(Enemy enemy);

        /**
         * Spieler hp aktualisiert -> ui soll hp leiste updaten
         */
        void onPlayerHealthUpdate(int current, int max);

        /**
         * gegner hp aktialisiert -> gegner infos updaten (vielleicht auch
         * gegner hp leiste in zukunft?)
         */
        void onEnemyHealthUpdate(Enemy[] enemies);

        /**
         * kampf beendet - playerWon = true bei Sieg, false bei loose
         */
        void onCombatEnd(boolean playerWon);
    }

    private Player player;
    private ArrayList<Enemy> enemiesLeftToRight;
    private CombatEventListener eventListener;
    private boolean waitingForPlayerAction;
    private int[] pendingPlayerAction;
    private int currentRound;
    private PriorityQueue<Character> currentActionQueue;
    private int playerMaxHP;

    public FightManager(Player player) {
        this.player = player;
        this.waitingForPlayerAction = false;
        this.currentRound = 0;
        this.playerMaxHP = player.getLifeTotal(); // Store initial HP as max
    }

    public void setCombatEventListener(CombatEventListener listener) {
        this.eventListener = listener;
    }

    /**
     * Startet einen neuen Kampf mit angegebenen Gegnern
     *
     * @param enemyTypesLeftToRight Array der Gegner typen (von links nach
     * rechts positioniert)
     */
    public void startFight(EnemyType[] enemyTypesLeftToRight) {
        // Erstelle Enemy-Instanzen aus Typen für vollen Kampf-Zustand
        this.enemiesLeftToRight = new ArrayList<>(enemyTypesLeftToRight.length);
        for (EnemyType enemy : enemyTypesLeftToRight) {
            this.enemiesLeftToRight.add(new Enemy(enemy));
        }

        // Kampf status initialisieren
        this.currentRound = 1;
        this.waitingForPlayerAction = false;

        // Ui über kampf start benachrichtigen
        if (eventListener != null) {
            eventListener.onCombatMessage("Der Kampf beginnt!");
            eventListener.onEnemyHealthUpdate(enemiesLeftToRight.toArray(new Enemy[0]));
        }

        // Start first round
        startRound();
    }

    private void startRound() {
        if (!player.isAlive() || enemiesLeftToRight.isEmpty()) {
            endFight();
            return;
        }

        if (eventListener != null) {
            eventListener.onRoundStart(currentRound);
            eventListener.onPlayerHealthUpdate(player.getLifeTotal(), playerMaxHP);
        }

        // Create priority queue for this round
        currentActionQueue = new PriorityQueue<>(Character.initiativeComparator);
        currentActionQueue.add(this.player);
        for (Enemy enemy : this.enemiesLeftToRight) {
            currentActionQueue.add(enemy);
        }

        processNextAction();
    }

    private void processNextAction() {
        if (currentActionQueue.isEmpty()) {
            // Round is finished, clean up dead enemies and start next round
            this.enemiesLeftToRight.removeIf(character -> !character.isAlive());

            // Update GUI with current enemy state after removing dead ones
            if (eventListener != null) {
                eventListener.onEnemyHealthUpdate(enemiesLeftToRight.toArray(new Enemy[0]));
            }

            currentRound++;
            startRound();
            return;
        }

        Character actingFighter = currentActionQueue.poll();

        // Check if fighter is still alive
        if (!actingFighter.isAlive()) {
            processNextAction(); // Skip to next action
            return;
        }

        if (actingFighter instanceof Player) {
            // Player's turn - wait for GUI input
            waitingForPlayerAction = true;
            if (eventListener != null) {
                eventListener.onPlayerTurn();
            }
        } else {
            // Enemy's turn - process automatically
            Enemy enemy = (Enemy) actingFighter;
            if (eventListener != null) {
                eventListener.onEnemyTurn(enemy);
            }

            // Enemy attacks with random abilities
            int randomNumberFinte = (int) (Math.random() * (enemy.getFinteLevel() + 1));
            int randomNumberWuchtschlag = (int) (Math.random() * (enemy.getWuchtschlagLevel() + 1));

            if (eventListener != null) {
                eventListener.onCombatMessage(enemy.getType() + " greift an!");
            }

            enemy.attack(this.player, randomNumberFinte, randomNumberWuchtschlag);

            if (eventListener != null) {
                eventListener.onPlayerHealthUpdate(player.getLifeTotal(), playerMaxHP);
            }

            // Continue with next action after a brief pause
            processNextAction();
        }
    }

    public void executePlayerAction(int targetEnemyIndex, int finteLevel, int wuchtschlagLevel) {
        if (!waitingForPlayerAction) {
            return; // Not waiting for input
        }

        // Validate input
        if (targetEnemyIndex < 0 || targetEnemyIndex >= enemiesLeftToRight.size()) {
            if (eventListener != null) {
                eventListener.onCombatMessage("Ungültiges Ziel!");
            }
            return;
        }

        if (finteLevel < 0 || finteLevel > player.getFinteLevel()) {
            if (eventListener != null) {
                eventListener.onCombatMessage("Ungültiger Finte-Level!");
            }
            return;
        }

        if (wuchtschlagLevel < 0 || wuchtschlagLevel > player.getWuchtschlagLevel()) {
            if (eventListener != null) {
                eventListener.onCombatMessage("Ungültiger Wuchtschlag-Level!");
            }
            return;
        }

        // Execute player attack
        Enemy target = enemiesLeftToRight.get(targetEnemyIndex);

        if (eventListener != null) {
            String action = "Angriff auf " + target.getType();
            if (finteLevel > 0) {
                action += " (Finte Lv." + finteLevel + ")";
            }
            if (wuchtschlagLevel > 0) {
                action += " (Wuchtschlag Lv." + wuchtschlagLevel + ")";
            }
            eventListener.onCombatMessage(action);
        }

        player.attack(target, finteLevel, wuchtschlagLevel);

        if (eventListener != null) {
            eventListener.onEnemyHealthUpdate(enemiesLeftToRight.toArray(new Enemy[0]));
        }

        waitingForPlayerAction = false;
        processNextAction();
    }

    private void endFight() {
        boolean playerWon = player.isAlive();
        if (eventListener != null) {
            eventListener.onCombatEnd(playerWon);
        }
    }

    // Method that GameManager can call to run the fight (for compatibility)
    public boolean fight(EnemyType[] enemyTypesLeftToRight) {
        startFight(enemyTypesLeftToRight);

        // For now, we'll simulate the fight synchronously for compatibility
        // In the future, this should be handled asynchronously through the GUI
        while (player.isAlive() && !enemiesLeftToRight.isEmpty() && !waitingForPlayerAction) {
            // This is a temporary solution - in a real GUI implementation,
            // the fight would be controlled by user input events
            try {
                Thread.sleep(100); // Small delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        return player.isAlive();
    }

    public boolean isWaitingForPlayerAction() {
        return waitingForPlayerAction;
    }

    // ===== GETTER METHODEN =====
    public Player getPlayer() {
        return player;
    }

    public ArrayList<Enemy> getEnemiesLeftToRight() {
        return enemiesLeftToRight;
    }
}
