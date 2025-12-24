package com.cboard;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.TypedValue;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

public class ThemeUtils {
    
    /**
     * Get the primary color from the current theme
     */
    @ColorInt
    public static int getPrimaryColor(@NonNull Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }
    
    /**
     * Get the primary dark color from the current theme
     */
    @ColorInt
    public static int getPrimaryDarkColor(@NonNull Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.data;
    }
    
    /**
     * Get the accent color from the current theme
     */
    @ColorInt
    public static int getAccentColor(@NonNull Context context) {
        TypedValue typedValue = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10+, use colorAccent
            context.getTheme().resolveAttribute(android.R.attr.colorAccent, typedValue, true);
        } else {
            // For older versions, try to get accent color
            int[] attrs = {android.R.attr.colorAccent};
            context.getTheme().resolveAttributes(attrs, typedValue, true);
        }
        return typedValue.data;
    }
    
    /**
     * Get the surface color from the current theme
     */
    @ColorInt
    public static int getSurfaceColor(@NonNull Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue, true);
        return typedValue.data;
    }
    
    /**
     * Get the on-surface color from the current theme
     */
    @ColorInt
    public static int getOnSurfaceColor(@NonNull Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        return typedValue.data;
    }
}