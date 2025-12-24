package com.cboard.keyboard.utils;

import android.content.Context;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.HashMap;

public class ExportImportManager {
    
    private static final String TAG = "ExportImportManager";
    private Context context;
    
    public ExportImportManager(Context context) {
        this.context = context;
    }
    
    public void exportAllSettings() {
        try {
            // Export general settings
            SettingsManager settingsManager = new SettingsManager(context);
            StringBuilder exportData = new StringBuilder();
            
            exportData.append("keyboard_height=").append(settingsManager.getKeyboardHeight()).append("\n");
            exportData.append("button_size=").append(settingsManager.getButtonSize()).append("\n");
            exportData.append("space_aware=").append(settingsManager.isSpaceAwareMode()).append("\n");
            exportData.append("double_space_period=").append(settingsManager.isDoubleSpacePeriodEnabled()).append("\n");
            
            // Export custom layouts
            KeyboardCustomizer customizer = new KeyboardCustomizer(context);
            for (String layoutName : customizer.getSavedLayoutNames()) {
                Map<Integer, String> layout = customizer.loadCustomLayout(layoutName);
                exportData.append("layout_name=").append(layoutName).append("\n");
                exportData.append("layout_rows=").append(layout.size()).append("\n");
                
                for (Map.Entry<Integer, String> entry : layout.entrySet()) {
                    exportData.append("row_").append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
                }
                exportData.append("end_layout\n");
            }
            
            // Write to file
            FileOutputStream fileOut = context.openFileOutput("cboard_full_backup.txt", Context.MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileOut);
            outputWriter.write(exportData.toString());
            outputWriter.close();
            
            Log.d(TAG, "Settings exported successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error exporting settings", e);
        }
    }
    
    public void importAllSettings() {
        try {
            FileInputStream fileIn = context.openFileInput("cboard_full_backup.txt");
            InputStreamReader inputReader = new InputStreamReader(fileIn);
            
            StringBuilder stringBuilder = new StringBuilder();
            char[] inputBuffer = new char[2048];
            int size;
            while ((size = inputReader.read(inputBuffer)) > 0) {
                stringBuilder.append(inputBuffer, 0, size);
            }
            String importData = stringBuilder.toString();
            
            // Parse the data
            String[] lines = importData.split("\n");
            SettingsManager settingsManager = new SettingsManager(context);
            KeyboardCustomizer customizer = new KeyboardCustomizer(context);
            
            String currentLayoutName = null;
            Map<Integer, String> currentLayout = null;
            
            for (String line : lines) {
                if (line.startsWith("keyboard_height=")) {
                    int height = Integer.parseInt(line.split("=")[1]);
                    settingsManager.setKeyboardHeight(height);
                } else if (line.startsWith("button_size=")) {
                    int size = Integer.parseInt(line.split("=")[1]);
                    settingsManager.setButtonSize(size);
                } else if (line.startsWith("space_aware=")) {
                    boolean enabled = Boolean.parseBoolean(line.split("=")[1]);
                    settingsManager.setSpaceAwareMode(enabled);
                } else if (line.startsWith("double_space_period=")) {
                    boolean enabled = Boolean.parseBoolean(line.split("=")[1]);
                    settingsManager.setDoubleSpacePeriod(enabled);
                } else if (line.startsWith("layout_name=")) {
                    // Start a new layout
                    if (currentLayoutName != null && currentLayout != null) {
                        // Save the previous layout
                        customizer.saveCustomLayout(currentLayoutName, currentLayout);
                    }
                    currentLayoutName = line.split("=")[1];
                    currentLayout = new HashMap<>();
                } else if (line.startsWith("row_") && currentLayout != null) {
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        String key = parts[0]; // e.g., "row_0"
                        String value = parts[1];
                        int rowIndex = Integer.parseInt(key.substring(4)); // Extract index from "row_0"
                        currentLayout.put(rowIndex, value);
                    }
                } else if (line.equals("end_layout")) {
                    // End the current layout
                    if (currentLayoutName != null && currentLayout != null) {
                        customizer.saveCustomLayout(currentLayoutName, currentLayout);
                        currentLayoutName = null;
                        currentLayout = null;
                    }
                }
            }
            
            // Save the last layout if exists
            if (currentLayoutName != null && currentLayout != null) {
                customizer.saveCustomLayout(currentLayoutName, currentLayout);
            }
            
            inputReader.close();
            
            Log.d(TAG, "Settings imported successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error importing settings", e);
        }
    }
}