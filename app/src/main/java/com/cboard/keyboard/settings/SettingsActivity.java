package com.cboard.keyboard.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cboard.keyboard.R;
import com.cboard.keyboard.utils.ExportImportManager;
import com.cboard.keyboard.utils.SettingsManager;

public class SettingsActivity extends Activity {

    private SettingsManager settingsManager;
    private ExportImportManager exportImportManager;

    private SeekBar heightSeekBar;
    private SeekBar sizeSeekBar;
    private TextView heightValue;
    private TextView sizeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);

        settingsManager = new SettingsManager(this);
        exportImportManager = new ExportImportManager(this);

        heightSeekBar = findViewById(R.id.height_seekbar);
        sizeSeekBar = findViewById(R.id.size_seekbar);
        heightValue = findViewById(R.id.height_value);
        sizeValue = findViewById(R.id.size_value);

        Button exportBtn = findViewById(R.id.export_btn);
        Button importBtn = findViewById(R.id.import_btn);

        // Load saved values
        int savedHeight = settingsManager.getKeyboardHeight();
        int savedSize = settingsManager.getButtonSize();

        heightSeekBar.setProgress(savedHeight - 100); // Min 100
        sizeSeekBar.setProgress(savedSize - 20); // Min 20
        heightValue.setText(savedHeight + "dp");
        sizeValue.setText(savedSize + "dp");

        heightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int height = progress + 100;
                heightValue.setText(height + "dp");
                settingsManager.setKeyboardHeight(height);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int size = progress + 20;
                sizeValue.setText(size + "dp");
                settingsManager.setButtonSize(size);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportImportManager.exportAllSettings();
                Toast.makeText(SettingsActivity.this, "All settings exported successfully", Toast.LENGTH_SHORT).show();
            }
        });

        importBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportImportManager.importAllSettings();
                Toast.makeText(SettingsActivity.this, "All settings imported successfully", Toast.LENGTH_SHORT).show();

                // Update UI to reflect imported values
                int newHeight = settingsManager.getKeyboardHeight();
                int newSize = settingsManager.getButtonSize();

                heightSeekBar.setProgress(newHeight - 100);
                sizeSeekBar.setProgress(newSize - 20);
                heightValue.setText(newHeight + "dp");
                sizeValue.setText(newSize + "dp");
            }
        });
    }
}