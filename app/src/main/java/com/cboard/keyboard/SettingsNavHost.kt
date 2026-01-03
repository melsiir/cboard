package com.cboard.keyboard

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cboard.keyboard.data.KeyboardSettings
import com.cboard.keyboard.utils.SettingsManager

@Composable
fun SettingsNavHost() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "settings"
    ) {
        composable("settings") {
            SettingsScreen(
                onNavigateToCustomLayout = {
                    navController.navigate("custom_layout")
                }
            )
        }
        
        composable("custom_layout") {
            val context = LocalContext.current
            val settingsManager = remember { SettingsManager(context) }
            val initialSettings = remember { settingsManager.loadSettings() }
            val settingsState = rememberSaveable {
                mutableStateOf(initialSettings)
            }

            CustomLayoutEditor(
                settings = settingsState.value,
                onSettingsChange = { newSettings ->
                    settingsState.value = newSettings
                    settingsManager.saveSettings(newSettings)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}