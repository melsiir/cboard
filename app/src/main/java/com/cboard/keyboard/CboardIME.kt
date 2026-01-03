package com.cboard.keyboard

import android.content.Context
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
    private var isCapsLock by mutableStateOf(false)
    private var isShifted by mutableStateOf(false)
    private var isNumberMode by mutableStateOf(false)

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
            if (isNumberMode) {
                // Show number/symbol layout
                settings.customRows.forEach { row ->
                    ProgrammingSymbolsRow(row)
                }
            } else {
                // Render custom keyboard layout rows
                settings.customLayout.forEach { (_, row) ->
                    KeyboardRow(
                        keys = if (isCapsLock || isShifted) row.map { if (it.length == 1 && it[0].isLetter()) it.uppercase() else it } else row,
                        onKeyClick = { key -> handleKeyInput(key) }
                    )
                }

                // Custom symbol rows for programming
                settings.customRows.forEach { row ->
                    ProgrammingSymbolsRow(row)
                }
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
                label = if (isCapsLock) "CAPS" else "a/A",
                onClick = {
                    if (isNumberMode) {
                        // If in number mode, just exit number mode
                        isNumberMode = false
                    } else {
                        if (isShifted && !isCapsLock) {
                            // Double tap shift to lock caps
                            isCapsLock = true
                            isShifted = false
                        } else {
                            isShifted = !isShifted
                            if (isCapsLock) isCapsLock = false
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            )

            KeyButton(
                label = if (isNumberMode) "ABC" else "123",
                onClick = {
                    isNumberMode = !isNumberMode
                    // Reset shift when switching modes
                    if (!isCapsLock) isShifted = false
                },
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
                label = "Paste",
                onClick = {
                    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    val clipData = clipboardManager.primaryClip
                    if (clipData != null && clipData.itemCount > 0) {
                        val text = clipData.getItemAt(0).text
                        currentInputConnection?.commitText(text, 1)
                    }
                },
                modifier = Modifier.weight(1f)
            )

            KeyButton(
                label = "Enter",
                onClick = {
                    handleKeyInput("\n")
                    // Reset number mode after Enter for convenience
                    if (!isCapsLock) isNumberMode = false
                },
                modifier = Modifier.weight(1.5f)
            )
        }
    }

    private fun handleKeyInput(key: String) {
        val currentInputConnection = currentInputConnection
        when (key) {
            "\b" -> currentInputConnection.deleteSurroundingText(1, 0) // Backspace
            "\n" -> {
                currentInputConnection.commitText("\n", 1)
                // Reset shift after Enter
                if (!isCapsLock) isShifted = false
            }
            " " -> {
                currentInputConnection.commitText(" ", 1)
                // Reset shift after space (for typing convenience)
                if (!isCapsLock) isShifted = false
            }
            else -> {
                currentInputConnection.commitText(key, 1)
                // Reset shift after letter input (if not caps lock)
                if (!isCapsLock && key.length == 1 && key[0].isLetter()) {
                    isShifted = false
                }
            }
        }
    }
}