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

public class CBoardKeyboardService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    
    private KeyboardView keyboardView;
    private Keyboard keyboard;
    private Keyboard programmingKeyboard;
    
    private boolean caps = false;
    private boolean isProgrammingLayout = false;
    
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
        
        // Load keyboard layouts
        keyboard = layoutManager.getKeyboard("qwerty");
        programmingKeyboard = layoutManager.getKeyboard("programming");
        
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
                    caps = !caps;
                    keyboard.setShifted(caps);
                    programmingKeyboard.setShifted(caps);
                    keyboardView.invalidateAllKeys();
                    break;
                case Keyboard.KEYCODE_DONE:
                case 10: // Enter key
                    inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                    break;
                case -10: // Switch to programming layout
                    isProgrammingLayout = !isProgrammingLayout;
                    keyboardView.setKeyboard(isProgrammingLayout ? programmingKeyboard : keyboard);
                    break;
                case 32: // Space key
                    spaceAwareManager.handleSpacePress(inputConnection);
                    break;
                default:
                    char code = (char) primaryCode;
                    if (Character.isLetter(code) && caps) {
                        code = Character.toUpperCase(code);
                    }
                    
                    // Use space-aware manager for character input
                    spaceAwareManager.handleCharacter(inputConnection, code);
                    
                    // Reset caps after letter input (for programming, we typically don't want caps lock)
                    if (Character.isLetter(code)) {
                        caps = false;
                        keyboard.setShifted(caps);
                        programmingKeyboard.setShifted(caps);
                        keyboardView.invalidateAllKeys();
                    }
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
        isProgrammingLayout = true;
        keyboardView.setKeyboard(programmingKeyboard);
    }

    @Override
    public void swipeRight() {
        // Switch to normal layout on swipe right
        isProgrammingLayout = false;
        keyboardView.setKeyboard(keyboard);
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeUp() {
    }
}