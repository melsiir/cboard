package com.cboard.keyboard.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {
    
    private static final String PREFS_NAME = "cboard_settings";
    private SharedPreferences prefs;
    
    public SettingsManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public void setKeyboardHeight(int height) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("keyboard_height", height);
        editor.apply();
    }
    
    public int getKeyboardHeight() {
        return prefs.getInt("keyboard_height", 200); // Default 200dp
    }
    
    public void setButtonSize(int size) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("button_size", size);
        editor.apply();
    }
    
    public int getButtonSize() {
        return prefs.getInt("button_size", 50); // Default 50dp
    }
    
    public void setSpaceAwareMode(boolean enabled) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("space_aware", enabled);
        editor.apply();
    }
    
    public boolean isSpaceAwareMode() {
        return prefs.getBoolean("space_aware", true); // Default to true
    }
    
    public void setDoubleSpacePeriod(boolean enabled) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("double_space_period", enabled);
        editor.apply();
    }
    
    public boolean isDoubleSpacePeriodEnabled() {
        return prefs.getBoolean("double_space_period", true); // Default to true
    }
}