package com.cboard.keyboard

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cboard.keyboard.ui.theme.CboardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CboardTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "cboard - Custom Programming Keyboard",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "1. Enable the keyboard in Settings > System > Languages & Input > On-screen keyboard",
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "2. After enabling, return here and tap 'Switch to cboard Keyboard'",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("Open Keyboard Settings")
        }

        Button(
            onClick = {
                val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                intent.putExtra(Settings.EXTRA_INPUT_METHOD_ID, "${context.packageName}/.CboardIME")
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        ) {
            Text("Directly Open cboard Settings")
        }

        Button(
            onClick = {
                val imm = context.getSystemService(InputMethodManager::class.java)
                imm.showInputMethodPicker()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Switch to cboard Keyboard")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    CboardTheme {
        MainScreen()
    }
}