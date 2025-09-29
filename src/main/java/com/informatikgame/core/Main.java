package com.informatikgame.core;

import java.io.IOException;

import com.informatikgame.ui.CharacterSelectionScreen;
import com.informatikgame.ui.GameplayScreen;
import com.informatikgame.ui.MainMenuScreen;
import com.informatikgame.ui.ScreenManager;
import com.informatikgame.ui.SettingsScreen;

public class Main {

    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {
        try {
            // ScreenManager initialisieren
            ScreenManager screenManager = ScreenManager.getInstance();

            // Terminal initialisieren (mit Vollbild und schwarzem Hintergrund)
            screenManager.initialize();

            // GameManager erstellen
            GameManager gameManager = new GameManager();

            // Alle Screens registrieren
            screenManager.registerScreen("menu", new MainMenuScreen());

            GameplayScreen gameplayScreen = new GameplayScreen();
            gameplayScreen.setGameManager(gameManager);
            screenManager.registerScreen("game", gameplayScreen);

            screenManager.registerScreen("settings", new SettingsScreen());

            CharacterSelectionScreen characterSelectionScreen = new CharacterSelectionScreen();
            characterSelectionScreen.setGameManager(gameManager);
            screenManager.registerScreen("characterSelection", characterSelectionScreen);
            // screenManager.registerScreen("help", new HelpScreen());
            // screenManager.registerScreen("credits", new CreditsScreen());

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
