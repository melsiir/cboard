package com.cboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ThemedKeyboardView extends LinearLayout {
    
    private Button[][] keyButtons;
    private String[][] keyLabels;
    private SharedPreferences prefs;
    private OnKeyClickListener keyClickListener;

    public interface OnKeyClickListener {
        void onKeyPressed(String key);
    }

    public ThemedKeyboardView(Context context) {
        super(context);
        init();
    }
    
    public ThemedKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public ThemedKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.keyboard_layout, this, true);
        prefs = getContext().getSharedPreferences("CboardPrefs", Context.MODE_PRIVATE);
        
        initializeKeyboard();
        setupKeyListeners();
        applyTheme();
    }
    
    private void initializeKeyboard() {
        // Define the keyboard layout
        keyLabels = new String[][]{
            // Function row
            {"ESC", "TAB", "CTRL", "ALT", "SHIFT", "SPACE", "ALT GR", "FN", "META", "BKSP"},
            // First symbol row
            {"{", "}", "[", "]", "(", ")", "<", ">", "=", "!"},
            // Second symbol row
            {"&", "|", "*", "/", "-", "+", "%", "^", "$", "#"},
            // Third symbol row
            {"@", ";", ":", ".", ",", "_", "\\", "~", "`", "'"},
            // QWERTY row 1
            {"q", "w", "e", "r", "t", "y", "u", "i", "o", "p"},
            // QWERTY row 2
            {"", "a", "s", "d", "f", "g", "h", "j", "k", "l", ""},
            // QWERTY row 3
            {"SHIFT", "z", "x", "c", "v", "b", "n", "m", "DEL", ""},
            // Bottom row
            {"?123", ",", "SPACE", ".", "RETURN"}
        };
        
        // Create button references for each row
        keyButtons = new Button[][]{
            // Row 0: Function keys
            {
                findViewById(R.id.key_escape),
                findViewById(R.id.key_tab),
                findViewById(R.id.key_ctrl),
                findViewById(R.id.key_alt),
                findViewById(R.id.key_shift),
                findViewById(R.id.key_space),
                findViewById(R.id.key_alt_gr),
                findViewById(R.id.key_fn),
                findViewById(R.id.key_meta),
                findViewById(R.id.key_backspace)
            },
            // Row 1: First symbol row
            {
                findViewById(R.id.key_brace_open),
                findViewById(R.id.key_brace_close),
                findViewById(R.id.key_bracket_open),
                findViewById(R.id.key_bracket_close),
                findViewById(R.id.key_paren_open),
                findViewById(R.id.key_paren_close),
                findViewById(R.id.key_angle_open),
                findViewById(R.id.key_angle_close),
                findViewById(R.id.key_equal),
                findViewById(R.id.key_exclamation)
            },
            // Row 2: Second symbol row
            {
                findViewById(R.id.key_ampersand),
                findViewById(R.id.key_pipe),
                findViewById(R.id.key_star),
                findViewById(R.id.key_slash),
                findViewById(R.id.key_dash),
                findViewById(R.id.key_plus),
                findViewById(R.id.key_percent),
                findViewById(R.id.key_caret),
                findViewById(R.id.key_dollar),
                findViewById(R.id.key_hash)
            },
            // Row 3: Third symbol row
            {
                findViewById(R.id.key_at),
                findViewById(R.id.key_semicolon),
                findViewById(R.id.key_colon),
                findViewById(R.id.key_dot),
                findViewById(R.id.key_comma),
                findViewById(R.id.key_underscore),
                findViewById(R.id.key_backslash),
                findViewById(R.id.key_tilde),
                findViewById(R.id.key_tick),
                findViewById(R.id.key_quote)
            },
            // Row 4: QWERTY row 1
            {
                findViewById(R.id.key_q),
                findViewById(R.id.key_w),
                findViewById(R.id.key_e),
                findViewById(R.id.key_r),
                findViewById(R.id.key_t),
                findViewById(R.id.key_y),
                findViewById(R.id.key_u),
                findViewById(R.id.key_i),
                findViewById(R.id.key_o),
                findViewById(R.id.key_p)
            },
            // Row 5: QWERTY row 2
            {
                null, // Placeholder for spacing
                findViewById(R.id.key_a),
                findViewById(R.id.key_s),
                findViewById(R.id.key_d),
                findViewById(R.id.key_f),
                findViewById(R.id.key_g),
                findViewById(R.id.key_h),
                findViewById(R.id.key_j),
                findViewById(R.id.key_k),
                findViewById(R.id.key_l),
                null // Placeholder for spacing
            },
            // Row 6: QWERTY row 3
            {
                findViewById(R.id.key_shift2),
                findViewById(R.id.key_z),
                findViewById(R.id.key_x),
                findViewById(R.id.key_c),
                findViewById(R.id.key_v),
                findViewById(R.id.key_b),
                findViewById(R.id.key_n),
                findViewById(R.id.key_m),
                findViewById(R.id.key_delete),
                null // Placeholder for spacing
            },
            // Row 7: Bottom row
            {
                findViewById(R.id.key_symbol_switch),
                findViewById(R.id.key_comma_bottom),
                findViewById(R.id.key_space_bottom),
                findViewById(R.id.key_period),
                findViewById(R.id.key_return)
            }
        };
    }
    
    private void setupKeyListeners() {
        // Set up listeners for all the buttons
        for (int i = 0; i < keyButtons.length; i++) {
            for (int j = 0; j < keyButtons[i].length; j++) {
                Button button = keyButtons[i][j];
                if (button != null) {
                    final String keyLabel = keyLabels[i][j];
                    button.setOnClickListener(v -> {
                        if (keyClickListener != null) {
                            keyClickListener.onKeyPressed(keyLabel);
                        }
                    });
                }
            }
        }
    }
    
    private void applyTheme() {
        // Get theme colors
        int surfaceColor = ThemeUtils.getSurfaceColor(getContext());
        int onSurfaceColor = ThemeUtils.getOnSurfaceColor(getContext());
        int accentColor = ThemeUtils.getAccentColor(getContext());
        
        // Apply colors to the keyboard background
        setBackgroundColor(surfaceColor);
        
        // Apply colors to all buttons
        for (Button[] row : keyButtons) {
            for (Button button : row) {
                if (button != null) {
                    // Set text color
                    button.setTextColor(onSurfaceColor);
                    
                    // Set background color with ripple effect
                    TypedValue outValue = new TypedValue();
                    getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                    button.setBackgroundResource(outValue.resourceId);
                    
                    // Set background color to surface color
                    button.setBackgroundColor(surfaceColor);
                    
                    // Adjust text size based on settings
                    int buttonSize = prefs.getInt("button_size", 1); // Default to medium
                    float textSize;
                    switch (buttonSize) {
                        case 0: // Small
                            textSize = 12f;
                            break;
                        case 2: // Large
                            textSize = 18f;
                            break;
                        case 1: // Medium (default)
                        default:
                            textSize = 14f;
                            break;
                    }
                    button.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                }
            }
        }
    }
    
    public void updateTheme() {
        applyTheme();
    }
    
    public void setOnKeyClickListener(OnKeyClickListener listener) {
        this.keyClickListener = listener;
    }
}