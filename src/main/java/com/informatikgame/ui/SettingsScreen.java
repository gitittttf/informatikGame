package com.informatikgame.ui;

import java.util.List;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;

/**
 * Einstellungsscreen mit Display-Modi und Skalierungsoptionen
 */
public class SettingsScreen extends GameScreen {

    // Use ScreenManager enums to avoid type conflicts
    private static final String[] DISPLAY_MODE_NAMES = {
        "Vollbildmodus", "Fenstermodus"
    };

    private static final String[] SCALING_MODE_NAMES = {
        "Klein (50%)", "Normal (100%)", "Groß (150%)", "Extra Groß (200%)"
    };

    private String getDisplayModeName(ScreenManager.DisplayMode mode) {
        return DISPLAY_MODE_NAMES[mode.ordinal()];
    }

    private String getScalingModeName(ScreenManager.ScalingMode mode) {
        return SCALING_MODE_NAMES[mode.ordinal()];
    }

    // Current settings
    private ScreenManager.DisplayMode currentDisplayMode = ScreenManager.DisplayMode.FULLSCREEN;
    private ScreenManager.ScalingMode currentScalingMode = ScreenManager.ScalingMode.NORMAL;
    private String currentSelectedDisplayId = null;

    // Pending settings (changed but not applied)
    private ScreenManager.DisplayMode pendingDisplayMode = ScreenManager.DisplayMode.FULLSCREEN;
    private ScreenManager.ScalingMode pendingScalingMode = ScreenManager.ScalingMode.NORMAL;
    private String pendingSelectedDisplayId = null;

    // Available displays
    private List<ScreenManager.DisplayInfo> availableDisplays;
    private int selectedDisplayIndex = 0;

    // UI state
    private int selectedOption = 0;
    private final String[] settingLabels = {
        "Display Modus:",
        "Monitor Auswahl:",
        "Vollbild Skalierung:",
        "< Zurück zum Hauptmenü"
    };

    private boolean hasChanges = false;
    private boolean showApplyButton = false;

    @Override
    public void initialize() {
        // Get current settings from ScreenManager if available
        currentDisplayMode = screenManager.isFullscreenMode()
                ? ScreenManager.DisplayMode.FULLSCREEN : ScreenManager.DisplayMode.WINDOWED;
        currentScalingMode = screenManager.getCurrentScalingMode();
        currentSelectedDisplayId = screenManager.getSelectedDisplayId();

        // Get available displays
        availableDisplays = screenManager.getAvailableDisplays();

        // Find current display index
        selectedDisplayIndex = 0;
        if (currentSelectedDisplayId != null) {
            for (int i = 0; i < availableDisplays.size(); i++) {
                if (availableDisplays.get(i).id.equals(currentSelectedDisplayId)) {
                    selectedDisplayIndex = i;
                    break;
                }
            }
        }

        // Initialize pending settings to current values
        pendingDisplayMode = currentDisplayMode;
        pendingScalingMode = currentScalingMode;
        pendingSelectedDisplayId = currentSelectedDisplayId;
        hasChanges = false;
        showApplyButton = false;
    }

    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = screenManager.getSize();

        // Background particles (simple version)
        renderBackgroundEffect(graphics, size);

        // Title
        graphics.setForegroundColor(ScreenManager.PRIMARY_COLOR);
        drawCentered(graphics, "╔══════════════════════════════════════╗", 3);
        drawCentered(graphics, "║            EINSTELLUNGEN             ║", 4);
        drawCentered(graphics, "╚══════════════════════════════════════╝", 5);

        // Settings box (bigger to accommodate display selection)
        int boxY = 8;
        int boxWidth = 60;
        int boxHeight = 16;
        int boxX = (size.getColumns() - boxWidth) / 2;

        drawBox(graphics, boxX, boxY, boxWidth, boxHeight,
                ScreenManager.SECONDARY_COLOR, ScreenManager.BACKGROUND_COLOR);

        // Settings content
        int contentY = boxY + 2;
        int currentSetting = 0;

        // Display Mode setting
        renderDisplayModeSetting(graphics, boxX, contentY, currentSetting++);

        // Display selection and scaling (only show if fullscreen is pending)
        if (pendingDisplayMode == ScreenManager.DisplayMode.FULLSCREEN) {
            // Display selection
            renderDisplaySelectionSetting(graphics, boxX, contentY + 2, currentSetting++);

            // Scaling setting
            renderScalingSetting(graphics, boxX, contentY + 4, currentSetting++);

            // Back option
            renderBackOption(graphics, boxX, contentY + 6, currentSetting);
        } else {
            // Back option (higher up when display selection and scaling are hidden)
            renderBackOption(graphics, boxX, contentY + 2, currentSetting);
        }

        // Apply button (bottom right)
        if (showApplyButton) {
            int applyY = size.getRows() - 4;
            int applyX = size.getColumns() - 20;

            // Apply button with animation
            TextColor applyColor = (animationFrame % 20 < 10)
                    ? new TextColor.RGB(255, 255, 0) : new TextColor.RGB(200, 200, 0);

            graphics.setForegroundColor(applyColor);
            graphics.setBackgroundColor(new TextColor.RGB(0, 50, 0));
            graphics.putString(new TerminalPosition(applyX, applyY), "┌────────────────┐");
            graphics.putString(new TerminalPosition(applyX, applyY + 1), "│  [A] ANWENDEN  │");
            graphics.putString(new TerminalPosition(applyX, applyY + 2), "└────────────────┘");
        }

        // Footer with controls
        graphics.setBackgroundColor(ScreenManager.BACKGROUND_COLOR);
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        String controls = "↑↓ Navigation | ←→ Werte ändern | ENTER Auswählen | ESC Zurück";
        if (showApplyButton) {
            controls += " | A Anwenden";
        }
        drawCentered(graphics, controls, size.getRows() - 2);
    }

    private void renderDisplayModeSetting(TextGraphics graphics, int boxX, int y, int index) {
        boolean selected = (selectedOption == index);

        graphics.setBackgroundColor(selected ? new TextColor.RGB(0, 30, 0) : ScreenManager.BACKGROUND_COLOR);
        graphics.setForegroundColor(selected ? TextColor.ANSI.YELLOW : ScreenManager.TEXT_COLOR);

        String label = settingLabels[index];
        graphics.putString(new TerminalPosition(boxX + 2, y), label);

        // Current value with arrows
        String valueDisplay = "< " + getDisplayModeName(pendingDisplayMode) + " >";
        graphics.setForegroundColor(selected ? TextColor.ANSI.CYAN : ScreenManager.SECONDARY_COLOR);
        graphics.putString(new TerminalPosition(boxX + 25, y), valueDisplay);

        // Show if this setting has changed
        if (pendingDisplayMode != currentDisplayMode) {
            graphics.setForegroundColor(new TextColor.RGB(255, 150, 0));
            graphics.putString(new TerminalPosition(boxX + 55, y), "*");
        }
    }

    private void renderDisplaySelectionSetting(TextGraphics graphics, int boxX, int y, int index) {
        boolean selected = (selectedOption == index);

        graphics.setBackgroundColor(selected ? new TextColor.RGB(0, 30, 0) : ScreenManager.BACKGROUND_COLOR);
        graphics.setForegroundColor(selected ? TextColor.ANSI.YELLOW : ScreenManager.TEXT_COLOR);

        String label = settingLabels[index];
        graphics.putString(new TerminalPosition(boxX + 2, y), label);

        // Current display name
        String displayName = "Standard";
        if (!availableDisplays.isEmpty() && selectedDisplayIndex < availableDisplays.size()) {
            displayName = availableDisplays.get(selectedDisplayIndex).toString();
        }

        // Truncate if too long
        if (displayName.length() > 25) {
            displayName = displayName.substring(0, 22) + "...";
        }

        String valueDisplay = "< " + displayName + " >";
        graphics.setForegroundColor(selected ? TextColor.ANSI.CYAN : ScreenManager.SECONDARY_COLOR);
        graphics.putString(new TerminalPosition(boxX + 25, y), valueDisplay);

        // Show if this setting has changed
        String currentDisplayId = (selectedDisplayIndex < availableDisplays.size())
                ? availableDisplays.get(selectedDisplayIndex).id : null;
        boolean displayChanged = !java.util.Objects.equals(currentDisplayId, currentSelectedDisplayId);
        if (displayChanged) {
            graphics.setForegroundColor(new TextColor.RGB(255, 150, 0));
            graphics.putString(new TerminalPosition(boxX + 55, y), "*");
        }
    }

    private void renderScalingSetting(TextGraphics graphics, int boxX, int y, int index) {
        boolean selected = (selectedOption == index);

        graphics.setBackgroundColor(selected ? new TextColor.RGB(0, 30, 0) : ScreenManager.BACKGROUND_COLOR);
        graphics.setForegroundColor(selected ? TextColor.ANSI.YELLOW : ScreenManager.TEXT_COLOR);

        String label = settingLabels[index];
        graphics.putString(new TerminalPosition(boxX + 2, y), label);

        // Current value with arrows
        String valueDisplay = "< " + getScalingModeName(pendingScalingMode) + " >";
        graphics.setForegroundColor(selected ? TextColor.ANSI.CYAN : ScreenManager.SECONDARY_COLOR);
        graphics.putString(new TerminalPosition(boxX + 25, y), valueDisplay);

        // Show if this setting has changed
        if (pendingScalingMode != currentScalingMode) {
            graphics.setForegroundColor(new TextColor.RGB(255, 150, 0));
            graphics.putString(new TerminalPosition(boxX + 55, y), "*");
        }
    }

    private void renderBackOption(TextGraphics graphics, int boxX, int y, int index) {
        boolean selected = (selectedOption == index);

        graphics.setBackgroundColor(selected ? new TextColor.RGB(0, 30, 0) : ScreenManager.BACKGROUND_COLOR);
        graphics.setForegroundColor(selected ? TextColor.ANSI.YELLOW : ScreenManager.TEXT_COLOR);

        graphics.putString(new TerminalPosition(boxX + 2, y), settingLabels[3]);
    }

    private void renderBackgroundEffect(TextGraphics graphics, TerminalSize size) {
        // Simple animated background dots
        for (int i = 0; i < 20; i++) {
            int x = (int) (Math.sin(animationFrame * 0.1 + i) * 10 + i * 6) % size.getColumns();
            int y = (int) (Math.cos(animationFrame * 0.15 + i) * 5 + i * 2) % size.getRows();
            if (x >= 0 && y >= 0) {
                graphics.setForegroundColor(new TextColor.RGB(0, 50 + (i * 5), 0));
                graphics.setCharacter(x, y, '·');
            }
        }
    }

    @Override
    public void handleInput(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() != null) {
            switch (keyStroke.getKeyType()) {
                case ArrowUp -> {
                    int maxOption = (pendingDisplayMode == ScreenManager.DisplayMode.FULLSCREEN) ? 3 : 1;
                    selectedOption = (selectedOption - 1 + (maxOption + 1)) % (maxOption + 1);
                }
                case ArrowDown -> {
                    int maxOption = (pendingDisplayMode == ScreenManager.DisplayMode.FULLSCREEN) ? 3 : 1;
                    selectedOption = (selectedOption + 1) % (maxOption + 1);
                }
                case ArrowLeft ->
                    changeSettingLeft();
                case ArrowRight ->
                    changeSettingRight();
                case Enter ->
                    executeSelectedOption();
                default -> {
                }
            }
        }

        // Handle 'A' key for Apply
        if (keyStroke.getCharacter() != null
                && (keyStroke.getCharacter() == 'a' || keyStroke.getCharacter() == 'A')) {
            if (showApplyButton) {
                applySettings();
            }
        }
    }

    private void changeSettingLeft() {
        switch (selectedOption) {
            case 0 -> { // Display Mode
                pendingDisplayMode = (pendingDisplayMode == ScreenManager.DisplayMode.FULLSCREEN)
                        ? ScreenManager.DisplayMode.WINDOWED : ScreenManager.DisplayMode.FULLSCREEN;
                checkForChanges();
            }
            case 1 -> { // Display Selection (only if fullscreen)
                if (pendingDisplayMode == ScreenManager.DisplayMode.FULLSCREEN && !availableDisplays.isEmpty()) {
                    selectedDisplayIndex = (selectedDisplayIndex - 1 + availableDisplays.size()) % availableDisplays.size();
                    pendingSelectedDisplayId = availableDisplays.get(selectedDisplayIndex).id;
                    checkForChanges();
                }
            }
            case 2 -> { // Scaling (only if fullscreen)
                if (pendingDisplayMode == ScreenManager.DisplayMode.FULLSCREEN) {
                    ScreenManager.ScalingMode[] modes = ScreenManager.ScalingMode.values();
                    int currentIndex = pendingScalingMode.ordinal();
                    pendingScalingMode = modes[(currentIndex - 1 + modes.length) % modes.length];
                    checkForChanges();
                }
            }
        }
    }

    private void changeSettingRight() {
        switch (selectedOption) {
            case 0 -> { // Display Mode
                pendingDisplayMode = (pendingDisplayMode == ScreenManager.DisplayMode.FULLSCREEN)
                        ? ScreenManager.DisplayMode.WINDOWED : ScreenManager.DisplayMode.FULLSCREEN;
                checkForChanges();
            }
            case 1 -> { // Display Selection (only if fullscreen)
                if (pendingDisplayMode == ScreenManager.DisplayMode.FULLSCREEN && !availableDisplays.isEmpty()) {
                    selectedDisplayIndex = (selectedDisplayIndex + 1) % availableDisplays.size();
                    pendingSelectedDisplayId = availableDisplays.get(selectedDisplayIndex).id;
                    checkForChanges();
                }
            }
            case 2 -> { // Scaling (only if fullscreen)
                if (pendingDisplayMode == ScreenManager.DisplayMode.FULLSCREEN) {
                    ScreenManager.ScalingMode[] modes = ScreenManager.ScalingMode.values();
                    int currentIndex = pendingScalingMode.ordinal();
                    pendingScalingMode = modes[(currentIndex + 1) % modes.length];
                    checkForChanges();
                }
            }
        }
    }

    private void executeSelectedOption() {
        int maxOption = (pendingDisplayMode == ScreenManager.DisplayMode.FULLSCREEN) ? 3 : 1;

        if (selectedOption == maxOption) { // Back option
            if (hasChanges) {
                // TODO: Could add confirmation dialog here
            }
            screenManager.switchToScreen("menu");
        }
    }

    private void checkForChanges() {
        hasChanges = (pendingDisplayMode != currentDisplayMode)
                || (pendingScalingMode != currentScalingMode)
                || !java.util.Objects.equals(pendingSelectedDisplayId, currentSelectedDisplayId);
        showApplyButton = hasChanges;
    }

    private void applySettings() {
        if (!hasChanges) {
            return;
        }

        // Apply settings to ScreenManager including display selection
        screenManager.applyDisplaySettings(pendingDisplayMode, pendingScalingMode, pendingSelectedDisplayId);

        // Update current settings
        currentDisplayMode = pendingDisplayMode;
        currentScalingMode = pendingScalingMode;
        currentSelectedDisplayId = pendingSelectedDisplayId;
        hasChanges = false;
        showApplyButton = false;
    }

    @Override
    public boolean onEscape() {
        if (hasChanges) {
            // Reset pending changes
            pendingDisplayMode = currentDisplayMode;
            pendingScalingMode = currentScalingMode;
            pendingSelectedDisplayId = currentSelectedDisplayId;

            // Reset display index
            if (currentSelectedDisplayId != null && !availableDisplays.isEmpty()) {
                for (int i = 0; i < availableDisplays.size(); i++) {
                    if (availableDisplays.get(i).id.equals(currentSelectedDisplayId)) {
                        selectedDisplayIndex = i;
                        break;
                    }
                }
            }

            checkForChanges();
            return false;
        } else {
            screenManager.switchToScreen("menu");
            return false;
        }
    }
}
