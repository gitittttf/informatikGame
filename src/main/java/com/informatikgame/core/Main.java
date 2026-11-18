package com.informatikgame.core;

import java.io.IOException;

import com.informatikgame.ui.CharacterSelectionScreen;
import com.informatikgame.ui.MainMenuScreen;
import com.informatikgame.ui.ScreenManager;
import com.informatikgame.ui.SchwierigkeitScreen;
import com.informatikgame.ui.SettingsScreen;
import com.informatikgame.ui.gameplay.GameplayScreen;

public class Main {

    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {
        try {
            // ScreenManager initialisieren
            ScreenManager screenManager = ScreenManager.getInstance();

            // Terminal initialisieren (mit Vollbild und schwarzem Hintergrund)
            screenManager.initialize();

            GameManager gameManager = new GameManager();

            // Alle Screens registrieren
            screenManager.registerScreen("menu", new MainMenuScreen());
            screenManager.registerScreen("settings", new SettingsScreen());

            GameplayScreen gameplayScreen = new GameplayScreen();
            gameplayScreen.setGameManager(gameManager);
            screenManager.registerScreen("game", gameplayScreen);

            SchwierigkeitScreen schwierigkeitScreen = new SchwierigkeitScreen();
            schwierigkeitScreen.setGameManager(gameManager);
            screenManager.registerScreen("schwierigkeit", schwierigkeitScreen);

            CharacterSelectionScreen characterSelectionScreen = new CharacterSelectionScreen();
            characterSelectionScreen.setSchwierigkeitScreen(schwierigkeitScreen);
            screenManager.registerScreen("characterSelection", characterSelectionScreen);

            // Mit Hauptmenü starten
            screenManager.switchToScreen("menu");

            // Game-Loop starten
            screenManager.run();

        } catch (IOException e) {
            System.err.println("Fehler beim Initialisieren des Terminals: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
