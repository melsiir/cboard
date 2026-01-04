package com.cboard.keyboard

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import android.view.KeyEvent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.cboard.keyboard.data.KeyboardSettings
import com.cboard.keyboard.utils.SettingsManager

class CboardIME : InputMethodService(), LifecycleOwner, ViewModelStoreOwner {

    private lateinit var settingsManager: SettingsManager
    private var settings = KeyboardSettings()
    private val lifecycleRegistry = LifecycleRegistry(this)
    private var composeView: ComposeView? = null
    private val _viewModelStore = ViewModelStore()

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val viewModelStore: ViewModelStore
        get() = _viewModelStore


    override fun onCreate() {
        super.onCreate()
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        settingsManager = SettingsManager(this)
        settings = settingsManager.loadSettings()
    }


    override fun onDestroy() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        super.onDestroy()
    }

    override fun onCreateInputView(): View {
        val composeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
        }

        composeView.setContent {
            CboardKeyboard()
        }

        return composeView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        // Ensure the lifecycle is in the correct state when input view starts
        if (lifecycleRegistry.currentState == Lifecycle.State.CREATED) {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        // Update lifecycle state when input view finishes
        if (lifecycleRegistry.currentState == Lifecycle.State.STARTED) {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }
    }

    @Composable
    fun CboardKeyboard() {
        var isCapsLock by rememberSaveable { mutableStateOf(false) }
        var isShifted by rememberSaveable { mutableStateOf(false) }
        var isNumberMode by rememberSaveable { mutableStateOf(false) }

        val keyboardHeight = remember { (settings.keyboardHeight.takeIf { it > 0 } ?: 250).dp }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(keyboardHeight)
        ) {
            if (isNumberMode) {
                // Show number/symbol layout
                settings.customRows.forEach { row ->
                    ProgrammingSymbolsRow(row, isCapsLock, isShifted, isNumberMode) { key ->
                        handleKeyInput(key, isCapsLock, isShifted, isNumberMode) { newShifted ->
                            isShifted = newShifted
                        }
                    }
                }
            } else {
                // Render custom keyboard layout rows
                val layoutRows = settings.customLayout.toSortedMap()
                layoutRows.forEach { (_, row) ->
                    val processedRow = if (isCapsLock || isShifted) {
                        row.map { if (it.length == 1 && it[0].isLetter()) it.uppercase() else it }
                    } else {
                        row
                    }

                    KeyboardRow(
                        keys = processedRow,
                        onKeyClick = { key ->
                            handleKeyInput(key, isCapsLock, isShifted, isNumberMode) { newShifted ->
                                isShifted = newShifted
                            }
                            // Reset shift after letter input (if not caps lock)
                            if (!isCapsLock && key.length == 1 && key[0].isLetter()) {
                                isShifted = false
                            }
                        }
                    )
                }

                // Custom symbol rows for programming
                settings.customRows.forEach { row ->
                    ProgrammingSymbolsRow(row, isCapsLock, isShifted, isNumberMode) { key ->
                        handleKeyInput(key, isCapsLock, isShifted, isNumberMode) { newShifted ->
                            isShifted = newShifted
                        }
                    }
                }
            }

            // Control row: Space, shift, backspace, enter
            ControlRow(
                isCapsLock = isCapsLock,
                isShifted = isShifted,
                isNumberMode = isNumberMode,
                onCapsLockChange = { isCapsLock = it },
                onShiftChange = { isShifted = it },
                onNumberModeChange = { isNumberMode = it }
            )
        }
    }

    @Composable
    fun KeyboardRow(keys: List<String>, onKeyClick: (String) -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height((settings.buttonSize.takeIf { it > 0 } ?: 50).dp)
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
        isCapsLock: Boolean,
        isShifted: Boolean,
        isNumberMode: Boolean,
        onKeyClick: (String) -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(((settings.buttonSize.takeIf { it > 0 } ?: 50) * 0.8).dp)
        ) {
            keys.forEach { symbol ->
                KeyButton(
                    label = symbol,
                    onClick = { onKeyClick(symbol) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    @Composable
    fun ControlRow(
        isCapsLock: Boolean,
        isShifted: Boolean,
        isNumberMode: Boolean,
        onCapsLockChange: (Boolean) -> Unit,
        onShiftChange: (Boolean) -> Unit,
        onNumberModeChange: (Boolean) -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height((settings.buttonSize.takeIf { it > 0 } ?: 50).dp)
        ) {
            KeyButton(
                label = if (isCapsLock) "CAPS" else "a/A",
                onClick = {
                    if (isNumberMode) {
                        // If in number mode, just exit number mode
                        onNumberModeChange(false)
                    } else {
                        if (isShifted && !isCapsLock) {
                            // Double tap shift to lock caps
                            onCapsLockChange(true)
                            onShiftChange(false)
                        } else {
                            onShiftChange(!isShifted)
                            if (isCapsLock) onCapsLockChange(false)
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            )

            KeyButton(
                label = if (isNumberMode) "ABC" else "123",
                onClick = {
                    onNumberModeChange(!isNumberMode)
                    // Reset shift when switching modes
                    if (!isCapsLock) onShiftChange(false)
                },
                modifier = Modifier.weight(1f)
            )

            KeyButton(
                label = " ",
                onClick = {
                    handleKeyInput(" ", isCapsLock, isShifted, isNumberMode) { newShifted ->
                        onShiftChange(newShifted)
                    }
                },
                modifier = Modifier.weight(3f)
            )

            KeyButton(
                label = "←",
                onClick = {
                    handleKeyInput("\b", isCapsLock, isShifted, isNumberMode) { newShifted ->
                        onShiftChange(newShifted)
                    }
                },
                modifier = Modifier.weight(1f)
            )

            KeyButton(
                label = "Paste",
                onClick = {
                    try {
                        val inputConnection = currentInputConnection
                        if (inputConnection != null) {
                            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clipData = clipboardManager.primaryClip
                            if (clipData != null && clipData.itemCount > 0) {
                                val text = clipData.getItemAt(0).text
                                if (text != null) {
                                    inputConnection.commitText(text, 1)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // Handle potential errors when pasting
                        e.printStackTrace()
                    }
                },
                modifier = Modifier.weight(1f)
            )

            KeyButton(
                label = "Enter",
                onClick = {
                    handleKeyInput("\n", isCapsLock, isShifted, isNumberMode) { newShifted ->
                        onShiftChange(newShifted)
                    }
                    // Reset number mode after Enter for convenience
                    if (!isCapsLock) onNumberModeChange(false)
                },
                modifier = Modifier.weight(1.5f)
            )
        }
    }

    private fun handleKeyInput(
        key: String,
        isCapsLock: Boolean,
        isShifted: Boolean,
        isNumberMode: Boolean,
        onShiftChange: (Boolean) -> Unit = {}
    ) {
        val inputConnection = currentInputConnection
        if (inputConnection == null) {
            // If there's no input connection, we can't send text
            return
        }

        when (key) {
            "\b" -> {
                try {
                    inputConnection.deleteSurroundingText(1, 0) // Backspace
                } catch (e: Exception) {
                    // Handle potential errors when deleting text
                    e.printStackTrace()
                }
            }
            "\n" -> {
                try {
                    inputConnection.commitText("\n", 1)
                    // Reset shift after Enter
                    if (!isCapsLock) onShiftChange(false)
                } catch (e: Exception) {
                    // Handle potential errors when committing text
                    e.printStackTrace()
                }
            }
            " " -> {
                try {
                    inputConnection.commitText(" ", 1)
                    // Reset shift after space (for typing convenience)
                    if (!isCapsLock) onShiftChange(false)
                } catch (e: Exception) {
                    // Handle potential errors when committing text
                    e.printStackTrace()
                }
            }
            else -> {
                try {
                    inputConnection.commitText(key, 1)
                    // Reset shift after letter input (if not caps lock)
                    if (!isCapsLock && key.length == 1 && key[0].isLetter()) {
                        onShiftChange(false)
                    }
                } catch (e: Exception) {
                    // Handle potential errors when committing text
                    e.printStackTrace()
                }
            }
        }
    }
}