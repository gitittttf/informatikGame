package com.informatikgame.core;

import java.util.LinkedList;
import java.util.Queue;

import com.informatikgame.combat.FightManager;
import com.informatikgame.combat.FightManager.AttackType;
import com.informatikgame.entities.Enemy;
import com.informatikgame.entities.Player;
import com.informatikgame.ui.GameplayScreen;
import com.informatikgame.ui.StoryDatabank;
import com.informatikgame.world.EnemyType;
import com.informatikgame.world.PlayerType;
import com.informatikgame.world.Room;
import com.informatikgame.world.RoomType;
import com.informatikgame.world.RoomUpgrade;
import com.informatikgame.world.UpgradeType;
import com.informatikgame.world.World;

/**
 * GameManager - Zentrale Spiellogik und Event-Koordination
 *
 * Diese Klasse orchestriert das gesamte Spiel und fungiert als Verbindung
 * zwischen:
 *
 * === SYSTEM-KOMPONENTEN ===
 * <ul>
 * <li>World: Raum-Navigation und Dungeon-Struktur</li>
 * <li>FightManager: Kampf-System und Gegner-Interaktionen</li>
 * <li>Player: Spieler-Zustand und Attribute</li>
 * <li>Story-System: Narrative Events und Text-Anzeige</li>
 * <li>UI (GameEventListener): Benutzeroberflächen-Updates</li>
 * </ul>
 * === EVENT-SYSTEM === Implementiert Observer-Pattern für UI-Updates: -
 * Combat-Events: Kampf-Start/Ende, HP-Änderungen - Navigation-Events:
 * Raum-Wechsel, Fortschritt - Story-Events: Text-Anzeige,
 * Benutzer-Entscheidungen
 *
 * === SPIEL-ABLAUF === 1. Spieler betritt Raum → Story-Anzeige 2.
 * Benutzer-Input → Navigation oder Kampf 3. Kampf-Resolution → Fortschritt oder
 * Game Over 4. Raum-Übergang → Wiederholen bis Sieg
 */
public class GameManager implements FightManager.CombatEventListener {

    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Event-Listener Interface für UI-Updates
     *
     * Alle Methoden werden vom GameManager aufgerufen, um die
     * Benutzeroberfläche über Spielzustand-Änderungen zu informieren. Das UI
     * (GameplayScreen) implementiert dieses Interface.
     */
    public interface GameEventListener {

        /**
         * Neue Kampf-Log Nachricht für UI-Anzeige
         */
        void onCombatLogUpdate(String message);

        /**
         * Spieler hat neuen Raum betreten
         */
        void onRoomChange(int roomNumber, int totalRooms, String roomName);

        /**
         * Spieler-HP hat sich geändert
         */
        void onPlayerHealthChange(int current, int max);

        /**
         * Gegner-Liste wurde aktualisiert (HP, Anzahl)
         */
        void onEnemyUpdate(Enemy[] enemies);

        /**
         * Kampf hat begonnen - UI in Kampf-Modus wechseln
         */
        void onCombatStart();

        /**
         * Kampf beendet - won=true bei Sieg, false bei Niederlage
         */
        void onCombatEnd(boolean won);

        /**
         * Spieler ist gestorben - Game Over Screen anzeigen
         */
        void onGameOver();

        /**
         * Alle Räume abgeschlossen - Victory Screen anzeigen
         */
        void onVictory();

        /**
         * Spieler-Aktion für Debug/Log-Zwecke
         */
        void onPlayerAction(String action);

        /**
         * Spieler-Aktion für Debug/Log-Zwecke
         */
        void onWaitingForRoomTransition();

        /**
         * Story-Text soll angezeigt werden (Vollbild-Modus)
         */
        void onStoryDisplay(String storyText);
    }

    private World world;
    private FightManager fightManager;
    private Player player;
    private GameEventListener eventListener;
    private final Queue<String> inputQueue;
    private boolean waitingForInput;
    private boolean gameRunning;

    private final String[] roomNames = {
        "Eingangsbereich",
        "Verlassener Flur",
        "Alte Bibliothek",
        "Speisekammer",
        "Speisesaal",
        "Laboratorium",
        "Dunkler Korridor",
        "Boss-Kammer"
    };

    /**
     * Initialisiert den GameManager und das Spiel-System
     *
     * Erstellt alle benötigten Komponenten: - Input-Queue für asynchrone
     * Benutzereingaben - Spiel-Zustand Variablen - Ruft initializeGame() für
     * World/Player Setup auf
     */
    public GameManager() {
        this.inputQueue = new LinkedList<>();
        this.waitingForInput = false;
        // initializeGame();
    }

    public void setEventListener(GameEventListener listener) {
        this.eventListener = listener;
    }

    /**
     * Initialisiert das Spiel mit Standardwerten
     */
    public void initializeGameWithPlayer(PlayerType playerType) {
        // Spieler mit PlayerType erstellen
        // lifeTotal, armourValue, initiative, attack, defense, damage
        this.player = new Player(playerType);

        this.fightManager = new FightManager(player);
        this.fightManager.setCombatEventListener(this);  // Set GameManager as combat listener

        // Welt mit vordefinierten Räumen erstellen (erstmal so testweise)
        RoomType[] gameRooms = {
            RoomType.INTRO_ROOM,
            RoomType.FLOOR_ROOM,
            RoomType.PANTRY_1,
            RoomType.LIBRARY_ROOM,
            RoomType.DINING_HALL,
            RoomType.LABORATORY,
            RoomType.CORRIDOR,
            RoomType.PANTRY_2,
            RoomType.FINAL_ROOM
        };

        this.world = new World(gameRooms);
        this.gameRunning = true;
    }

    /**
     * Startet das Spiel (GUI Version)
     */
    public void startGame() {
        if (eventListener != null) {
            // Show story for the initial room first
            String roomStory = getRoomStory();
            eventListener.onStoryDisplay(roomStory);
            // onRoomChange() will be called after story is read in continueAfterStory()

            eventListener.onPlayerHealthChange(player.getLifeTotal(), player.getMaxLife());
        }

        // processCurrentRoom() will be called after story is read
    }

    /**
     * Verarbeitet den aktuellen Raum
     */
    private void processCurrentRoom() {
        Room currentRoom = world.getCurrent_room();

        if (currentRoom.getEnemiesInRoom().length == 0) {
            notifyLog("Dieser Raum ist leer");
            checkForNextRoom();
        } else {
            startCombat(currentRoom);
        }
    }

    /**
     * Startet einen Kampf (GUI Version)
     */
    private void startCombat(Room room) {
        if (eventListener != null) {
            eventListener.onCombatStart();
            eventListener.onEnemyUpdate(createEnemyArray(room.getEnemiesInRoom()));
        }

        notifyLog("Kampf beginnt! " + room.getEnemiesInRoom().length + " Gegner greifen an!");

        // Start fight asynchronously - combat will be handled through events
        fightManager.startFight(room.getEnemiesInRoom());
    }

    /**
     * Konvertiert EnemyTypes zu Enemy-Objekten für die GUI
     */
    private Enemy[] createEnemyArray(EnemyType[] types) {
        Enemy[] enemies = new Enemy[types.length];
        for (int i = 0; i < types.length; i++) {
            enemies[i] = new Enemy(types[i]);
        }
        return enemies;
    }

    /**
     * Prüft ob es weitere Räume gibt
     */
    private void checkForNextRoom() {
        if (world.getCurrent_room_number() < world.getRoom_count() - 1) {
            // Warte auf choice
            waitingForInput = true;
            notifyLog("Möchtest du zum nächsten Raum gehen? (J/N)");

            // Notify UI that we're waiting for room transition input
            if (eventListener != null) {
                eventListener.onWaitingForRoomTransition();
            }
        } else {
            victory();
        }
    }

    /**
     * Verarbeitet Spieler-Input von der GUI
     */
    public void handlePlayerInput(String input) {
        inputQueue.add(input);

        if (waitingForInput) {
            processInput();
        }
    }

    /**
     * Verarbeitet Spieler-Aktion im Kampf
     */
    public void handleCombatAction(int enemyIndex, int finte, int wuchtschlag) {
        if (eventListener != null) {
            String action = "Angriff auf Gegner " + (enemyIndex + 1);
            if (finte > 0) {
                action += " (Finte Lv." + finte + ")";
            }
            if (wuchtschlag > 0) {
                action += " (Wuchtschlag Lv." + wuchtschlag + ")";
            }
            eventListener.onPlayerAction(action);
        }
        // Diese Info wird an FightManager weitergegeben
    }

    private void processInput() {
        String input = inputQueue.poll();
        if (input != null) {
            if (input.equalsIgnoreCase("J") || input.equalsIgnoreCase("Y")) {
                waitingForInput = false;
                advanceToNextRoom();
            } else if (input.equalsIgnoreCase("N")) {
                // falls man ausruhen + heilen haben will:
                //
                // notifyLog("Du ruhst dich aus...");
                // // Kleine Heilung beim Ausruhen
                // int healAmount = 5;
                // player.heal(healAmount);
                // notifyLog("Du heilst " + healAmount + " HP.");
                // if (eventListener != null) {
                //     eventListener.onPlayerHealthChange(player.getLifeTotal(), maxPlayerHealth);
                // }
                // 
            }
        }
    }

    /**
     * Spieler geht zum nächsten Raum
     */
    public void advanceToNextRoom() {
        if (world.hasNextRoom()) {
            world.advance_to_next_room();

            if (eventListener != null) {
                // Show the full story before room exploration
                String roomStory = getRoomStory();
                eventListener.onStoryDisplay(roomStory);
                // onRoomChange() will be called after story is read in continueAfterStory()
            }

            notifyLog("Du betrittst: " + roomNames[world.getCurrent_room_number()]);
            // processCurrentRoom() will be called after story is read
        }
    }

    private void gameOver() {
        gameRunning = false;
        notifyLog("GAME OVER - Du wurdest besiegt!");
        if (eventListener != null) {
            eventListener.onGameOver();
        }
    }

    private void victory() {
        gameRunning = false;
        notifyLog("SIEG! Du hast alle Räume gemeistert!");
        if (eventListener != null) {
            eventListener.onVictory();
        }
    }

    private void notifyLog(String message) {
        if (eventListener != null) {
            eventListener.onCombatLogUpdate(message);
        }
    }

    public FightManager getFightManager() {
        return fightManager;
    }

    public Player getPlayer() {
        return player;
    }

    public World getWorld() {
        return world;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public boolean isWaitingForInput() {
        return waitingForInput;
    }

    /**
     * Gibt den aktuellen raumnamen zurück
     */
    public String getCurrentRoomName() {
        int index = Math.min(world.getCurrent_room_number(), roomNames.length - 1);
        return roomNames[index];
    }

    /**
     * Gibt die aktuelle raumbeschreibung zurück
     */
    public String getRoomDescription() {
        String[] descriptions = { // kurze beschreibungen für exploration
            "Ein verlassener Eingangsbereich.",
            "Ein dunkler Flur.",
            "Eine alte Bibliothek.",
            "Eine Speisekammer.",
            "Ein großer Speisesaal.",
            "Ein wissenschaftliches Labor.",
            "Ein schwach beleuchteter Korridor.",
            "Die finale Kammer."
        };

        int index = Math.min(world.getCurrent_room_number(), descriptions.length - 1);
        return descriptions[index];
    }

    /**
     * Gibt die vollständige Story für den aktuellen Raum zurück
     */
    public String getRoomStory() {
        StoryDatabank[] stories = {
            StoryDatabank.INTRO_ROOM,
            StoryDatabank.FLOOR_ROOM,
            StoryDatabank.PANTRY_1,
            StoryDatabank.LIBRARY_ROOM,
            StoryDatabank.DINING_HALL,
            StoryDatabank.LABORATORY,
            StoryDatabank.CORRIDOR,
            StoryDatabank.FINAL_ROOM
        };

        int index = Math.min(world.getCurrent_room_number(), stories.length - 1);
        return StoryDatabank.getStory(stories[index]);
    }

    /**
     * Gets the exit story for the current room after combat is finished
     */
    public String getExitStory() {
        StoryDatabank[] exitStories = {
            StoryDatabank.INTRO_ROOM_END,
            StoryDatabank.FLOOR_ROOM_END,
            StoryDatabank.PANTRY_1_END,
            StoryDatabank.LIBRARY_ROOM_END,
            StoryDatabank.DINING_HALL_END,
            StoryDatabank.LABORATORY_END,
            StoryDatabank.CORRIDOR_END,
            StoryDatabank.FINAL_ROOM_END
        };

        int index = Math.min(world.getCurrent_room_number(), exitStories.length - 1);
        return StoryDatabank.getStory(exitStories[index]);
    }

    // === CombatEventListener Implementation ===
    @Override
    public void onRoundStart(int roundNumber) {
        notifyLog("=== Runde " + roundNumber + " ===");
    }

    @Override
    public void onCombatMessage(String message) {
        notifyLog(message);
    }

    @Override
    public void onCombatMessage(String message, FightManager.CombatMessageType messageType) {
        // Forward colored message to UI if it supports it
        if (eventListener != null && eventListener instanceof GameplayScreen) {
            ((GameplayScreen) eventListener).onCombatMessage(message, messageType);
        } else {
            // Fallback to regular message
            notifyLog(message);
        }
    }

    @Override
    public void onQueuedCombatMessage(String message, FightManager.CombatMessageType messageType, long delayMs) {
        // Forward queued message to UI if it supports it
        if (eventListener != null && eventListener instanceof GameplayScreen) {
            ((GameplayScreen) eventListener).onQueuedCombatMessage(message, messageType, delayMs);
        } else {
            // Fallback to regular message
            notifyLog(message);
        }
    }

    @Override
    public void onPlayerTurn() {
        // Notify GUI that it's the player's turn and should show combat input
        notifyLog("Du bist dran! Wähle deine Aktion.");
    }

    @Override
    public void onEnemyTurn(Enemy enemy) {
        notifyLog(enemy.getType() + " ist dran.");
    }

    @Override
    public void onPlayerHealthUpdate(int current, int max) {
        if (eventListener != null) {
            eventListener.onPlayerHealthChange(current, max);
        }
    }

    @Override
    public void onEnemyHealthUpdate(Enemy[] enemies) {
        if (eventListener != null) {
            eventListener.onEnemyUpdate(enemies);
        }
    }

    @Override
    public void onCombatEnd(boolean playerWon) {
        if (eventListener != null) {
            eventListener.onCombatEnd(playerWon);
        }

        if (!playerWon) {
            gameOver();
        } else {
            notifyLog("Kampf gewonnen!");
            // Show exit story after combat victory, before next room transition
            showExitStoryAndUpgrade();
        }
    }

    /**
     * Shows exit story and applies room upgrade after combat victory
     */
    private void showExitStoryAndUpgrade() {
        if (eventListener != null) {
            // Get the exit story for the current room
            String exitStory = getExitStory();
            if (exitStory != null && !exitStory.trim().isEmpty()) {
                // Apply the room upgrade before showing the story
                applyRoomUpgrade();

                // Show the exit story
                eventListener.onStoryDisplay(exitStory);
                // After story is read, continueAfterExitStory() will be called
            } else {
                // No exit story for this room, go directly to next room check
                checkForNextRoom();
            }
        } else {
            checkForNextRoom();
        }
    }

    /**
     * Method for GUI to call when exit story reading is finished
     */
    public void continueAfterExitStory() {
        checkForNextRoom();
    }

    /**
     * Method for GUI to call when player makes combat action
     */
    public void executeCombatAction(int targetEnemyIndex, AttackType attackType) {
        if (fightManager != null && fightManager.isWaitingForPlayerAction()) {
            fightManager.executePlayerAction(targetEnemyIndex, attackType);
        }
    }

    /**
     * Method for GUI to call when story reading is finished
     */
    public void continueAfterStory() {
        if (eventListener != null) {
            int roomNum = world.getCurrent_room_number();
            String roomName = roomNames[Math.min(roomNum, roomNames.length - 1)];
            eventListener.onRoomChange(roomNum, world.getRoom_count(), roomName);
        }
        processCurrentRoom();
    }

    /**
     * Applies room-specific upgrade based on the current room Should be called
     * when showing exit stories after clearing a room
     */
    public void applyRoomUpgrade() {
        int currentRoomIndex = world.getCurrent_room_number();

        // Get the RoomType from the world's room list
        RoomType[] gameRooms = {
            RoomType.INTRO_ROOM,
            RoomType.FLOOR_ROOM,
            RoomType.PANTRY_1,
            RoomType.LIBRARY_ROOM,
            RoomType.DINING_HALL,
            RoomType.LABORATORY,
            RoomType.CORRIDOR,
            RoomType.PANTRY_2,
            RoomType.FINAL_ROOM
        };

        if (currentRoomIndex >= 0 && currentRoomIndex < gameRooms.length) {
            RoomType currentRoomType = gameRooms[currentRoomIndex];
            RoomUpgrade roomUpgrade = RoomUpgrade.getUpgradeForRoom(currentRoomType);

            if (roomUpgrade != null && roomUpgrade.getUpgradeType() != null) {
                UpgradeType upgradeType = roomUpgrade.getUpgradeType();
                player.upgrade(upgradeType);
                if (eventListener != null) {
                    eventListener.onPlayerHealthChange(player.getLifeTotal(), player.getMaxLife());
                }

                notifyLog("Upgrade angewendet: " + upgradeType.name());
            }
        }
    }
}
