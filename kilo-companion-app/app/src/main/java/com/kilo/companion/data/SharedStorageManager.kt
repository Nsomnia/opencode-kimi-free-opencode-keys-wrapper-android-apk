// =============================================================================
// SharedStorageManager.kt
// =============================================================================
// Manages file operations in the shared workspace directory.
// Handles permission requests and file I/O for configuration files.
// =============================================================================

package com.kilo.companion.data

import android.content.Context
import android.os.Environment
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class SharedStorageManager(private val context: Context) {
    
    companion object {
        private const val TAG = "SharedStorageManager"
        const val WORKSPACE_DIR_NAME = "KiloWorkspace"
        const val CONFIG_PATH = ".config/kilo"
        const val FILE_OPENCODE_JSON = "opencode.json"
        const val FILE_OPENCODE_JSONC = "opencode.jsonc"
        const val FILE_AUTH_JSON = "auth.json"
    }
    
    private fun getWorkspaceDirectory(): File? {
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            Log.e(TAG, "External storage not mounted")
            return null
        }
        
        val documentsDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOCUMENTS
        )
        val workspaceDir = File(documentsDir, WORKSPACE_DIR_NAME)
        
        if (!workspaceDir.exists()) {
            val created = workspaceDir.mkdirs()
            if (!created) {
                Log.e(TAG, "Failed to create workspace directory")
                return null
            }
            Log.i(TAG, "Created workspace directory: ${workspaceDir.absolutePath}")
        }
        
        return workspaceDir
    }
    
    suspend fun readFile(relativePath: String): String? = withContext(Dispatchers.IO) {
        try {
            val workspaceDir = getWorkspaceDirectory() ?: return@withContext null
            val file = File(workspaceDir, relativePath)
            
            if (!file.exists() || !file.canRead()) {
                return@withContext null
            }
            
            file.readText(Charsets.UTF_8)
        } catch (e: Exception) {
            Log.e(TAG, "Error reading file: $relativePath", e)
            null
        }
    }
    
    suspend fun writeFile(relativePath: String, content: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val workspaceDir = getWorkspaceDirectory() ?: return@withContext false
            val file = File(workspaceDir, relativePath)
            
            file.parentFile?.mkdirs()
            file.writeText(content, Charsets.UTF_8)
            
            Log.i(TAG, "Successfully wrote file: ${file.absolutePath}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error writing file: $relativePath", e)
            false
        }
    }
    
    fun fileExists(relativePath: String): Boolean {
        val workspaceDir = getWorkspaceDirectory() ?: return false
        return File(workspaceDir, relativePath).exists()
    }
    
    fun getWorkspacePath(): String? = getWorkspaceDirectory()?.absolutePath
    
    suspend fun readOpencodeConfig(): String? {
        return readFile("$CONFIG_PATH/$FILE_OPENCODE_JSONC") 
            ?: readFile("$CONFIG_PATH/$FILE_OPENCODE_JSON")
    }
    
    suspend fun writeOpencodeConfig(content: String): Boolean {
        return writeFile("$CONFIG_PATH/$FILE_OPENCODE_JSON", content)
    }
    
    suspend fun readAuthFile(): String? = readFile(FILE_AUTH_JSON)
    suspend fun writeAuthFile(content: String): Boolean = writeFile(FILE_AUTH_JSON, content)
}
