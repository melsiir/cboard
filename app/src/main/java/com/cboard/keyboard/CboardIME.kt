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
    private var settings = KeyboardSettings()

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
        val isCapsLock = remember { mutableStateOf(false) }
        val isShifted = remember { mutableStateOf(false) }
        val isNumberMode = remember { mutableStateOf(false) }

        val keyboardHeight = remember { settings.keyboardHeight.dp }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(keyboardHeight)
        ) {
            if (isNumberMode.value) {
                // Show number/symbol layout
                settings.customRows.forEach { row ->
                    ProgrammingSymbolsRow(row, isCapsLock, isShifted, isNumberMode)
                }
            } else {
                // Render custom keyboard layout rows
                settings.customLayout.forEach { (_, row) ->
                    KeyboardRow(
                        keys = if (isCapsLock.value || isShifted.value) row.map { if (it.length == 1 && it[0].isLetter()) it.uppercase() else it } else row,
                        onKeyClick = { key ->
                            handleKeyInput(key, isCapsLock, isShifted, isNumberMode)
                            // Reset shift after letter input (if not caps lock)
                            if (!isCapsLock.value && key.length == 1 && key[0].isLetter()) {
                                isShifted.value = false
                            }
                        }
                    )
                }

                // Custom symbol rows for programming
                settings.customRows.forEach { row ->
                    ProgrammingSymbolsRow(row, isCapsLock, isShifted, isNumberMode)
                }
            }

            // Control row: Space, shift, backspace, enter
            ControlRow(
                isCapsLock = isCapsLock,
                isShifted = isShifted,
                isNumberMode = isNumberMode
            )
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
    fun ProgrammingSymbolsRow(
        keys: List<String>,
        isCapsLock: MutableState<Boolean>,
        isShifted: MutableState<Boolean>,
        isNumberMode: MutableState<Boolean>
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height((settings.buttonSize * 0.8).dp)
        ) {
            keys.forEach { symbol ->
                KeyButton(
                    label = symbol,
                    onClick = { handleKeyInput(symbol, isCapsLock, isShifted, isNumberMode) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    @Composable
    fun ControlRow(
        isCapsLock: MutableState<Boolean>,
        isShifted: MutableState<Boolean>,
        isNumberMode: MutableState<Boolean>
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(settings.buttonSize.dp)
        ) {
            KeyButton(
                label = if (isCapsLock.value) "CAPS" else "a/A",
                onClick = {
                    if (isNumberMode.value) {
                        // If in number mode, just exit number mode
                        isNumberMode.value = false
                    } else {
                        if (isShifted.value && !isCapsLock.value) {
                            // Double tap shift to lock caps
                            isCapsLock.value = true
                            isShifted.value = false
                        } else {
                            isShifted.value = !isShifted.value
                            if (isCapsLock.value) isCapsLock.value = false
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            )

            KeyButton(
                label = if (isNumberMode.value) "ABC" else "123",
                onClick = {
                    isNumberMode.value = !isNumberMode.value
                    // Reset shift when switching modes
                    if (!isCapsLock.value) isShifted.value = false
                },
                modifier = Modifier.weight(1f)
            )

            KeyButton(
                label = " ",
                onClick = { handleKeyInput(" ", isCapsLock, isShifted, isNumberMode) },
                modifier = Modifier.weight(3f)
            )

            KeyButton(
                label = "←",
                onClick = { handleKeyInput("\b", isCapsLock, isShifted, isNumberMode) },
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
                    handleKeyInput("\n", isCapsLock, isShifted, isNumberMode)
                    // Reset number mode after Enter for convenience
                    if (!isCapsLock.value) isNumberMode.value = false
                },
                modifier = Modifier.weight(1.5f)
            )
        }
    }

    private fun handleKeyInput(
        key: String,
        isCapsLock: MutableState<Boolean>,
        isShifted: MutableState<Boolean>,
        isNumberMode: MutableState<Boolean>
    ) {
        val currentInputConnection = currentInputConnection
        if (currentInputConnection == null) {
            // If there's no input connection, we can't send text
            return
        }

        when (key) {
            "\b" -> currentInputConnection.deleteSurroundingText(1, 0) // Backspace
            "\n" -> {
                currentInputConnection.commitText("\n", 1)
                // Reset shift after Enter
                if (!isCapsLock.value) isShifted.value = false
            }
            " " -> {
                currentInputConnection.commitText(" ", 1)
                // Reset shift after space (for typing convenience)
                if (!isCapsLock.value) isShifted.value = false
            }
            else -> {
                currentInputConnection.commitText(key, 1)
                // Reset shift after letter input (if not caps lock)
                if (!isCapsLock.value && key.length == 1 && key[0].isLetter()) {
                    isShifted.value = false
                }
            }
        }
    }
}