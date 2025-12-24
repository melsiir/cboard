package com.cboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class KeyboardView extends LinearLayout {
    
    public KeyboardView(Context context) {
        super(context);
        init();
    }
    
    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public KeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.keyboard_layout, this, true);
    }
}