// =============================================================================
// RuntimeScreen.kt - Node.js Runtime Control
// =============================================================================
// Provides UI for managing the embedded Node.js runtime.
// Users can start/stop opencode and kilo servers from here.
// =============================================================================

package com.kilo.companion.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kilo.companion.nodejs.NodeRuntime
import com.kilo.companion.nodejs.NodeService
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuntimeScreen() {
    val context = LocalContext.current
    val runtime = remember { NodeRuntime.getInstance() }
    val scope = rememberCoroutineScope()
    
    var isRunning by remember { mutableStateOf(false) }
    var activeTool by remember { mutableStateOf<String?>(null) }
    var messages by remember { mutableStateOf<List<String>>(emptyList()) }
    var showClearDialog by remember { mutableStateOf(false) }
    
    // Monitor runtime state
    LaunchedEffect(Unit) {
        while (true) {
            isRunning = runtime.isRunning.value
            messages = runtime.messages.value.takeLast(100) // Keep last 100 messages
            delay(500)
        }
    }
    
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear Messages") },
            text = { Text("Are you sure you want to clear all log messages?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        runtime.clearMessages()
                        showClearDialog = false
                    }
                ) { Text("Clear") }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Cancel") }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Runtime")
                        Text(
                            if (isRunning) "● $activeTool is running" else "○ Stopped",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isRunning) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    if (isRunning) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(Icons.Default.Refresh, "Clear logs")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isRunning) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isRunning) Icons.Default.Computer else Icons.Default.Terminal,
                            contentDescription = null,
                            tint = if (isRunning) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isRunning) "Node.js Active" else "Node.js Stopped",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = if (isRunning) 
                                    "$activeTool environment is running" 
                                else 
                                    "Start a tool to begin coding",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Control Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Start OpenCode
                Button(
                    onClick = {
                        scope.launch {
                            activeTool = "OpenCode"
                            val intent = Intent(context, NodeService::class.java).apply {
                                action = NodeService.ACTION_START
                                putExtra(NodeService.EXTRA_TOOL, "opencode")
                                putExtra(NodeService.EXTRA_PROJECT_PATH, "/data/data/com.kilo.companion/files/workspace")
                            }
                            context.startService(intent)
                        }
                    },
                    enabled = !isRunning,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("OpenCode")
                }
                
                // Start Kilo
                Button(
                    onClick = {
                        scope.launch {
                            activeTool = "Kilo"
                            val intent = Intent(context, NodeService::class.java).apply {
                                action = NodeService.ACTION_START
                                putExtra(NodeService.EXTRA_TOOL, "kilo")
                                putExtra(NodeService.EXTRA_PROJECT_PATH, "/data/data/com.kilo.companion/files/workspace")
                            }
                            context.startService(intent)
                        }
                    },
                    enabled = !isRunning,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PlayArrow, null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Kilo")
                }
                
                // Stop
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            val intent = Intent(context, NodeService::class.java).apply {
                                action = NodeService.ACTION_STOP
                            }
                            context.startService(intent)
                            activeTool = null
                        }
                    },
                    enabled = isRunning,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Stop, null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Stop")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Log Output
            Text(
                text = "Output Log",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    reverseLayout = true
                ) {
                    items(messages.reversed()) { message ->
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
            
            // Quick Actions
            if (isRunning) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* Navigate to WebView */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Open Web UI")
                    }
                    
                    OutlinedButton(
                        onClick = { /* Navigate to Terminal */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Open Terminal")
                    }
                }
            }
        }
    }
}
