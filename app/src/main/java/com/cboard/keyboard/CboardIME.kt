package com.cboard.keyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import android.view.KeyEvent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.cboard.keyboard.data.KeyboardSettings
import com.cboard.keyboard.utils.SettingsManager

class CboardIME : InputMethodService() {

    private lateinit var settingsManager: SettingsManager
    private var settings by mutableStateOf(KeyboardSettings())

    override fun onCreate() {
        super.onCreate()
        settingsManager = SettingsManager(this)
        settings = settingsManager.loadSettings()
    }

    override fun onCreateInputView(): View {
        return ComposeView(this).apply {
            setContent {
                CboardKeyboard()
            }
        }
    }

    @Composable
    fun CboardKeyboard() {
        val keyboardHeight = remember { settings.keyboardHeight.dp }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(keyboardHeight)
        ) {
            // Render custom keyboard layout rows
            settings.customLayout.forEach { (_, row) ->
                KeyboardRow(
                    keys = row,
                    onKeyClick = { key -> handleKeyInput(key) }
                )
            }

            // Custom symbol rows for programming
            settings.customRows.forEach { row ->
                ProgrammingSymbolsRow(row)
            }

            // Control row: Space, shift, backspace, enter
            ControlRow()
        }
    }

    @Composable
    fun KeyboardRow(keys: List<String>, onKeyClick: (String) -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(settings.buttonSize.dp)
        ) {
            keys.forEach { key ->
                KeyButton(
                    label = key,
                    onClick = { onKeyClick(key) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    @Composable
    fun KeyButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
        Button(
            onClick = onClick,
            modifier = modifier,
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }

    @Composable
    fun ProgrammingSymbolsRow(keys: List<String>) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height((settings.buttonSize * 0.8).dp)
        ) {
            keys.forEach { symbol ->
                KeyButton(
                    label = symbol,
                    onClick = { handleKeyInput(symbol) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    @Composable
    fun ControlRow() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(settings.buttonSize.dp)
        ) {
            KeyButton(
                label = "a/A",
                onClick = { /* Handle shift */ },
                modifier = Modifier.weight(1f)
            )

            KeyButton(
                label = "ABC",
                onClick = { /* Switch to letters */ },
                modifier = Modifier.weight(1f)
            )

            KeyButton(
                label = " ",
                onClick = { handleKeyInput(" ") },
                modifier = Modifier.weight(3f)
            )

            KeyButton(
                label = "←",
                onClick = { handleKeyInput("\b") },
                modifier = Modifier.weight(1f)
            )

            KeyButton(
                label = "Enter",
                onClick = { handleKeyInput("\n") },
                modifier = Modifier.weight(1.5f)
            )
        }
    }

    private fun handleKeyInput(key: String) {
        val currentInputConnection = currentInputConnection
        when (key) {
            "\b" -> currentInputConnection.deleteSurroundingText(1, 0) // Backspace
            "\n" -> currentInputConnection.commitText("\n", 1)
            " " -> currentInputConnection.commitText(" ", 1)
            else -> currentInputConnection.commitText(key, 1)
        }
    }
}