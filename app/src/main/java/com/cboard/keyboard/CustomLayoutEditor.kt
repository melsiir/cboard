package com.cboard.keyboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cboard.keyboard.data.KeyboardSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomLayoutEditor(
    settings: KeyboardSettings,
    onSettingsChange: (KeyboardSettings) -> Unit,
    onBack: () -> Unit
) {
    var rows by remember { mutableStateOf(settings.customLayout.toList().sortedBy { it.first }) }
    var newKeyText by remember { mutableStateOf("") }
    var selectedRow by remember { mutableStateOf(-1) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Custom Layout Editor",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Button(onClick = onBack) {
                Text("Save & Back")
            }
        }
        
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(rows) { (index, row) ->
                CustomRowEditor(
                    rowIndex = index,
                    rowKeys = row,
                    onRowChange = { updatedRow ->
                        val updatedRows = rows.toMutableList()
                        updatedRows.replaceAll { if (it.first == index) index to updatedRow else it }
                        rows = updatedRows
                        onSettingsChange(settings.copy(customLayout = rows.toMap()))
                    },
                    onDeleteRow = {
                        val updatedRows = rows.filter { it.first != index }.mapIndexed { i, pair -> 
                            if (i < index) pair else (i to pair.second) 
                        }
                        rows = updatedRows
                        onSettingsChange(settings.copy(
                            customLayout = rows.mapIndexed { i, pair -> i to pair.second }.toMap()
                        ))
                    }
                )
            }
        }
        
        // Add new row section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newKeyText,
                onValueChange = { newKeyText = it },
                label = { Text("New Key") },
                modifier = Modifier.weight(1f)
            )
            
            IconButton(
                onClick = {
                    if (newKeyText.isNotEmpty()) {
                        val newRow = (rows.maxByOrNull { it.first }?.first ?: -1) + 1
                        val updatedRows = rows.toMutableList().apply {
                            add(newRow to listOf(newKeyText))
                        }
                        rows = updatedRows
                        onSettingsChange(settings.copy(customLayout = updatedRows.toMap()))
                        newKeyText = ""
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Key")
            }
            
            IconButton(
                onClick = {
                    val newRow = (rows.maxByOrNull { it.first }?.first ?: -1) + 1
                    val updatedRows = rows.toMutableList().apply {
                        add(newRow to emptyList())
                    }
                    rows = updatedRows
                    onSettingsChange(settings.copy(customLayout = updatedRows.toMap()))
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Empty Row")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomRowEditor(
    rowIndex: Int,
    rowKeys: List<String>,
    onRowChange: (List<String>) -> Unit,
    onDeleteRow: () -> Unit
) {
    var keys by remember { mutableStateOf(rowKeys) }
    
    LaunchedEffect(rowKeys) {
        keys = rowKeys
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Row ${rowIndex + 1}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                IconButton(onClick = onDeleteRow) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Row")
                }
            }
            
            LazyRow {
                items(keys) { key ->
                    RowKeyItem(
                        key = key,
                        onKeyChange = { newKey ->
                            val updatedKeys = keys.toMutableList()
                            val index = updatedKeys.indexOf(key)
                            if (index != -1) {
                                updatedKeys[index] = newKey
                                keys = updatedKeys
                                onRowChange(updatedKeys)
                            }
                        },
                        onDelete = {
                            val updatedKeys = keys.toMutableList()
                            updatedKeys.remove(key)
                            keys = updatedKeys
                            onRowChange(updatedKeys)
                        }
                    )
                }
                
                // Add new key button at the end of the row
                item {
                    IconButton(onClick = {
                        val updatedKeys = keys.toMutableList()
                        updatedKeys.add("")
                        keys = updatedKeys
                        onRowChange(updatedKeys)
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Key")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowKeyItem(
    key: String,
    onKeyChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.padding(end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = key,
            onValueChange = onKeyChange,
            label = { Text("Key") },
            modifier = Modifier.width(80.dp)
        )

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Key", modifier = Modifier.size(16.dp))
        }
    }
}