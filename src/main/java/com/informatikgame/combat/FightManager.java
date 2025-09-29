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
         * Kampflog Nachricht mit Farbe für ui anzeige
         */
        void onCombatMessage(String message, CombatMessageType messageType);

        /**
         * Queued combat message with delay
         */
        void onQueuedCombatMessage(String message, CombatMessageType messageType, long delayMs);

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

    /**
     * Types of combat messages for color coding
     */
    public enum CombatMessageType {
        ROUND_START, // For round indicators
        PLAYER_ACTION, // For player actions
        ENEMY_ACTION, // For enemy actions
        UPGRADE, // For upgrades
        SPECIAL_MOVE, // For finte/wuchtschlag
        DAMAGE, // For damage dealing
        DEFENSE, // For defense/parrying
        COMBAT_START, // For combat start
        COMBAT_END      // For combat end
    }

    /**
     * Types of attacks
     */
    public enum AttackType {
        NORMAL,
        FINTE,
        WUCHTSCHLAG
    }

    private Player player;
    private ArrayList<Enemy> enemiesLeftToRight;
    private CombatEventListener eventListener;
    private boolean waitingForPlayerAction;
    private int currentRound;
    private PriorityQueue<Character> currentActionQueue;

    public FightManager(Player player) {
        this.player = player;
        this.waitingForPlayerAction = false;
        this.currentRound = 0;

        // Set up combat event listeners for characters
        setupCombatEventListeners();
    }

    private void setupCombatEventListeners() {
        Character.CombatEventListener characterListener = (String message, Character.CombatMessageType type, long delayMs) -> {
            if (eventListener != null) {
                // Convert Character.CombatMessageType to FightManager.CombatMessageType
                CombatMessageType fightManagerType = convertMessageType(type);
                eventListener.onQueuedCombatMessage(message, fightManagerType, delayMs);
            }
        };

        player.setCombatEventListener(characterListener);
    }

    private CombatMessageType convertMessageType(Character.CombatMessageType charType) {
        return switch (charType) {
            case ROUND_START ->
                CombatMessageType.ROUND_START;
            case PLAYER_ACTION ->
                CombatMessageType.PLAYER_ACTION;
            case ENEMY_ACTION ->
                CombatMessageType.ENEMY_ACTION;
            case UPGRADE ->
                CombatMessageType.UPGRADE;
            case SPECIAL_MOVE ->
                CombatMessageType.SPECIAL_MOVE;
            case DAMAGE ->
                CombatMessageType.DAMAGE;
            case DEFENSE ->
                CombatMessageType.DEFENSE;
            case COMBAT_START ->
                CombatMessageType.COMBAT_START;
            case COMBAT_END ->
                CombatMessageType.COMBAT_END;
            default ->
                CombatMessageType.PLAYER_ACTION;
        };
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
            Enemy newestEnemy = enemiesLeftToRight.get(enemiesLeftToRight.size() - 1);
            newestEnemy.setRandomizedInitiative(newestEnemy.getInitiative() + (int) Math.round(Math.random() * 6 + 1));
        }

        this.player.setRandomizedInitiative(this.player.getInitiative() + (int) Math.round(Math.random() * 6 + 1));

        // Kampf status initialisieren
        this.currentRound = 1;
        this.waitingForPlayerAction = false;

        // Setup combat listeners for all enemies
        Character.CombatEventListener enemyListener = (String message, Character.CombatMessageType type, long delayMs) -> {
            if (eventListener != null) {
                // Convert Character.CombatMessageType to FightManager.CombatMessageType
                CombatMessageType fightManagerType = convertMessageType(type);
                eventListener.onQueuedCombatMessage(message, fightManagerType, delayMs);
            }
        };

        for (Enemy enemy : enemiesLeftToRight) {
            enemy.setCombatEventListener(enemyListener);
        }

        // Ui über kampf start benachrichtigen
        if (eventListener != null) {
            eventListener.onCombatMessage("=== Der Kampf beginnt! ===", CombatMessageType.COMBAT_START);
            eventListener.onEnemyHealthUpdate(enemiesLeftToRight.toArray(Enemy[]::new));
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
            eventListener.onPlayerHealthUpdate(player.getLifeTotal(), player.getMaxLife());
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
                eventListener.onEnemyHealthUpdate(enemiesLeftToRight.toArray(Enemy[]::new));
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
                eventListener.onQueuedCombatMessage(enemy.getType() + " ist am Zug!", CombatMessageType.ENEMY_ACTION, 0);
            }

            enemy.attack(this.player, randomNumberFinte, randomNumberWuchtschlag);

            if (eventListener != null) {
                eventListener.onPlayerHealthUpdate(player.getLifeTotal(), player.getMaxLife());
            }

            // Continue with next action 
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
            eventListener.onQueuedCombatMessage("Du startest deinen Angriff!", CombatMessageType.PLAYER_ACTION, 0);
        }

        player.attack(target, finteLevel, wuchtschlagLevel);

        if (eventListener != null) {
            eventListener.onEnemyHealthUpdate(enemiesLeftToRight.toArray(Enemy[]::new));
        }

        waitingForPlayerAction = false;
        processNextAction();
    }

    public void executePlayerAction(int targetEnemyIndex, AttackType attackType) {
        int finteLevel = 0;
        int wuchtschlagLevel = 0;

        switch (attackType) {
            case FINTE ->
                finteLevel = player.getFinteLevel(); // Uses PlayerType's value (3)
            case WUCHTSCHLAG ->
                wuchtschlagLevel = player.getWuchtschlagLevel(); // Uses PlayerType's value (3)
            case NORMAL -> {
            }
        }
        executePlayerAction(targetEnemyIndex, finteLevel, wuchtschlagLevel);
    }

    private void endFight() {
        boolean playerWon = player.isAlive();
        if (eventListener != null) {
            eventListener.onCombatEnd(playerWon);
        }
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
