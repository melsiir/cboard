package com.cboard.keyboard.utils

import android.content.Context
import android.util.Log
import com.cboard.keyboard.data.KeyboardSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class SettingsManager(private val context: Context) {
    
    private val gson = Gson()
    private val settingsFile = File(context.filesDir, "cboard_settings.json")
    
    fun saveSettings(settings: KeyboardSettings) {
        try {
            val json = gson.toJson(settings)
            settingsFile.writeText(json)
        } catch (e: Exception) {
            Log.e("SettingsManager", "Error saving settings", e)
        }
    }
    
    fun loadSettings(): KeyboardSettings {
        return try {
            if (settingsFile.exists()) {
                val json = settingsFile.readText()
                val type = object : TypeToken<KeyboardSettings>() {}.type
                gson.fromJson(json, type) ?: KeyboardSettings()
            } else {
                KeyboardSettings()
            }
        } catch (e: Exception) {
            Log.e("SettingsManager", "Error loading settings", e)
            KeyboardSettings()
        }
    }
    
    fun exportSettings(): String {
        return try {
            if (settingsFile.exists()) {
                settingsFile.readText()
            } else {
                gson.toJson(KeyboardSettings())
            }
        } catch (e: Exception) {
            Log.e("SettingsManager", "Error exporting settings", e)
            gson.toJson(KeyboardSettings())
        }
    }
    
    fun importSettings(json: String): Boolean {
        return try {
            val settings = gson.fromJson(json, KeyboardSettings::class.java)
            saveSettings(settings)
            true
        } catch (e: Exception) {
            Log.e("SettingsManager", "Error importing settings", e)
            false
        }
    }
}