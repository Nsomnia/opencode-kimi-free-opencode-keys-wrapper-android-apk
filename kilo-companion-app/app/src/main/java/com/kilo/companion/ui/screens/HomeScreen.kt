// =============================================================================
// HomeScreen.kt - Dashboard with workspace status
// =============================================================================
package com.kilo.companion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kilo.companion.data.SharedStorageManager
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(storageManager: SharedStorageManager) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    
    var workspacePath by remember { mutableStateOf<String?>(null) }
    var configExists by remember { mutableStateOf(false) }
    var authExists by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        workspacePath = storageManager.getWorkspacePath()
        configExists = storageManager.readOpencodeConfig() != null
        authExists = storageManager.readAuthFile() != null
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Kilo Companion",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "Configuration Dashboard",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        StatusCard(workspacePath, configExists, authExists) {
            scope.launch {
                workspacePath = storageManager.getWorkspacePath()
                configExists = storageManager.readOpencodeConfig() != null
                authExists = storageManager.readAuthFile() != null
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        InstructionsCard()
    }
}

@Composable
fun StatusCard(
    workspacePath: String?,
    configExists: Boolean,
    authExists: Boolean,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Workspace Status",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Folder, null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = workspacePath ?: "Not available",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            StatusRow("Config file", configExists)
            StatusRow("Auth file", authExists)
            
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRefresh, modifier = Modifier.align(Alignment.End)) {
                Text("Refresh")
            }
        }
    }
}

@Composable
fun StatusRow(label: String, exists: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (exists) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = if (exists) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "$label: ${if (exists) "Found" else "Not found"}")
    }
}

@Composable
fun InstructionsCard() {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Getting Started",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("1. Config files go in: Documents/KiloWorkspace/.config/kilo/")
            Text("2. Use Config tab to edit files")
            Text("3. OAuth flows are auto-intercepted")
        }
    }
}
