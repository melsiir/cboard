package com.cboard.keyboard.data

data class KeyboardSettings(
    var keyboardHeight: Int = 250,
    var buttonSize: Int = 50,
    var customRows: List<List<String>> = listOf(
        listOf("{", "}", "[", "]", "(", ")", ";", ":", ",", ".", "?", "!", "@", "#", "$", "%", "^", "&", "*", "|", "\\"),
        listOf("<", ">", "=", "+", "-", "*", "/", "\\", "|", "~", "`", "'", "\"", "_")
    ),
    var customLayout: Map<Int, List<String>> = mapOf(
        0 to listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "="),
        1 to listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p", "[", "]"),
        2 to listOf("a", "s", "d", "f", "g", "h", "j", "k", "l", ";", "'", "\\"),
        3 to listOf("z", "x", "c", "v", "b", "n", "m", ",", ".", "/")
    )
)