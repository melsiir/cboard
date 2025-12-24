package com.cboard.keyboard.layout;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import java.util.HashMap;
import java.util.Map;

public class LayoutManager {
    
    private Context context;
    private Map<String, CustomKeyboard> keyboardMap;
    
    public LayoutManager(Context context) {
        this.context = context;
        this.keyboardMap = new HashMap<>();
        initializeKeyboards();
    }
    
    private void initializeKeyboards() {
        // Initialize default keyboards
        keyboardMap.put("qwerty", new CustomKeyboard(context, R.xml.qwerty));
        keyboardMap.put("programming", new CustomKeyboard(context, R.xml.programming));
    }
    
    public CustomKeyboard getKeyboard(String layoutName) {
        return keyboardMap.get(layoutName);
    }
    
    public void addCustomKey(String layoutName, int rowIndex, int x, int y, int width, int height, 
                            int code, String label, String popupCharacters) {
        CustomKeyboard keyboard = keyboardMap.get(layoutName);
        if (keyboard != null) {
            keyboard.addKeyToRow(rowIndex, x, y, width, height, code, label, popupCharacters);
        }
    }
    
    public boolean removeKey(String layoutName, int rowIndex, int code) {
        CustomKeyboard keyboard = keyboardMap.get(layoutName);
        if (keyboard != null) {
            return keyboard.removeKeyFromRow(rowIndex, code);
        }
        return false;
    }
    
    public boolean swapKeys(String layoutName, int code1, int code2) {
        CustomKeyboard keyboard = keyboardMap.get(layoutName);
        if (keyboard != null) {
            return keyboard.swapKeys(code1, code2);
        }
        return false;
    }
    
    public void createNewLayout(String layoutName, int xmlLayoutResId) {
        keyboardMap.put(layoutName, new CustomKeyboard(context, xmlLayoutResId));
    }
    
    public void createNewLayout(String layoutName, CharSequence characters, int columns) {
        keyboardMap.put(layoutName, new CustomKeyboard(context, 
            R.xml.qwerty, characters, columns, 0)); // Using qwerty as base template
    }
    
    public boolean deleteLayout(String layoutName) {
        return keyboardMap.remove(layoutName) != null;
    }
    
    public String[] getLayoutNames() {
        return keyboardMap.keySet().toArray(new String[0]);
    }
}