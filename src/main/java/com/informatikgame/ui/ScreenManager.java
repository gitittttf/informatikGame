package com.informatikgame.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

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
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
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
    private SwingTerminalFrame swingFrame;
    private int currentFontSize = 16;

    // Display mode settings
    public enum DisplayMode {
        FULLSCREEN, WINDOWED
    }

    public enum ScalingMode {
        SMALL(0.5), NORMAL(1.0), LARGE(1.5), EXTRA_LARGE(2.0);

        private final double scaleFactor;

        ScalingMode(double scaleFactor) {
            this.scaleFactor = scaleFactor;
        }

        public double getScaleFactor() {
            return scaleFactor;
        }
    }

    private DisplayMode currentDisplayMode = DisplayMode.FULLSCREEN;
    private ScalingMode currentScalingMode = ScalingMode.NORMAL;
    private String selectedDisplayId = null; // null = default display
    private JFrame customFrame;

    // Display information class
    public static class DisplayInfo {

        public final String id;
        public final String name;
        public final Rectangle bounds;
        public final Dimension resolution;
        public final boolean primary;

        public DisplayInfo(String id, String name, Rectangle bounds, Dimension resolution, boolean primary) {
            this.id = id;
            this.name = name;
            this.bounds = bounds;
            this.resolution = resolution;
            this.primary = primary;
        }

        @Override
        public String toString() {
            return name + " (" + resolution.width + "x" + resolution.height + ")" + (primary ? " [Primary]" : "");
        }
    }

    // Simple dynamic scaling
    private final AtomicBoolean isRecreating = new AtomicBoolean(false);
    private final int TARGET_COLUMNS = 120;
    private final int TARGET_ROWS = 40;
    private final int MIN_FONT_SIZE = 8;
    private final int MAX_FONT_SIZE = 32;
    private int resizeCheckCounter = 3; // Check every N frames

    // Farbtheme für das Spiel
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
     * Get all available displays
     */
    public List<DisplayInfo> getAvailableDisplays() {
        List<DisplayInfo> displays = new ArrayList<>();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = ge.getScreenDevices();
        GraphicsDevice defaultDevice = ge.getDefaultScreenDevice();

        for (int i = 0; i < devices.length; i++) {
            GraphicsDevice device = devices[i];
            GraphicsConfiguration config = device.getDefaultConfiguration();
            Rectangle bounds = config.getBounds();
            Dimension resolution = new Dimension(device.getDisplayMode().getWidth(), device.getDisplayMode().getHeight());
            boolean isPrimary = device.equals(defaultDevice);

            String id = device.getIDstring();
            String name = "Monitor " + (i + 1);

            displays.add(new DisplayInfo(id, name, bounds, resolution, isPrimary));
        }

        return displays;
    }

    /**
     * Set the selected display for fullscreen mode
     */
    public void setSelectedDisplay(String displayId) {
        this.selectedDisplayId = displayId;
    }

    /**
     * Get the currently selected display ID
     */
    public String getSelectedDisplayId() {
        return selectedDisplayId;
    }

    /**
     * Find graphics device by ID or return default
     */
    private GraphicsDevice findDeviceByIdOrDefault(String displayId) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

        if (displayId != null) {
            for (GraphicsDevice device : ge.getScreenDevices()) {
                if (device.getIDstring().equals(displayId)) {
                    return device;
                }
            }
        }

        return ge.getDefaultScreenDevice();
    }

    /**
     * Compute the best font size using binary search with FontMetrics
     */
    private int computeBestFontSize(GraphicsDevice device, int targetColumns, int targetRows) {
        Dimension resolution = new Dimension(device.getDisplayMode().getWidth(), device.getDisplayMode().getHeight());

        int minFontSize = MIN_FONT_SIZE;
        int maxFontSize = MAX_FONT_SIZE;
        int bestFontSize = minFontSize;

        // Binary search for the largest font size that fits
        while (minFontSize <= maxFontSize) {
            int midFontSize = (minFontSize + maxFontSize) / 2;

            // Create a temporary font to measure
            Font testFont = new Font(Font.MONOSPACED, Font.PLAIN, midFontSize);

            // Use a BufferedImage to get FontMetrics
            BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            Graphics g = img.getGraphics();
            FontMetrics fm = g.getFontMetrics(testFont);

            // Calculate required dimensions
            int charWidth = fm.charWidth('M'); // Use 'M' as it's typically the widest character
            int charHeight = fm.getHeight();

            int requiredWidth = targetColumns * charWidth;
            int requiredHeight = targetRows * charHeight;

            g.dispose();

            // Check if this font size fits
            if (requiredWidth <= resolution.width && requiredHeight <= resolution.height) {
                bestFontSize = midFontSize;
                minFontSize = midFontSize + 1;
            } else {
                maxFontSize = midFontSize - 1;
            }
        }

        return bestFontSize;
    }

    /**
     * Initialisiert das Terminal im echten Vollbildmodus mit korrekter
     * Skalierung
     */
    public void initialize() throws IOException {
        initializeWithSettings(currentDisplayMode, currentScalingMode);
    }

    /**
     * Initialisiert das Terminal mit den gegebenen Display-Einstellungen
     */
    private void initializeWithSettings(DisplayMode displayMode, ScalingMode scalingMode) throws IOException {
        // Get the selected graphics device
        GraphicsDevice gd = findDeviceByIdOrDefault(selectedDisplayId);

        // Compute optimal font size using binary search
        int fontSize = computeBestFontSize(gd, TARGET_COLUMNS, TARGET_ROWS);

        // Apply scaling factor
        fontSize = (int) (fontSize * scalingMode.getScaleFactor());
        fontSize = Math.max(MIN_FONT_SIZE, Math.min(MAX_FONT_SIZE, fontSize));
        currentFontSize = fontSize;

        // Terminalfactory setup
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        terminalFactory.setPreferTerminalEmulator(true);

        TerminalSize initialSize = new TerminalSize(TARGET_COLUMNS, TARGET_ROWS);
        terminalFactory.setInitialTerminalSize(initialSize);

        // Fontconf mit berechneter Größe
        Font terminalFont = new Font(Font.MONOSPACED, Font.PLAIN, fontSize);
        SwingTerminalFontConfiguration fontConfig = SwingTerminalFontConfiguration.newInstance(terminalFont);
        terminalFactory.setTerminalEmulatorFontConfiguration(fontConfig);

        // Terminal erstellen
        terminal = terminalFactory.createTerminal();

        // Configure frame based on display mode with letterboxing
        if (terminal instanceof SwingTerminalFrame originalFrame) {
            this.swingFrame = originalFrame;

            if (displayMode == DisplayMode.FULLSCREEN) {
                configureFullscreenModeWithLetterboxing(originalFrame, gd, fontSize);
            } else {
                configureWindowedModeWithLetterboxing(originalFrame, gd, fontSize, scalingMode);
            }
        }

        // Screen erstellen
        screen = new TerminalScreen(terminal);
        screen.startScreen();
        screen.setCursorPosition(null);

        // Graphics konfigurieren
        graphics = screen.newTextGraphics();
        graphics.setBackgroundColor(BACKGROUND_COLOR);
        graphics.setForegroundColor(TEXT_COLOR);

        clearScreen();
        System.out.println("Terminal-Setup abgeschlossen - Modus: " + displayMode + ", Skalierung: " + scalingMode + ", Schriftgröße: " + fontSize);
    }

    /**
     * Configure fullscreen mode with proper window disposal and centered layout
     */
    private void configureFullscreenModeWithLetterboxing(SwingTerminalFrame originalFrame, GraphicsDevice gd, int fontSize) {
        // Calculate terminal dimensions for proper centering
        Font terminalFont = new Font(Font.MONOSPACED, Font.PLAIN, fontSize);
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        FontMetrics fm = g.getFontMetrics(terminalFont);

        int charWidth = fm.charWidth('M');
        int charHeight = fm.getHeight();
        int terminalWidth = TARGET_COLUMNS * charWidth;
        int terminalHeight = TARGET_ROWS * charHeight;
        g.dispose();

        // FIXED: Properly configure for true fullscreen with centering
        if (gd.isFullScreenSupported()) {
            try {
                System.out.println("Attempting TRUE CENTERED fullscreen on " + gd.getIDstring() + "...");

                // Get screen dimensions for centering calculations
                Rectangle screenBounds = gd.getDefaultConfiguration().getBounds();
                int screenWidth = screenBounds.width;
                int screenHeight = screenBounds.height;

                // Calculate centering offsets
                int centerX = (screenWidth - terminalWidth) / 2;
                int centerY = (screenHeight - terminalHeight) / 2;

                // Dispose first, then configure as undecorated
                originalFrame.dispose();
                originalFrame.setUndecorated(true);
                originalFrame.setResizable(false);
                originalFrame.setAlwaysOnTop(true);

                // Set black background for letterboxing effect
                originalFrame.getContentPane().setBackground(Color.BLACK);

                // Create a centered layout panel
                JPanel centerPanel = new JPanel(new BorderLayout());
                centerPanel.setBackground(Color.BLACK);
                centerPanel.setOpaque(true);

                // Get the terminal component and center it
                Component terminalComponent = originalFrame.getContentPane().getComponent(0);
                originalFrame.getContentPane().removeAll();

                // Add padding panels for centering
                JPanel topPadding = new JPanel();
                topPadding.setBackground(Color.BLACK);
                topPadding.setPreferredSize(new Dimension(screenWidth, centerY));

                JPanel bottomPadding = new JPanel();
                bottomPadding.setBackground(Color.BLACK);
                bottomPadding.setPreferredSize(new Dimension(screenWidth, centerY));

                JPanel leftPadding = new JPanel();
                leftPadding.setBackground(Color.BLACK);
                leftPadding.setPreferredSize(new Dimension(centerX, terminalHeight));

                JPanel rightPadding = new JPanel();
                rightPadding.setBackground(Color.BLACK);
                rightPadding.setPreferredSize(new Dimension(centerX, terminalHeight));

                // Create center container for terminal
                JPanel terminalContainer = new JPanel(new BorderLayout());
                terminalContainer.setBackground(Color.BLACK);
                terminalContainer.add(terminalComponent, BorderLayout.CENTER);

                // Assemble the centered layout
                centerPanel.add(topPadding, BorderLayout.NORTH);
                centerPanel.add(bottomPadding, BorderLayout.SOUTH);
                centerPanel.add(leftPadding, BorderLayout.WEST);
                centerPanel.add(rightPadding, BorderLayout.EAST);
                centerPanel.add(terminalContainer, BorderLayout.CENTER);

                // Add the centered panel to the frame
                originalFrame.getContentPane().add(centerPanel, BorderLayout.CENTER);

                // Now set fullscreen on properly configured window
                gd.setFullScreenWindow(originalFrame);
                originalFrame.setVisible(true);

                // Verify fullscreen actually worked
                if (gd.getFullScreenWindow() == originalFrame) {
                    System.out.println("SUCCESS: FULLSCREEN activated on " + gd.getIDstring()
                            + " - Resolution: " + screenWidth + "x" + screenHeight
                            + ", Font: " + fontSize + ", Game: " + terminalWidth + "x" + terminalHeight + " centered at (" + centerX + "," + centerY + ")"
                            + ", Display: " + (selectedDisplayId != null ? selectedDisplayId : "Default"));
                } else {
                    System.out.println("WARNING: Fullscreen was requested but not active, falling back to borderless window");
                    configureBorderlessWindow(originalFrame, gd);
                }
            } catch (Exception e) {
                System.out.println("ERROR: Exclusive fullscreen failed: " + e.getMessage() + ", using borderless window");
                e.printStackTrace();
                configureBorderlessWindow(originalFrame, gd);
            }
        } else {
            System.out.println("INFO: Exclusive fullscreen not supported on this system, using borderless window");
            configureBorderlessWindow(originalFrame, gd);
        }

        // Ensure the frame gets focus for input handling
        SwingUtilities.invokeLater(() -> {
            originalFrame.toFront();
            originalFrame.requestFocus();
        });
    }

    /**
     * Configure borderless window that covers the selected monitor
     */
    private void configureBorderlessWindow(SwingTerminalFrame frame, GraphicsDevice gd) {
        Rectangle bounds = gd.getDefaultConfiguration().getBounds();

        // FIXED: Properly configure borderless window
        frame.dispose();
        frame.setUndecorated(true);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        frame.setBounds(bounds);
        frame.setExtendedState(Frame.NORMAL); // Ensure not maximized, use explicit bounds
        frame.setVisible(true);

        System.out.println("BORDERLESS FULLSCREEN activated on " + gd.getIDstring()
                + " - Bounds: " + bounds.width + "x" + bounds.height);
    }

    /**
     * Configure windowed mode with proper sizing and centering
     */
    private void configureWindowedModeWithLetterboxing(SwingTerminalFrame frame, GraphicsDevice gd, int fontSize, ScalingMode scalingMode) {
        // Skip setUndecorated and setResizable calls to avoid IllegalComponentStateException
        // The Lanterna SwingTerminalFrame should be properly configured by default

        // Calculate accurate window size using FontMetrics
        Font terminalFont = new Font(Font.MONOSPACED, Font.PLAIN, fontSize);
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();
        FontMetrics fm = g.getFontMetrics(terminalFont);

        int charWidth = fm.charWidth('M');
        int charHeight = fm.getHeight();
        int gameWidth = TARGET_COLUMNS * charWidth;
        int gameHeight = TARGET_ROWS * charHeight;
        g.dispose();

        // Add padding for window decorations
        int windowWidth = gameWidth + 50;
        int windowHeight = gameHeight + 100;

        // Center window on the selected monitor
        Rectangle bounds = gd.getDefaultConfiguration().getBounds();
        int windowX = bounds.x + (bounds.width - windowWidth) / 2;
        int windowY = bounds.y + (bounds.height - windowHeight) / 2;

        frame.setBounds(windowX, windowY, windowWidth, windowHeight);
        frame.setVisible(true);

        System.out.println("Windowed mode activated on " + gd.getIDstring()
                + " - Window: " + windowWidth + "x" + windowHeight + ", Font: " + fontSize
                + ", Game: " + gameWidth + "x" + gameHeight);
    }

    /**
     * Öffentliche Methoden für SettingsScreen
     */
    public boolean isFullscreenMode() {
        return currentDisplayMode == DisplayMode.FULLSCREEN;
    }

    public ScalingMode getCurrentScalingMode() {
        return currentScalingMode;
    }

    /**
     * Apply new display settings with optional display selection
     */
    public void applyDisplaySettings(Object displayMode, Object scalingMode) {
        applyDisplaySettings(displayMode, scalingMode, selectedDisplayId);
    }

    /**
     * Apply new display settings including display selection
     */
    public void applyDisplaySettings(Object displayMode, Object scalingMode, String displayId) {
        try {
            // Convert from SettingsScreen enums to ScreenManager enums
            DisplayMode newDisplayMode = DisplayMode.valueOf(displayMode.toString());
            ScalingMode newScalingMode = ScalingMode.valueOf(scalingMode.toString());

            // Store new settings
            currentDisplayMode = newDisplayMode;
            currentScalingMode = newScalingMode;
            selectedDisplayId = displayId;

            // Shutdown current terminal
            shutdownTerminal();

            // Reinitialize with new settings
            initializeWithSettings(newDisplayMode, newScalingMode);

            // Ensure focus is restored after reinitialization
            SwingUtilities.invokeLater(() -> {
                if (swingFrame != null) {
                    swingFrame.toFront();
                    swingFrame.requestFocus();
                    // Give the terminal component focus specifically
                    SwingUtilities.invokeLater(() -> {
                        swingFrame.requestFocusInWindow();
                    });
                }
            });

            System.out.println("Display settings applied - Mode: " + newDisplayMode
                    + ", Scaling: " + newScalingMode + ", Display: "
                    + (displayId != null ? displayId : "Default"));

        } catch (Exception e) {
            System.err.println("Error applying display settings: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Shutdown only the terminal (without ending the application)
     */
    private void shutdownTerminal() {
        try {
            // Exit fullscreen mode first from all devices
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            for (GraphicsDevice device : ge.getScreenDevices()) {
                if (device.getFullScreenWindow() != null) {
                    device.setFullScreenWindow(null);
                }
            }

            // No custom frame cleanup needed since we use original frame directly
            // Clean up terminal and screen
            if (screen != null) {
                screen.stopScreen();
            }
            if (terminal != null) {
                terminal.close();
            }
            if (swingFrame != null) {
                swingFrame.setVisible(false);
                swingFrame.dispose();
            }
        } catch (Exception e) {
            System.err.println("Error shutting down terminal: " + e.getMessage());
        }
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

            // Einfache dynamische Font-Skalierung
            checkForSimpleResize();

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
     * Resize-Check deaktiviert da Fenster nicht mehr verändert werden kann
     * (Vollbildmodus ohne Dekoration, Größenänderung deaktiviert)
     */
    private void checkForSimpleResize() {
        // Resize-Check deaktiviert da Vollbildmodus mit fester Größe
        // Das verhindert das "Herumspringen" der ASCII-Zeichen
        return;
    }

    /**
     * Shutdown the GUI cleanly
     */
    public void shutdown() {
        shutdownTerminal();
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
