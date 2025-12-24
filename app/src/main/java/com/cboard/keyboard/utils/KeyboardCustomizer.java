package com.cboard.keyboard.utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class KeyboardCustomizer {
    
    private static final String PREFS_NAME = "cboard_custom_layouts";
    private Context context;
    private SharedPreferences prefs;
    
    public KeyboardCustomizer(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public void saveCustomLayout(String layoutName, Map<Integer, String> layout) {
        SharedPreferences.Editor editor = prefs.edit();
        
        // Save the number of rows
        editor.putInt(layoutName + "_rows", layout.size());
        
        // Save each row
        int rowIndex = 0;
        for (Map.Entry<Integer, String> entry : layout.entrySet()) {
            editor.putString(layoutName + "_row_" + rowIndex, entry.getValue());
            rowIndex++;
        }
        
        editor.apply();
    }
    
    public Map<Integer, String> loadCustomLayout(String layoutName) {
        Map<Integer, String> layout = new HashMap<>();
        
        int rows = prefs.getInt(layoutName + "_rows", 0);
        
        for (int i = 0; i < rows; i++) {
            String row = prefs.getString(layoutName + "_row_" + i, null);
            if (row != null) {
                layout.put(i, row);
            }
        }
        
        return layout;
    }
    
    public Set<String> getSavedLayoutNames() {
        Set<String> layoutNames = new HashSet<>();
        Map<String, ?> allPrefs = prefs.getAll();
        
        for (String key : allPrefs.keySet()) {
            if (key.endsWith("_rows")) {
                layoutNames.add(key.substring(0, key.length() - 5)); // Remove "_rows" suffix
            }
        }
        
        return layoutNames;
    }
    
    public void deleteLayout(String layoutName) {
        Set<String> layoutNames = getSavedLayoutNames();
        if (layoutNames.contains(layoutName)) {
            SharedPreferences.Editor editor = prefs.edit();
            
            int rows = prefs.getInt(layoutName + "_rows", 0);
            
            // Remove each row
            for (int i = 0; i < rows; i++) {
                editor.remove(layoutName + "_row_" + i);
            }
            
            // Remove the row count
            editor.remove(layoutName + "_rows");
            
            editor.apply();
        }
    }
}