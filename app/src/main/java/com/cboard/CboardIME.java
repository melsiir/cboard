package com.cboard;

import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.media.AudioManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputConnection;

public class CboardIME extends InputMethodService {
    
    private ThemedKeyboardView keyboardView;
    private SharedPreferences prefs;

    @Override
    public View onCreateInputView() {
        keyboardView = new ThemedKeyboardView(this);
        prefs = getSharedPreferences("CboardPrefs", MODE_PRIVATE);
        
        // Set up the key click listener
        keyboardView.setOnKeyClickListener(this::onKeyPressed);
        
        updateKeyboardDimensions();
        return keyboardView;
    }

    private void onKeyPressed(String key) {
        // Get the current input connection
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        
        // Handle special keys
        switch (key) {
            case "BKSP":
            case "DEL":
                ic.deleteSurroundingText(1, 0);
                break;
            case "SPACE":
                ic.commitText(" ", 1);
                break;
            case "RETURN":
                ic.commitText("\n", 1);
                break;
            case "SHIFT":
            case "SHIFT2":
                // Toggle shift state - for now just log it
                break;
            default:
                ic.commitText(key, 1);
                break;
        }
        
        // Play key click sound if enabled
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (am != null) {
            am.playSoundEffect(AudioManager.FX_KEY_CLICK);
        }
    }
    
    private void updateKeyboardDimensions() {
        // Get keyboard height preference (0-40, where 20 is default = 50%)
        int heightProgress = prefs.getInt("keyboard_height", 20);
        // Convert to percentage: 30% to 70% (20 corresponds to 50%)
        int heightPercentage = 30 + heightProgress; // 30 + 20 = 50%
        
        // Calculate actual height based on screen dimensions
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int calculatedHeight = (int) (screenHeight * heightPercentage / 100.0);
        
        // Update the keyboard view height
        ViewGroup.LayoutParams params = keyboardView.getLayoutParams();
        params.height = calculatedHeight;
        keyboardView.setLayoutParams(params);
    }
    
    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Update theme when configuration changes (e.g. theme change)
        if (keyboardView != null) {
            keyboardView.updateTheme();
        }
    }
}