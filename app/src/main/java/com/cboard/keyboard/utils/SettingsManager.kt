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

    init {
        // Ensure the file's parent directory exists
        settingsFile.parentFile?.let { parentDir ->
            if (!parentDir.exists()) {
                parentDir.mkdirs()
            }
        }
    }
    
    fun saveSettings(settings: KeyboardSettings) {
        try {
            val json = gson.toJson(settings)
            // Write to a temporary file first, then rename to avoid corruption
            val tempFile = File(context.filesDir, "cboard_settings.json.tmp")
            tempFile.writeText(json)
            // Rename the temp file to the actual settings file
            if (settingsFile.exists()) {
                settingsFile.delete()
            }
            tempFile.renameTo(settingsFile)
        } catch (e: Exception) {
            Log.e("SettingsManager", "Error saving settings", e)
        }
    }

    fun loadSettings(): KeyboardSettings {
        return try {
            if (settingsFile.exists() && settingsFile.length() > 0) {
                val json = settingsFile.readText()
                if (json.isNotEmpty()) {
                    val type = object : TypeToken<KeyboardSettings>() {}.type
                    gson.fromJson(json, type) ?: KeyboardSettings()
                } else {
                    KeyboardSettings()
                }
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
            if (settingsFile.exists() && settingsFile.length() > 0) {
                val content = settingsFile.readText()
                if (content.isNotEmpty()) {
                    content
                } else {
                    gson.toJson(KeyboardSettings())
                }
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
            if (json.isNotEmpty()) {
                val settings = gson.fromJson(json, KeyboardSettings::class.java)
                saveSettings(settings)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("SettingsManager", "Error importing settings", e)
            false
        }
    }
}