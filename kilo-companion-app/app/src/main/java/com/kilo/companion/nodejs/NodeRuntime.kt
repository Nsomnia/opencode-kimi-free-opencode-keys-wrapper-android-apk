// =============================================================================
// NodeRuntime.kt - Node.js Runtime Manager
// =============================================================================
// Manages the lifecycle of the embedded Node.js runtime.
// Provides a clean interface for starting, stopping, and communicating with Node.js.
// =============================================================================

package com.kilo.companion.nodejs

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue

class NodeRuntime private constructor() {
    
    companion object {
        private const val TAG = "NodeRuntime"
        
        @Volatile
        private var instance: NodeRuntime? = null
        
        fun getInstance(): NodeRuntime {
            return instance ?: synchronized(this) {
                instance ?: NodeRuntime().also { instance = it }
            }
        }
    }
    
    // State
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning
    
    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages: StateFlow<List<String>> = _messages
    
    private val messageQueue = ConcurrentLinkedQueue<String>()
    private var messageJob: Job? = null
    
    // Native methods
    private external fun nativeInit(callback: NodeMessageCallback)
    private external fun nativeStartNode(projectPath: String, scriptPath: String): Boolean
    private external fun nativeStopNode()
    private external fun nativeIsRunning(): Boolean
    private external fun nativeCleanup()
    
    init {
        System.loadLibrary("codepal-node")
    }
    
    /**
     * Initialize the runtime
     */
    fun initialize() {
        nativeInit(object : NodeMessageCallback {
            override fun onNodeMessage(message: String) {
                messageQueue.offer(message)
            }
        })
        
        // Start message collection coroutine
        messageJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val newMessages = mutableListOf<String>()
                while (messageQueue.isNotEmpty()) {
                    messageQueue.poll()?.let { newMessages.add(it) }
                }
                
                if (newMessages.isNotEmpty()) {
                    _messages.value = _messages.value + newMessages
                    
                    // Check for status messages
                    newMessages.forEach { msg ->
                        when (msg) {
                            "stopped" -> _isRunning.value = false
                            else -> Log.d(TAG, "Node message: $msg")
                        }
                    }
                }
                
                delay(100)
            }
        }
    }
    
    /**
     * Start Node.js with a specific script
     * @param projectPath Path to the Node.js project directory
     * @param scriptPath Path to the main script to run
     */
    fun start(projectPath: String, scriptPath: String): Boolean {
        if (_isRunning.value) {
            Log.w(TAG, "Node.js is already running")
            return false
        }
        
        // Verify paths exist
        if (!File(projectPath).exists()) {
            Log.e(TAG, "Project path does not exist: $projectPath")
            return false
        }
        
        if (!File(scriptPath).exists()) {
            Log.e(TAG, "Script path does not exist: $scriptPath")
            return false
        }
        
        val success = nativeStartNode(projectPath, scriptPath)
        if (success) {
            _isRunning.value = true
            Log.i(TAG, "Node.js started successfully")
        } else {
            Log.e(TAG, "Failed to start Node.js")
        }
        
        return success
    }
    
    /**
     * Start opencode CLI in server mode
     */
    fun startOpencode(projectPath: String): Boolean {
        val opencodePath = "/data/data/com.kilo.companion/files/opencode/bin/opencode.js"
        return start(projectPath, opencodePath)
    }
    
    /**
     * Start kilo CLI in server mode
     */
    fun startKilo(projectPath: String): Boolean {
        val kiloPath = "/data/data/com.kilo.companion/files/kilo/bin/kilo.js"
        return start(projectPath, kiloPath)
    }
    
    /**
     * Stop Node.js
     */
    fun stop() {
        Log.i(TAG, "Stopping Node.js...")
        nativeStopNode()
        _isRunning.value = false
    }
    
    /**
     * Clean up resources
     */
    fun cleanup() {
        stop()
        messageJob?.cancel()
        nativeCleanup()
    }
    
    /**
     * Clear message history
     */
    fun clearMessages() {
        _messages.value = emptyList()
    }
}

/**
 * Callback interface for Node.js messages
 */
interface NodeMessageCallback {
    fun onNodeMessage(message: String)
}
