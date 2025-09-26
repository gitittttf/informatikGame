// package com.informatikgame.ui;

// import com.googlecode.lanterna.TerminalSize;
// import com.googlecode.lanterna.graphics.TextGraphics;

// public class SettingsScreen extends GameScreen {

//     // Screen-spezifische Variablen
//     private int selectedOption = 0;
//     private boolean animationComplete = false;

//     @Override
//     public void initialize() {
//         // Einmalige Initialisierung
//         // Animation-Counter zurücksetzen
//         // Daten laden
//     }

//     @Override
//     public void render(TextGraphics graphics) {
//         TerminalSize size = screenManager.getSize();

//         // Screen-Inhalt zeichnen
//         // UI-Elemente positionieren
//         // Animationen anwenden
//     }

//     @Override
//     public void handleInput(KeyStroke keyStroke) {
//         // Input-Behandlung basierend auf keyStroke.getKeyType()
//         // Navigation, Auswahl, Zurück
//     }

//     @Override
//     public void update() {
//         super.update(); // WICHTIG: Für animationFrame

//         // Screen-spezifische Animationen
//         // Zustand-Updates
//     }

//     @Override
//     public boolean onEscape() {
//         // Navigation zurück zum Hauptmenü
//         // return true = Spiel beenden
//         // return false = ESC ignorieren
//         screenManager.switchToScreen("menu");
//         return false;
//     }
// }
