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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.kilo.companion.data.SharedStorageManager
import kotlinx.coroutines.launch

enum class ConfigType {
    OPENCODE, KILO
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigManagerScreen(storageManager: SharedStorageManager) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    var selectedConfigType by remember { mutableStateOf(ConfigType.OPENCODE) }
    var fileContent by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var hasChanges by remember { mutableStateOf(false) }
    var fileName by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }
    
    fun getConfigPaths(): List<String> = when (selectedConfigType) {
        ConfigType.OPENCODE -> listOf(
            "~/${SharedStorageManager.HOME_CONFIG_OPENCODE}/${SharedStorageManager.FILE_OPENCODE_JSON}",
            "~/${SharedStorageManager.HOME_LOCAL_SHARE_OPENCODE}/${SharedStorageManager.FILE_OPENCODE_JSON}",
            "~/${SharedStorageManager.HOME_OPENCODE_DIR}/${SharedStorageManager.FILE_OPENCODE_JSON}"
        )
        ConfigType.KILO -> listOf(
            "~/${SharedStorageManager.HOME_CONFIG_KILO}/${SharedStorageManager.FILE_OPENCODE_JSON}",
            "~/${SharedStorageManager.HOME_LOCAL_SHARE_KILO}/${SharedStorageManager.FILE_OPENCODE_JSON}"
        )
    }
    
    fun loadConfig() {
        scope.launch {
            isLoading = true
            val content = when (selectedConfigType) {
                ConfigType.OPENCODE -> storageManager.readOpencodeConfig()
                ConfigType.KILO -> storageManager.readKiloConfig()
            }
            
            fileContent = content ?: ""
            fileName = when (selectedConfigType) {
                ConfigType.OPENCODE -> {
                    when {
                        storageManager.fileExistsInHome("${SharedStorageManager.HOME_CONFIG_OPENCODE}/${SharedStorageManager.FILE_OPENCODE_JSONC}") -> "opencode.jsonc"
                        storageManager.fileExistsInHome("${SharedStorageManager.HOME_LOCAL_SHARE_OPENCODE}/${SharedStorageManager.FILE_OPENCODE_JSONC}") -> "opencode.jsonc"
                        storageManager.fileExistsInHome("${SharedStorageManager.HOME_OPENCODE_DIR}/${SharedStorageManager.FILE_OPENCODE_JSONC}") -> "opencode.jsonc"
                        else -> "opencode.json"
                    }
                }
                ConfigType.KILO -> {
                    when {
                        storageManager.fileExistsInHome("${SharedStorageManager.HOME_CONFIG_KILO}/${SharedStorageManager.FILE_OPENCODE_JSONC}") -> "opencode.jsonc"
                        storageManager.fileExistsInHome("${SharedStorageManager.HOME_LOCAL_SHARE_KILO}/${SharedStorageManager.FILE_OPENCODE_JSONC}") -> "opencode.jsonc"
                        else -> "opencode.json"
                    }
                }
            }
            hasChanges = false
            isLoading = false
            
            if (content == null) {
                showCreateDialog = true
            }
        }
    }
    
    LaunchedEffect(selectedConfigType) {
        loadConfig()
    }
    
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Config Not Found") },
            text = { 
                Column {
                    Text("No ${selectedConfigType.name.lowercase().replaceFirstChar { it.uppercase() }} config file exists.")
                    Text("")
                    Text("Searched in:", style = MaterialTheme.typography.bodySmall)
                    getConfigPaths().forEach { path ->
                        Text(path, style = MaterialTheme.typography.bodySmall)
                    }
                    Text("")
                    Text("Create a default config file?")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            val success = when (selectedConfigType) {
                                ConfigType.OPENCODE -> storageManager.createDefaultOpencodeConfig()
                                ConfigType.KILO -> storageManager.createDefaultKiloConfig()
                            }
                            if (success) {
                                loadConfig()
                                Toast.makeText(context, "Default config created!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to create config", Toast.LENGTH_SHORT).show()
                            }
                            showCreateDialog = false
                        }
                    }
                ) { Text("Create Default") }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Config Editor")
                        Text(
                            fileName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // Config Type Selector
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        TextButton(onClick = { expanded = true }) {
                            Text(selectedConfigType.name)
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            ConfigType.entries.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.name) },
                                    onClick = {
                                        selectedConfigType = type
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    
                    // Create default config button
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, "Create Default")
                    }
                    
                    IconButton(onClick = { loadConfig() }) { 
                        Icon(Icons.Default.Refresh, "Refresh") 
                    }
                    
                    IconButton(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                val success = when (selectedConfigType) {
                                    ConfigType.OPENCODE -> storageManager.writeOpencodeConfig(fileContent)
                                    ConfigType.KILO -> storageManager.writeKiloConfig(fileContent)
                                }
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
            } else if (fileContent.isEmpty() && !showCreateDialog) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text("No config file found")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Searched in:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    getConfigPaths().forEach { path ->
                        Text(
                            path,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Create Default Config")
                    }
                }
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
