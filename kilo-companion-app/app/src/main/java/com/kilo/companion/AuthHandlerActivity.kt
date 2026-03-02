// =============================================================================
// AuthHandlerActivity.kt - OAuth callback interceptor
// =============================================================================
package com.kilo.companion

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.kilo.companion.data.SharedStorageManager
import com.kilo.companion.ui.theme.KiloCompanionTheme
import kotlinx.coroutines.launch
import org.json.JSONObject

class AuthHandlerActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val data: Uri? = intent?.data
        val authData = extractAuthData(data)
        val storageManager = SharedStorageManager(this)
        
        lifecycleScope.launch {
            val success = saveAuthData(storageManager, authData)
            
            setContent {
                KiloCompanionTheme {
                    AuthResultScreen(
                        success = success,
                        provider = authData["provider"] ?: "Unknown",
                        onDismiss = { finish() }
                    )
                }
            }
            
            Toast.makeText(
                this@AuthHandlerActivity,
                if (success) "Token Saved!" else "Failed to save token",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    
    private fun extractAuthData(uri: Uri?): Map<String, String> {
        val data = mutableMapOf<String, String>()
        if (uri == null) return data
        
        data["provider"] = when {
            uri.scheme == "opencode" -> "OpenCode"
            uri.scheme == "kilo" -> "Kilo"
            else -> uri.scheme ?: "Unknown"
        }
        
        uri.getQueryParameter("token")?.let { data["token"] = it }
        uri.getQueryParameter("access_token")?.let { data["access_token"] = it }
        uri.getQueryParameter("code")?.let { data["code"] = it }
        
        data["callback_url"] = uri.toString()
        return data
    }
    
    private suspend fun saveAuthData(storage: SharedStorageManager, data: Map<String, String>): Boolean {
        return try {
            val json = JSONObject()
            data.forEach { (k, v) -> json.put(k, v) }
            json.put("timestamp", System.currentTimeMillis())
            storage.writeAuthFile(json.toString(2))
        } catch (e: Exception) {
            false
        }
    }
}

@Composable
fun AuthResultScreen(success: Boolean, provider: String, onDismiss: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = if (success) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (success) "Authenticated!" else "Auth Failed",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Provider: $provider")
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onDismiss) { Text("Close") }
        }
    }
}
