// =============================================================================
// ConfigManagerScreen.kt - Configuration file editor
// =============================================================================
package com.kilo.companion.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.kilo.companion.data.SharedStorageManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigManagerScreen(storageManager: SharedStorageManager) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    var fileContent by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var hasChanges by remember { mutableStateOf(false) }
    var fileName by remember { mutableStateOf("opencode.json") }
    
    LaunchedEffect(Unit) {
        isLoading = true
        val content = storageManager.readOpencodeConfig()
        fileContent = content ?: createDefaultConfig()
        fileName = if (storageManager.fileExists(".config/kilo/opencode.jsonc")) "opencode.jsonc" else "opencode.json"
        isLoading = false
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Config: $fileName") },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            isLoading = true
                            val content = storageManager.readOpencodeConfig()
                            if (content != null) {
                                fileContent = content
                                hasChanges = false
                                Toast.makeText(context, "Refreshed", Toast.LENGTH_SHORT).show()
                            }
                            isLoading = false
                        }
                    }) { Icon(Icons.Default.Refresh, "Refresh") }
                    
                    IconButton(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                val success = storageManager.writeOpencodeConfig(fileContent)
                                isLoading = false
                                hasChanges = !success
                                Toast.makeText(context, if (success) "Saved!" else "Failed!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = hasChanges
                    ) { Icon(Icons.Default.Save, "Save") }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
            } else {
                BasicTextField(
                    value = fileContent,
                    onValueChange = { fileContent = it; hasChanges = true },
                    modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(16.dp),
                    textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

fun createDefaultConfig(): String = """{
  // OpenCode Configuration
  "model": { "provider": "openai", "name": "gpt-4" },
  "preferences": { "theme": "system" }
}"""
