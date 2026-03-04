// =============================================================================
// NodeService.kt - Foreground Service for Node.js
// =============================================================================
// Manages Node.js as a foreground service to keep it running.
// This is critical for long-running AI coding sessions.
// =============================================================================

package com.kilo.companion.nodejs

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.kilo.companion.MainActivity
import com.kilo.companion.R
import kotlinx.coroutines.*

class NodeService : Service() {
    
    companion object {
        private const val TAG = "NodeService"
        private const val CHANNEL_ID = "codepal_node_channel"
        private const val NOTIFICATION_ID = 1
        
        const val ACTION_START = "com.kilo.companion.action.START_NODE"
        const val ACTION_STOP = "com.kilo.companion.action.STOP_NODE"
        const val EXTRA_PROJECT_PATH = "project_path"
        const val EXTRA_TOOL = "tool" // "opencode" or "kilo"
    }
    
    private val binder = NodeBinder()
    private val runtime = NodeRuntime.getInstance()
    private var serviceJob: Job? = null
    
    inner class NodeBinder : Binder() {
        fun getService(): NodeService = this@NodeService
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "NodeService created")
        runtime.initialize()
        createNotificationChannel()
    }
    
    override fun onBind(intent: Intent): IBinder {
        return binder
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val projectPath = intent.getStringExtra(EXTRA_PROJECT_PATH) ?: "/data/data/com.kilo.companion/files/workspace"
                val tool = intent.getStringExtra(EXTRA_TOOL) ?: "opencode"
                startNode(projectPath, tool)
            }
            ACTION_STOP -> {
                stopNode()
            }
        }
        
        return START_STICKY
    }
    
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "CodePal Node.js Runtime",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Keeps the AI coding environment running"
        }
        
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
    
    private fun createNotification(content: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val stopIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, NodeService::class.java).apply {
                action = ACTION_STOP
            },
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("CodePal AI Environment")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Stop", stopIntent)
            .setOngoing(true)
            .build()
    }
    
    private fun startNode(projectPath: String, tool: String) {
        Log.i(TAG, "Starting Node.js with $tool in $projectPath")
        
        startForeground(NOTIFICATION_ID, createNotification("Starting $tool..."))
        
        serviceJob = CoroutineScope(Dispatchers.IO).launch {
            val success = when (tool) {
                "opencode" -> runtime.startOpencode(projectPath)
                "kilo" -> runtime.startKilo(projectPath)
                else -> runtime.startOpencode(projectPath)
            }
            
            if (success) {
                updateNotification("$tool is running")
                monitorNodeProcess()
            } else {
                updateNotification("Failed to start $tool")
                stopSelf()
            }
        }
    }
    
    private suspend fun monitorNodeProcess() {
        while (isActive && runtime.isRunning.value) {
            delay(5000) // Check every 5 seconds
            
            // Update notification with status
            val msgCount = runtime.messages.value.size
            if (msgCount > 0) {
                updateNotification("Running • $msgCount messages")
            }
        }
        
        // Node.js stopped
        stopSelf()
    }
    
    private fun updateNotification(content: String) {
        val notification = createNotification(content)
        startForeground(NOTIFICATION_ID, notification)
    }
    
    private fun stopNode() {
        Log.i(TAG, "Stopping Node.js")
        runtime.stop()
        serviceJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "NodeService destroyed")
        runtime.cleanup()
    }
}
