package com.cboard;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SettingsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "CboardPrefs";
    private static final String KEY_KEYBOARD_HEIGHT = "keyboard_height";
    private static final String KEY_BUTTON_SIZE = "button_size";
    private static final String KEY_KEYBOARD_LAYOUT = "keyboard_layout";
    
    private SeekBar seekbarKeyboardHeight;
    private RadioGroup radioGroupButtonSize;
    private RadioButton radioSmall, radioMedium, radioLarge;
    private Button btnExportSettings, btnImportSettings, btnCustomizeLayout;

    // For handling file picker results
    private ActivityResultLauncher<Intent> exportFileLauncher;
    private ActivityResultLauncher<Intent> importFileLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initFileLaunchers();
        initViews();
        loadSettings();
        setupListeners();
    }

    private void initFileLaunchers() {
        exportFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    exportSettings(uri);
                }
            }
        );

        importFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    importSettings(uri);
                }
            }
        );
    }

    private void initViews() {
        seekbarKeyboardHeight = findViewById(R.id.seekbarKeyboardHeight);
        radioGroupButtonSize = findViewById(R.id.radioGroupButtonSize);
        radioSmall = findViewById(R.id.radioSmall);
        radioMedium = findViewById(R.id.radioMedium);
        radioLarge = findViewById(R.id.radioLarge);
        btnExportSettings = findViewById(R.id.btnExportSettings);
        btnImportSettings = findViewById(R.id.btnImportSettings);
        btnCustomizeLayout = findViewById(R.id.btnCustomizeLayout);
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        // Load keyboard height (default to 20 which represents 50%)
        int heightProgress = prefs.getInt(KEY_KEYBOARD_HEIGHT, 20);
        seekbarKeyboardHeight.setProgress(heightProgress);
        
        // Load button size (default to medium)
        int buttonSize = prefs.getInt(KEY_BUTTON_SIZE, 1); // 0=small, 1=medium, 2=large
        switch (buttonSize) {
            case 0:
                radioSmall.setChecked(true);
                break;
            case 1:
                radioMedium.setChecked(true);
                break;
            case 2:
                radioLarge.setChecked(true);
                break;
        }
    }

    private void setupListeners() {
        seekbarKeyboardHeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    saveSetting(KEY_KEYBOARD_HEIGHT, progress);
                    // Update the displayed value (30 + progress = percentage)
                    int percentage = 30 + progress;
                    android.widget.TextView heightValue = findViewById(R.id.keyboardHeightValue);
                    if (heightValue != null) {
                        heightValue.setText(percentage + "%");
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Restart the input method to apply changes
                restartInputMethod();
            }
        });

        radioGroupButtonSize.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int buttonSize = 1; // default to medium
                if (checkedId == R.id.radioSmall) {
                    buttonSize = 0;
                } else if (checkedId == R.id.radioLarge) {
                    buttonSize = 2;
                }
                saveSetting(KEY_BUTTON_SIZE, buttonSize);

                // Restart the input method to apply changes
                restartInputMethod();
            }
        });

        // Enable the customize layout button and set its listener
        btnCustomizeLayout.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, CustomizeLayoutActivity.class);
            startActivity(intent);
        });

        // Enable export/import buttons with functionality
        btnExportSettings.setOnClickListener(v -> openExportDialog());
        btnImportSettings.setOnClickListener(v -> openImportDialog());
    }

    private void openExportDialog() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, "cboard_settings.json");
        exportFileLauncher.launch(intent);
    }

    private void openImportDialog() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        importFileLauncher.launch(intent);
    }

    private void exportSettings(Uri uri) {
        try {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            
            JSONObject settings = new JSONObject();
            settings.put(KEY_KEYBOARD_HEIGHT, prefs.getInt(KEY_KEYBOARD_HEIGHT, 20));
            settings.put(KEY_BUTTON_SIZE, prefs.getInt(KEY_BUTTON_SIZE, 1));
            settings.put(KEY_KEYBOARD_LAYOUT, prefs.getString(KEY_KEYBOARD_LAYOUT, ""));
            
            String settingsJson = settings.toString();
            
            // Write to the selected file
            try (FileOutputStream outputStream = (FileOutputStream) getContentResolver().openOutputStream(uri)) {
                outputStream.write(settingsJson.getBytes());
                outputStream.flush();
                
                // Show success message
                android.widget.Toast.makeText(this, "Settings exported successfully!", android.widget.Toast.LENGTH_SHORT).show();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            android.widget.Toast.makeText(this, "Error exporting settings: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
        }
    }

    private void importSettings(Uri uri) {
        try {
            // Read from the selected file
            StringBuilder stringBuilder = new StringBuilder();
            try (InputStream inputStream = getContentResolver().openInputStream(uri);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            }
            
            String settingsJson = stringBuilder.toString();
            JSONObject settings = new JSONObject(settingsJson);
            
            // Save the imported settings
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            
            if (settings.has(KEY_KEYBOARD_HEIGHT)) {
                editor.putInt(KEY_KEYBOARD_HEIGHT, settings.getInt(KEY_KEYBOARD_HEIGHT));
            }
            
            if (settings.has(KEY_BUTTON_SIZE)) {
                editor.putInt(KEY_BUTTON_SIZE, settings.getInt(KEY_BUTTON_SIZE));
            }
            
            if (settings.has(KEY_KEYBOARD_LAYOUT)) {
                editor.putString(KEY_KEYBOARD_LAYOUT, settings.getString(KEY_KEYBOARD_LAYOUT));
            }
            
            editor.apply();
            
            // Reload the UI to reflect imported settings
            loadSettings();
            
            // Show success message
            android.widget.Toast.makeText(this, "Settings imported successfully!", android.widget.Toast.LENGTH_SHORT).show();
            
            // Restart the input method to apply changes
            restartInputMethod();
            
        } catch (JSONException e) {
            e.printStackTrace();
            android.widget.Toast.makeText(this, "Invalid settings file format", android.widget.Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            android.widget.Toast.makeText(this, "Error importing settings: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
        }
    }

    private void saveSetting(String key, int value) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    
    private void restartInputMethod() {
        // This will trigger the keyboard to reload with new settings
        // In a real implementation, we might want to use a more elegant approach
        // like broadcasting an intent to update the keyboard
    }
}