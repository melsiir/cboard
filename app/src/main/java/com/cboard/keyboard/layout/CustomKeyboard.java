package com.cboard.keyboard.layout;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import java.util.List;
import java.util.ArrayList;

public class CustomKeyboard extends Keyboard {
    
    public CustomKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }
    
    public CustomKeyboard(Context context, int layoutTemplateResId, 
                         CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
    }
    
    /**
     * Adds a new key to a specific row
     */
    public void addKeyToRow(int rowIndex, int x, int y, int width, int height, 
                           int code, String label, String popupCharacters) {
        // Create a new key
        Key newKey = new Key();
        newKey.x = x;
        newKey.y = y;
        newKey.width = width;
        newKey.height = height;
        newKey.label = label;
        newKey.popupCharacters = popupCharacters != null ? popupCharacters.toCharArray() : null;
        
        // Convert code to character codes
        if (code != 0) {
            newKey.codes = new int[] {code};
        } else {
            // If code is 0, try to get from label
            if (label != null && label.length() > 0) {
                newKey.codes = new int[] {label.charAt(0)};
            } else {
                newKey.codes = new int[] {0};
            }
        }
        
        // Add to the appropriate row
        if (rowIndex < getRows().size()) {
            getRows().get(rowIndex).mKeys.add(newKey);
        } else {
            // If row doesn't exist, create it
            Keyboard.Row newRow = new Keyboard.Row(this);
            newRow.defaultHeight = height;
            newRow.y = y;
            newRow.mKeys = new ArrayList<>();
            newRow.mKeys.add(newKey);
            getRows().add(newRow);
        }
    }
    
    /**
     * Removes a key from a specific row
     */
    public boolean removeKeyFromRow(int rowIndex, int code) {
        if (rowIndex < getRows().size()) {
            List<Key> keys = getRows().get(rowIndex).mKeys;
            for (int i = 0; i < keys.size(); i++) {
                if (keys.get(i).codes[0] == code) {
                    keys.remove(i);
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Swaps two keys in the keyboard
     */
    public boolean swapKeys(int code1, int code2) {
        Key key1 = null, key2 = null;
        Keyboard.Row row1 = null, row2 = null;
        int index1 = -1, index2 = -1;
        
        // Find both keys
        for (int r = 0; r < getRows().size(); r++) {
            Keyboard.Row row = getRows().get(r);
            for (int k = 0; k < row.mKeys.size(); k++) {
                Key key = row.mKeys.get(k);
                if (key1 == null && key.codes[0] == code1) {
                    key1 = key;
                    row1 = row;
                    index1 = k;
                } else if (key2 == null && key.codes[0] == code2) {
                    key2 = key;
                    row2 = row;
                    index2 = k;
                }
            }
        }
        
        // Swap if both keys were found
        if (key1 != null && key2 != null) {
            // Swap positions
            int tempX = key1.x;
            int tempY = key1.y;
            key1.x = key2.x;
            key1.y = key2.y;
            key2.x = tempX;
            key2.y = tempY;
            
            // Swap in their respective rows
            row1.mKeys.set(index1, key2);
            row2.mKeys.set(index2, key1);
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Gets the rows of the keyboard (for modification)
     */
    private List<Keyboard.Row> getRows() {
        // Use reflection to access the mRows field since it's not publicly accessible
        try {
            java.lang.reflect.Field rowsField = Keyboard.class.getDeclaredField("mRows");
            rowsField.setAccessible(true);
            return (List<Keyboard.Row>) rowsField.get(this);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}