package com.cboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomizeLayoutActivity extends AppCompatActivity {
    
    private static final String PREFS_NAME = "CboardPrefs";
    private static final String KEY_KEYBOARD_LAYOUT = "keyboard_layout";
    
    private Spinner spinnerRowSelection;
    private LinearLayout rowKeysContainer;
    private EditText editTextNewChar;
    private Button btnAddChar, btnSaveLayout, btnResetLayout;
    
    // Default keyboard layout
    private List<List<String>> keyboardLayout;
    private List<String> rowNames;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize_layout);
        
        initViews();
        loadKeyboardLayout();
        setupSpinner();
        setupListeners();
        updateRowDisplay(0); // Show first row by default
    }
    
    private void initViews() {
        spinnerRowSelection = findViewById(R.id.spinnerRowSelection);
        rowKeysContainer = findViewById(R.id.rowKeysContainer);
        editTextNewChar = findViewById(R.id.editTextNewChar);
        btnAddChar = findViewById(R.id.btnAddChar);
        btnSaveLayout = findViewById(R.id.btnSaveLayout);
        btnResetLayout = findViewById(R.id.btnResetLayout);
        
        // Initialize common symbol buttons
        setupCommonSymbolButtons();
    }
    
    private void setupCommonSymbolButtons() {
        // Add click listeners to common symbol buttons
        setSymbolButtonListener(R.id.btnCharBraceOpen, "{");
        setSymbolButtonListener(R.id.btnCharBraceClose, "}");
        setSymbolButtonListener(R.id.btnCharBracketOpen, "[");
        setSymbolButtonListener(R.id.btnCharBracketClose, "]");
        setSymbolButtonListener(R.id.btnCharParenOpen, "(");
        setSymbolButtonListener(R.id.btnCharParenClose, ")");
        setSymbolButtonListener(R.id.btnCharAngleOpen, "<");
        setSymbolButtonListener(R.id.btnCharAngleClose, ">");
        setSymbolButtonListener(R.id.btnCharEqual, "=");
        setSymbolButtonListener(R.id.btnCharExclamation, "!");
        setSymbolButtonListener(R.id.btnCharAmpersand, "&");
        setSymbolButtonListener(R.id.btnCharPipe, "|");
        setSymbolButtonListener(R.id.btnCharStar, "*");
        setSymbolButtonListener(R.id.btnCharSlash, "/");
        setSymbolButtonListener(R.id.btnCharDash, "-");
        setSymbolButtonListener(R.id.btnCharPlus, "+");
        setSymbolButtonListener(R.id.btnCharPercent, "%");
        setSymbolButtonListener(R.id.btnCharCaret, "^");
        setSymbolButtonListener(R.id.btnCharDollar, "$");
        setSymbolButtonListener(R.id.btnCharHash, "#");
    }
    
    private void setSymbolButtonListener(int buttonId, String symbol) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            editTextNewChar.setText(symbol);
        });
    }
    
    private void loadKeyboardLayout() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String layoutString = prefs.getString(KEY_KEYBOARD_LAYOUT, null);
        
        if (layoutString != null) {
            // Parse the layout from stored string
            keyboardLayout = parseLayoutString(layoutString);
        } else {
            // Use default layout
            keyboardLayout = getDefaultLayout();
        }
        
        // Set up row names for the spinner
        rowNames = Arrays.asList(
            "Function Keys", 
            "Symbols Row 1", 
            "Symbols Row 2", 
            "Symbols Row 3", 
            "QWERTY Row 1", 
            "QWERTY Row 2", 
            "QWERTY Row 3", 
            "Bottom Row"
        );
    }
    
    private List<List<String>> getDefaultLayout() {
        return Arrays.asList(
            Arrays.asList("ESC", "TAB", "CTRL", "ALT", "SHIFT", "SPACE", "ALT GR", "FN", "META", "BKSP"),
            Arrays.asList("{", "}", "[", "]", "(", ")", "<", ">", "=", "!"),
            Arrays.asList("&", "|", "*", "/", "-", "+", "%", "^", "$", "#"),
            Arrays.asList("@", ";", ":", ".", ",", "_", "\\", "~", "`", "'"),
            Arrays.asList("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
            Arrays.asList("", "a", "s", "d", "f", "g", "h", "j", "k", "l", ""),
            Arrays.asList("SHIFT", "z", "x", "c", "v", "b", "n", "m", "DEL", ""),
            Arrays.asList("?123", ",", "SPACE", ".", "RETURN")
        );
    }
    
    private List<List<String>> parseLayoutString(String layoutString) {
        // This is a simplified implementation - in a real app, you'd need proper parsing
        // For now, return the default layout
        return getDefaultLayout();
    }
    
    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, rowNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRowSelection.setAdapter(adapter);
        
        spinnerRowSelection.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                updateRowDisplay(position);
            }
            
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }
    
    private void setupListeners() {
        btnAddChar.setOnClickListener(v -> {
            String newChar = editTextNewChar.getText().toString().trim();
            if (!newChar.isEmpty()) {
                int selectedRow = spinnerRowSelection.getSelectedItemPosition();
                addCharacterToRow(selectedRow, newChar);
                editTextNewChar.setText("");
            }
        });
        
        btnSaveLayout.setOnClickListener(v -> saveLayout());
        
        btnResetLayout.setOnClickListener(v -> {
            keyboardLayout = getDefaultLayout();
            updateRowDisplay(spinnerRowSelection.getSelectedItemPosition());
        });
    }
    
    private void updateRowDisplay(int rowIndex) {
        // Clear the current display
        rowKeysContainer.removeAllViews();
        
        if (rowIndex >= 0 && rowIndex < keyboardLayout.size()) {
            List<String> currentRow = keyboardLayout.get(rowIndex);
            
            for (int i = 0; i < currentRow.size(); i++) {
                String key = currentRow.get(i);
                
                // Skip empty placeholders
                if (key != null && !key.isEmpty()) {
                    Button keyButton = new Button(this);
                    keyButton.setText(key);
                    keyButton.setPadding(16, 16, 16, 16);
                    
                    // Set click listener to remove the key
                    final int position = i;
                    final int row = rowIndex;
                    keyButton.setOnClickListener(v -> removeCharacterFromRow(row, position));
                    
                    rowKeysContainer.addView(keyButton);
                }
            }
        }
    }
    
    private void addCharacterToRow(int rowIndex, String character) {
        if (rowIndex >= 0 && rowIndex < keyboardLayout.size()) {
            keyboardLayout.get(rowIndex).add(character);
            updateRowDisplay(rowIndex);
        }
    }
    
    private void removeCharacterFromRow(int rowIndex, int position) {
        if (rowIndex >= 0 && rowIndex < keyboardLayout.size()) {
            List<String> row = keyboardLayout.get(rowIndex);
            if (position >= 0 && position < row.size()) {
                row.remove(position);
                updateRowDisplay(rowIndex);
            }
        }
    }
    
    private void saveLayout() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        
        // Convert layout to string for storage (simplified)
        String layoutString = layoutToString(keyboardLayout);
        editor.putString(KEY_KEYBOARD_LAYOUT, layoutString);
        editor.apply();
        
        // Notify user
        android.widget.Toast.makeText(this, "Layout saved successfully!", android.widget.Toast.LENGTH_SHORT).show();
    }
    
    private String layoutToString(List<List<String>> layout) {
        // This is a simplified implementation - in a real app, you'd need proper serialization
        StringBuilder sb = new StringBuilder();
        for (List<String> row : layout) {
            for (int i = 0; i < row.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(row.get(i));
            }
            sb.append(";");
        }
        return sb.toString();
    }
}