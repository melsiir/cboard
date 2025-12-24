package com.cboard.keyboard.layout;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import java.util.Map;
import java.util.HashMap;
import com.cboard.keyboard.R;
import com.cboard.keyboard.utils.KeyboardCustomizer;

public class LayoutEditorActivity extends Activity {
    
    private EditText layoutNameInput;
    private EditText rowInput;
    private Spinner rowSpinner;
    private KeyboardCustomizer customizer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.layout_editor);
        
        layoutNameInput = findViewById(R.id.layout_name_input);
        rowInput = findViewById(R.id.row_input);
        rowSpinner = findViewById(R.id.row_spinner);
        Button saveBtn = findViewById(R.id.save_btn);
        Button loadBtn = findViewById(R.id.load_btn);
        Button addRowBtn = findViewById(R.id.add_row_btn);
        Button removeRowBtn = findViewById(R.id.remove_row_btn);
        
        customizer = new KeyboardCustomizer(this);
        
        // Populate spinner with saved layouts
        updateLayoutSpinner();
        
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveLayout();
            }
        });
        
        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadLayout();
            }
        });
        
        addRowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRow();
            }
        });
        
        removeRowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeRow();
            }
        });
    }
    
    private void updateLayoutSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, 
            customizer.getSavedLayoutNames().toArray(new String[0]));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rowSpinner.setAdapter(adapter);
    }
    
    private void saveLayout() {
        String layoutName = layoutNameInput.getText().toString().trim();
        if (layoutName.isEmpty()) {
            Toast.makeText(this, "Please enter a layout name", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // For simplicity, we'll just save one row for now
        // In a real implementation, you'd have multiple rows
        String rowContent = rowInput.getText().toString();
        Map<Integer, String> layout = new HashMap<>();
        layout.put(0, rowContent); // Just one row for now
        
        customizer.saveCustomLayout(layoutName, layout);
        updateLayoutSpinner();
        
        Toast.makeText(this, "Layout saved successfully", Toast.LENGTH_SHORT).show();
    }
    
    private void loadLayout() {
        String layoutName = (String) rowSpinner.getSelectedItem();
        if (layoutName == null) {
            Toast.makeText(this, "Please select a layout to load", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Map<Integer, String> layout = customizer.loadCustomLayout(layoutName);
        if (layout.size() > 0) {
            rowInput.setText(layout.get(0)); // Just load the first row
        } else {
            rowInput.setText("");
        }
        
        layoutNameInput.setText(layoutName);
        
        Toast.makeText(this, "Layout loaded successfully", Toast.LENGTH_SHORT).show();
    }
    
    private void addRow() {
        // Placeholder for adding a new row
        Toast.makeText(this, "Add row functionality would be implemented here", Toast.LENGTH_SHORT).show();
    }
    
    private void removeRow() {
        // Placeholder for removing a row
        Toast.makeText(this, "Remove row functionality would be implemented here", Toast.LENGTH_SHORT).show();
    }
}