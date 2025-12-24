package com.cboard.keyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.content.Context;
import android.view.KeyEvent;
import com.cboard.keyboard.layout.LayoutManager;
import com.cboard.keyboard.utils.SettingsManager;
import com.cboard.keyboard.utils.SpaceAwareManager;

/**
 * This is a simplified version for testing purposes
 */
public class TestKeyboardService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    
    private KeyboardView keyboardView;
    private Keyboard keyboard;
    
    private LayoutManager layoutManager;
    private SettingsManager settingsManager;
    private SpaceAwareManager spaceAwareManager;

    @Override
    public void onCreate() {
        super.onCreate();
        layoutManager = new LayoutManager(this);
        settingsManager = new SettingsManager(this);
        spaceAwareManager = new SpaceAwareManager(this);
    }

    @Override
    public View onCreateInputView() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        
        // Load default keyboard
        keyboard = layoutManager.getKeyboard("qwerty");
        
        // Apply custom keyboard height if available
        applyKeyboardHeight();
        
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
        return keyboardView;
    }
    
    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        // Apply settings when input view starts
        applyKeyboardHeight();
    }
    
    private void applyKeyboardHeight() {
        if (keyboardView != null) {
            // Convert dp to pixels
            float scale = getResources().getDisplayMetrics().density;
            int pixelHeight = (int) (settingsManager.getKeyboardHeight() * scale + 0.5f);
            
            // Apply height to the keyboard view
            android.view.ViewGroup.LayoutParams layoutParams = keyboardView.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.height = pixelHeight;
                keyboardView.setLayoutParams(layoutParams);
            }
        }
    }

    @Override
    public void onPress(int primaryCode) {
        // Change key color when pressed (visual feedback)
        if (keyboardView != null) {
            keyboardView.setPreviewEnabled(true);
        }
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection != null) {
            switch (primaryCode) {
                case Keyboard.KEYCODE_DELETE:
                    inputConnection.deleteSurroundingText(1, 0);
                    break;
                case Keyboard.KEYCODE_SHIFT:
                    // Toggle shift state
                    keyboard.setShifted(!keyboard.isShifted());
                    keyboardView.invalidateAllKeys();
                    break;
                case 10: // Enter key
                    inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                    break;
                case 32: // Space key
                    spaceAwareManager.handleSpacePress(inputConnection);
                    break;
                default:
                    char code = (char) primaryCode;
                    spaceAwareManager.handleCharacter(inputConnection, code);
                    break;
            }
        }
    }

    @Override
    public void onText(CharSequence text) {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection != null) {
            inputConnection.commitText(text, 1);
        }
    }

    @Override
    public void swipeLeft() {
        // Switch to programming layout on swipe left
        keyboard = layoutManager.getKeyboard("programming");
        keyboardView.setKeyboard(keyboard);
    }

    @Override
    public void swipeRight() {
        // Switch to normal layout on swipe right
        keyboard = layoutManager.getKeyboard("qwerty");
        keyboardView.setKeyboard(keyboard);
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeUp() {
    }
}