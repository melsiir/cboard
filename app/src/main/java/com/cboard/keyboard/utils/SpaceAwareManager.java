package com.cboard.keyboard.utils;

import android.content.Context;
import android.view.inputmethod.InputConnection;
import java.util.regex.Pattern;

public class SpaceAwareManager {
    
    private SettingsManager settingsManager;
    private long lastSpaceTime = 0;
    private static final long DOUBLE_SPACE_TIMEOUT = 800; // ms
    private static final Pattern WORD_PATTERN = Pattern.compile("[a-zA-Z0-9_]+");
    
    public SpaceAwareManager(Context context) {
        this.settingsManager = new SettingsManager(context);
    }
    
    /**
     * Handles space key press with space-aware features
     */
    public void handleSpacePress(InputConnection inputConnection) {
        if (!settingsManager.isSpaceAwareMode()) {
            inputConnection.commitText(" ", 1);
            return;
        }
        
        // Handle double space to period functionality
        if (settingsManager.isDoubleSpacePeriodEnabled()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastSpaceTime < DOUBLE_SPACE_TIMEOUT) {
                // Replace last space with period
                inputConnection.deleteSurroundingText(1, 0);
                inputConnection.commitText(".", 1);
                lastSpaceTime = 0; // Reset to prevent triple space issues
            } else {
                inputConnection.commitText(" ", 1);
                lastSpaceTime = currentTime;
            }
        } else {
            inputConnection.commitText(" ", 1);
        }
    }
    
    /**
     * Auto-corrects common programming patterns
     */
    public void handleCharacter(InputConnection inputConnection, char character) {
        if (!settingsManager.isSpaceAwareMode()) {
            inputConnection.commitText(String.valueOf(character), 1);
            return;
        }
        
        String textToCommit = String.valueOf(character);
        
        // Auto-complete brackets and quotes when appropriate
        switch (character) {
            case '(':
            case '[':
            case '{':
                // Add closing bracket/brace and position cursor between
                inputConnection.commitText(textToCommit + getClosingChar(character), 1);
                inputConnection.commitText("", -1); // Move cursor back
                break;
            case '"':
            case '\'':
                // Add closing quote and position cursor between
                inputConnection.commitText(textToCommit + character, 1);
                inputConnection.commitText("", -1); // Move cursor back
                break;
            default:
                inputConnection.commitText(textToCommit, 1);
                break;
        }
    }
    
    private char getClosingChar(char openChar) {
        switch (openChar) {
            case '(': return ')';
            case '[': return ']';
            case '{': return '}';
            default: return openChar;
        }
    }
    
    /**
     * Checks if the current context is appropriate for space-aware features
     */
    public boolean isSpaceAwareContext(InputConnection inputConnection) {
        if (inputConnection == null) {
            return false;
        }
        
        // For now, we assume space-aware features are always applicable
        // In a more advanced implementation, we could check the input type
        return true;
    }
}