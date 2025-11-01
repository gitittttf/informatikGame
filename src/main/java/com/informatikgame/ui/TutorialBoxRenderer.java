package com.informatikgame.ui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import java.util.ArrayList;
import java.util.List;

/**
 * Reusable utility for rendering tutorial boxes with typewriter effect.
 * Provides consistent styling and behavior across all tutorial implementations.
 */
public class TutorialBoxRenderer {
    
    // Box styling constants
    private static final int HORIZONTAL_PADDING = 4;
    private static final int VERTICAL_PADDING = 2;
    private static final int DEFAULT_MAX_WIDTH = 70;
    private static final int DEFAULT_MIN_HEIGHT = 5;
    
    // Typewriter effect speed (characters per frame)
    private static final int TYPEWRITER_SPEED = 2;
    
    /**
     * Renders a tutorial box with typewriter effect at the default bottom position.
     * 
     * @param graphics The TextGraphics to render to
     * @param size The terminal size
     * @param text The tutorial text to display
     * @param animationFrame Current animation frame
     * @param startFrame The frame when animation started
     * @param isAwaitingConfirmation Whether to show "press ENTER" prompt
     * @param darkened Whether to use darkened colors (for background dimming)
     */
    public static void renderTutorialBox(TextGraphics graphics, TerminalSize size, 
                                        String text, int animationFrame, int startFrame,
                                        boolean isAwaitingConfirmation, boolean darkened) {
        // Default position at bottom of screen
        int boxY = calculateDefaultBoxY(size, text);
        renderTutorialBox(graphics, size, text, animationFrame, startFrame, 
                         isAwaitingConfirmation, darkened, boxY);
    }
    
    /**
     * Renders a tutorial box with typewriter effect at a custom Y position.
     * 
     * @param graphics The TextGraphics to render to
     * @param size The terminal size
     * @param text The tutorial text to display
     * @param animationFrame Current animation frame
     * @param startFrame The frame when animation started
     * @param isAwaitingConfirmation Whether to show "press ENTER" prompt
     * @param darkened Whether to use darkened colors (for background dimming)
     * @param customBoxY Custom Y position for the box
     */
    public static void renderTutorialBox(TextGraphics graphics, TerminalSize size, 
                                        String text, int animationFrame, int startFrame,
                                        boolean isAwaitingConfirmation, boolean darkened,
                                        int customBoxY) {
        // Calculate typewriter effect
        int charsShown = Math.max(0, (animationFrame - startFrame)) * TYPEWRITER_SPEED;
        charsShown = Math.min(charsShown, text.length());
        String textToShow = text.substring(0, charsShown);
        
        // Calculate box dimensions
        int boxWidth = Math.min(DEFAULT_MAX_WIDTH, size.getColumns() - 10);
        int maxTextWidth = boxWidth - (HORIZONTAL_PADDING * 2);
        
        // Word-wrap the text
        List<String> wrappedLines = wrapText(textToShow, maxTextWidth);
        
        // Calculate dynamic box height
        int boxHeight = Math.max(DEFAULT_MIN_HEIGHT, wrappedLines.size() + VERTICAL_PADDING * 2);
        int boxX = (size.getColumns() - boxWidth) / 2;
        int boxY = customBoxY;
        
        // Choose colors based on darkened flag
        TextColor bgColor = darkened ? new TextColor.RGB(3, 3, 7) : new TextColor.RGB(10, 10, 20);
        TextColor textColor = darkened ? new TextColor.RGB(70, 70, 70) : TextColor.ANSI.WHITE;
        TextColor promptColor = darkened ? new TextColor.RGB(80, 80, 0) : TextColor.ANSI.YELLOW;
        
        // Fill background
        graphics.setBackgroundColor(bgColor);
        for (int y = 0; y < boxHeight; y++) {
            for (int x = 0; x < boxWidth; x++) {
                graphics.setCharacter(boxX + x, boxY + y, ' ');
            }
        }
        
        // Draw border
        if (darkened) {
            drawDarkenedStoryBorder(graphics, boxX, boxY, boxWidth, boxHeight);
        } else {
            drawStoryBorder(graphics, boxX, boxY, boxWidth, boxHeight);
        }
        
        // Draw wrapped text with padding
        graphics.setForegroundColor(textColor);
        int tx = boxX + HORIZONTAL_PADDING;
        int ty = boxY + VERTICAL_PADDING;
        
        for (String wrappedLine : wrappedLines) {
            if (ty < boxY + boxHeight - VERTICAL_PADDING) {
                graphics.putString(new TerminalPosition(tx, ty++), wrappedLine);
            }
        }
        
        // Draw prompt when awaiting confirmation
        if (isAwaitingConfirmation) {
            String prompt = "Drücke ENTER um fortzufahren";
            graphics.setForegroundColor(promptColor);
            int promptX = (size.getColumns() - prompt.length()) / 2;
            graphics.putString(new TerminalPosition(promptX, boxY + boxHeight), prompt);
        }
    }
    
    /**
     * Wraps text to fit within the specified width.
     */
    public static List<String> wrapText(String text, int maxWidth) {
        List<String> wrappedLines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        
        for (String word : words) {
            if (line.length() + word.length() + 1 > maxWidth) {
                if (line.length() > 0) {
                    wrappedLines.add(line.toString());
                    line = new StringBuilder();
                }
            }
            if (line.length() > 0) line.append(' ');
            line.append(word);
        }
        
        if (line.length() > 0) {
            wrappedLines.add(line.toString());
        }
        
        return wrappedLines;
    }
    
    /**
     * Calculates the default Y position for the tutorial box (bottom of screen).
     */
    private static int calculateDefaultBoxY(TerminalSize size, String text) {
        int boxWidth = Math.min(DEFAULT_MAX_WIDTH, size.getColumns() - 10);
        int maxTextWidth = boxWidth - (HORIZONTAL_PADDING * 2);
        List<String> wrappedLines = wrapText(text, maxTextWidth);
        int boxHeight = Math.max(DEFAULT_MIN_HEIGHT, wrappedLines.size() + VERTICAL_PADDING * 2);
        return size.getRows() - boxHeight - 3;
    }
    
    /**
     * Draws a story-style border around the tutorial box.
     */
    private static void drawStoryBorder(TextGraphics graphics, int x, int y, int width, int height) {
        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        
        // Draw horizontal lines
        for (int i = 0; i < width; i++) {
            graphics.setCharacter(x + i, y, '═');
            graphics.setCharacter(x + i, y + height - 1, '═');
        }
        
        // Draw vertical lines
        for (int i = 0; i < height; i++) {
            graphics.setCharacter(x, y + i, '║');
            graphics.setCharacter(x + width - 1, y + i, '║');
        }
        
        // Draw corners
        graphics.setCharacter(x, y, '╔');
        graphics.setCharacter(x + width - 1, y, '╗');
        graphics.setCharacter(x, y + height - 1, '╚');
        graphics.setCharacter(x + width - 1, y + height - 1, '╝');
        
        // Decorative elements
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        for (int i = 2; i < width - 2; i += 4) {
            graphics.setCharacter(x + i, y, '◈');
            graphics.setCharacter(x + i, y + height - 1, '◈');
        }
    }
    
    /**
     * Draws a darkened story-style border for use when background is dimmed.
     */
    private static void drawDarkenedStoryBorder(TextGraphics graphics, int x, int y, int width, int height) {
        graphics.setForegroundColor(new TextColor.RGB(20, 80, 80)); // Darkened cyan
        
        // Draw horizontal lines
        for (int i = 0; i < width; i++) {
            graphics.setCharacter(x + i, y, '═');
            graphics.setCharacter(x + i, y + height - 1, '═');
        }
        
        // Draw vertical lines
        for (int i = 0; i < height; i++) {
            graphics.setCharacter(x, y + i, '║');
            graphics.setCharacter(x + width - 1, y + i, '║');
        }
        
        // Draw corners
        graphics.setCharacter(x, y, '╔');
        graphics.setCharacter(x + width - 1, y, '╗');
        graphics.setCharacter(x, y + height - 1, '╚');
        graphics.setCharacter(x + width - 1, y + height - 1, '╝');
        
        // Decorative elements
        graphics.setForegroundColor(new TextColor.RGB(60, 60, 0)); // Darkened yellow
        for (int i = 2; i < width - 2; i += 4) {
            graphics.setCharacter(x + i, y, '◈');
            graphics.setCharacter(x + i, y + height - 1, '◈');
        }
    }
    
    /**
     * Utility to darken an RGB color by a factor.
     */
    public static TextColor darkenColor(TextColor.RGB color, int factor) {
        return new TextColor.RGB(
            color.getRed() / factor,
            color.getGreen() / factor,
            color.getBlue() / factor
        );
    }
}
