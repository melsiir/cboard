package com.cboard.keyboard

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.cboard.keyboard.data.KeyboardSettings
import com.cboard.keyboard.utils.SettingsManager

@Composable
fun SettingsScreen(
    onNavigateToCustomLayout: () -> Unit = {}
) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    var settings by remember { mutableStateOf(settingsManager.loadSettings()) }

    val scrollState = rememberScrollState()

    // Remember file pickers
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(settingsManager.exportSettings().toByteArray())
                }
                Toast.makeText(context, "Settings exported successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val jsonString = inputStream.bufferedReader().use { it.readText() }
                    if (settingsManager.importSettings(jsonString)) {
                        settings = settingsManager.loadSettings()
                        Toast.makeText(context, "Settings imported successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Import failed", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Import failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "cboard Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Keyboard height setting
        KeyboardHeightSetting(
            currentHeight = settings.keyboardHeight,
            onHeightChange = { height ->
                settings = settings.copy(keyboardHeight = height)
                settingsManager.saveSettings(settings)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Button size setting
        ButtonSizeSetting(
            currentSize = settings.buttonSize,
            onSizeChange = { size ->
                settings = settings.copy(buttonSize = size)
                settingsManager.saveSettings(settings)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Export/Import settings
        ExportImportSettings(
            onExport = { exportLauncher.launch("cboard_settings.json") },
            onImport = { importLauncher.launch("application/json") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Custom layout setting
        CustomLayoutSetting(onNavigateToCustomLayout)
    }
}

@Composable
fun KeyboardHeightSetting(currentHeight: Int, onHeightChange: (Int) -> Unit) {
    var height by remember { mutableStateOf(currentHeight.toFloat()) }

    LaunchedEffect(currentHeight) {
        height = currentHeight.toFloat()
    }

    Column {
        Text(
            text = "Keyboard Height: ${height.toInt()}dp",
            style = MaterialTheme.typography.titleMedium
        )

        Slider(
            value = height,
            onValueChange = { newValue ->
                height = newValue
                onHeightChange(newValue.toInt())
            },
            valueRange = 150f..400f,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun ButtonSizeSetting(currentSize: Int, onSizeChange: (Int) -> Unit) {
    var size by remember { mutableStateOf(currentSize.toFloat()) }

    LaunchedEffect(currentSize) {
        size = currentSize.toFloat()
    }

    Column {
        Text(
            text = "Button Size: ${size.toInt()}dp",
            style = MaterialTheme.typography.titleMedium
        )

        Slider(
            value = size,
            onValueChange = { newValue ->
                size = newValue
                onSizeChange(newValue.toInt())
            },
            valueRange = 30f..80f,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun ExportImportSettings(onExport: () -> Unit, onImport: () -> Unit) {
    Column {
        Text(
            text = "Settings Import/Export",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Button(
            onClick = onExport,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Export Settings")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onImport,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Import Settings")
        }
    }
}

@Composable
fun CustomLayoutSetting(onNavigateToCustomLayout: () -> Unit) {
    Column {
        Text(
            text = "Custom Layout",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Button(
            onClick = onNavigateToCustomLayout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edit Custom Layout")
        }
    }
}