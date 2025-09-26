package com.informatikgame.ui;

import java.awt.Frame;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

/**
 * Zentrale Screen-Verwaltung für das gesamte Spiel. Verwaltet alle Screens und
 * ermöglicht nahtlose Übergänge mit Animationen
 */
public class ScreenManager {

    private static ScreenManager instance;
    private Terminal terminal;
    private Screen screen;
    private Map<String, GameScreen> screens;
    private GameScreen currentScreen;
    private TextGraphics graphics;
    private boolean running;

    // Farb-Theme für das Spiel
    public static final TextColor BACKGROUND_COLOR = TextColor.ANSI.BLACK;
    public static final TextColor PRIMARY_COLOR = TextColor.ANSI.GREEN;
    public static final TextColor SECONDARY_COLOR = TextColor.ANSI.CYAN;
    public static final TextColor DANGER_COLOR = TextColor.ANSI.RED;
    public static final TextColor WARNING_COLOR = TextColor.ANSI.YELLOW;
    public static final TextColor TEXT_COLOR = TextColor.ANSI.WHITE;

    private ScreenManager() {
        screens = new HashMap<>();
        running = true;
    }

    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    /**
     * Initialisiert das Terminal im Vollbildmodus mit schwarzem Hintergrund
     */
    public void initialize() throws IOException {
        // Terminal-Factory mit besonderen Einstellungen
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();

        // Setze Terminal-Eigenschaften für bessere Kontrolle
        terminalFactory.setForceTextTerminal(false);
        terminalFactory.setPreferTerminalEmulator(false);

        // Die initial terminal size maximieren
        terminalFactory.setInitialTerminalSize(new TerminalSize(120, 40));

        // Erstelle Terminal
        terminal = terminalFactory.createTerminal();

        // Wenn SwingTerminalFrame dann fullscreen konfigurieren
        if (terminal instanceof SwingTerminalFrame frame) {

            // Fenster anzeigen und maximieren
            frame.setVisible(true);

            // VOllbildmodus aktivieren
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);

            // Fenster schließen beendet das Programm
            frame.setDefaultCloseOperation(3); // WindowConstants.EXIT_ON_CLOSE = 3

            System.out.println("VOllbildmodus aktiviert");
        }

        // Screen erstellen
        screen = new TerminalScreen(terminal);
        screen.startScreen();

        // Cursor verstecken
        screen.setCursorPosition(null);

        // Graphics-Objekt für Zeichenoperationen
        graphics = screen.newTextGraphics();
        graphics.setBackgroundColor(BACKGROUND_COLOR);
        graphics.setForegroundColor(TEXT_COLOR);

        // Bildschirm initial schwarz färben
        clearScreen();
    }

    /**
     * Registriert einen neuen Screen
     */
    public void registerScreen(String name, GameScreen screen) {
        screens.put(name, screen);
        screen.setScreenManager(this);
    }

    /**
     * Wechselt zu einem anderen Screen mit Fade-Animation
     */
    public void switchToScreen(String screenName) {
        GameScreen newScreen = screens.get(screenName);
        if (newScreen != null) {
            // Fade-Out Animation
            if (currentScreen != null) {
                fadeOut();
            }

            currentScreen = newScreen;
            currentScreen.initialize();

            // Fade-In Animation
            fadeIn();
        }
    }

    /**
     * Hauptspiel-Loop
     */
    public void run() throws IOException {
        while (running && currentScreen != null) {
            // Bildschirm löschen
            clearScreen();

            // Aktuellen Screen rendern
            currentScreen.render(graphics);

            // Bildschirm aktualisieren
            screen.refresh();

            // Input verarbeiten
            KeyStroke keyStroke = screen.pollInput();
            if (keyStroke != null) {
                if (keyStroke.getKeyType() == KeyType.Escape) {
                    // ESC zum Beenden (kann angepasst werden)
                    if (currentScreen.onEscape()) {
                        running = false;
                    }
                } else {
                    currentScreen.handleInput(keyStroke);
                }
            }

            // Update-Logik (für Animationen)
            currentScreen.update();

            // Frame-Rate begrenzen (ca. 30 FPS)
            try {
                Thread.sleep(33);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        shutdown();
    }

    /**
     * Bildschirm komplett schwarz färben
     */
    private void clearScreen() {
        TerminalSize size = screen.getTerminalSize();
        graphics.setBackgroundColor(BACKGROUND_COLOR);
        graphics.setForegroundColor(TEXT_COLOR);
        graphics.fillRectangle(new TerminalPosition(0, 0), size, ' ');
    }

    /**
     * Fade-In Animation
     */
    private void fadeIn() {
        // Einfache Fade-Animation durch schrittweises Zeichnen
        for (int i = 0; i < 10; i++) {
            clearScreen();
            graphics.setForegroundColor(new TextColor.RGB(
                    i * 25, i * 25, i * 25
            ));
            currentScreen.render(graphics);
            try {
                screen.refresh();
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        graphics.setForegroundColor(TEXT_COLOR);
    }

    /**
     * Fade-Out Animation
     */
    private void fadeOut() {
        for (int i = 10; i >= 0; i--) {
            clearScreen();
            graphics.setForegroundColor(new TextColor.RGB(
                    i * 25, i * 25, i * 25
            ));
            currentScreen.render(graphics);
            try {
                screen.refresh();
                Thread.sleep(30);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Beendet die GUI sauber
     */
    public void shutdown() {
        try {
            if (screen != null) {
                screen.stopScreen();
            }
            if (terminal != null) {
                terminal.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getter für Screens
    public Screen getScreen() {
        return screen;
    }

    public TextGraphics getGraphics() {
        return graphics;
    }

    public TerminalSize getSize() {
        return screen.getTerminalSize();
    }

    public void stopRunning() {
        running = false;
    }
}
