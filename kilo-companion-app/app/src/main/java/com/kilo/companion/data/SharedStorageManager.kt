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
        
        // Config paths in user's home directory
        const val HOME_OPENCODE_DIR = ".opencode"
        const val HOME_LOCAL_SHARE_OPENCODE = ".local/share/opencode"
        const val HOME_LOCAL_SHARE_KILO = ".local/share/kilo"
        const val HOME_CONFIG_OPENCODE = ".config/opencode"
        const val HOME_CONFIG_KILO = ".config/kilo"
        
        const val FILE_OPENCODE_JSON = "opencode.json"
        const val FILE_OPENCODE_JSONC = "opencode.jsonc"
        const val FILE_AUTH_JSON = "auth.json"
        
        // Default configs based on actual OpenCode schema
        // Note: Using single $ to avoid Kotlin string interpolation issues
        val DEFAULT_CONFIG_OPENCODE = """{
  "${'$'}schema": "https://opencode.ai/config.json",
  "model": {
    "provider": "openai",
    "name": "gpt-4o"
  },
  "preferences": {
    "theme": "system"
  }
}"""
        
        val DEFAULT_CONFIG_KILO = """{
  "${'$'}schema": "https://kilo.ai/config.json",
  "model": {
    "provider": "openai",
    "name": "gpt-4o"
  },
  "preferences": {
    "theme": "system"
  }
}"""
    }
    
    private fun getHomeDirectory(): File? {
        return File(System.getenv("HOME") ?: "/data/data/com.kilo.companion/files")
    }
    
    private fun ensureDirectory(file: File): Boolean {
        if (!file.exists()) {
            return file.mkdirs()
        }
        return true
    }
    
    suspend fun readFileFromHome(relativePath: String): String? = withContext(Dispatchers.IO) {
        try {
            val homeDir = getHomeDirectory() ?: return@withContext null
            val file = File(homeDir, relativePath)
            
            if (!file.exists() || !file.canRead()) {
                return@withContext null
            }
            
            file.readText(Charsets.UTF_8)
        } catch (e: Exception) {
            Log.e(TAG, "Error reading file: $relativePath", e)
            null
        }
    }
    
    suspend fun writeFileToHome(relativePath: String, content: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val homeDir = getHomeDirectory() ?: return@withContext false
            val file = File(homeDir, relativePath)
            
            file.parentFile?.let { ensureDirectory(it) }
            file.writeText(content, Charsets.UTF_8)
            
            Log.i(TAG, "Successfully wrote file: ${file.absolutePath}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error writing file: $relativePath", e)
            false
        }
    }
    
    fun fileExistsInHome(relativePath: String): Boolean {
        val homeDir = getHomeDirectory() ?: return false
        return File(homeDir, relativePath).exists()
    }
    
    // OpenCode config - checks multiple locations
    suspend fun readOpencodeConfig(): String? {
        // Check in order of priority
        return readFileFromHome("$HOME_CONFIG_OPENCODE/$FILE_OPENCODE_JSONC")
            ?: readFileFromHome("$HOME_CONFIG_OPENCODE/$FILE_OPENCODE_JSON")
            ?: readFileFromHome("$HOME_LOCAL_SHARE_OPENCODE/$FILE_OPENCODE_JSONC")
            ?: readFileFromHome("$HOME_LOCAL_SHARE_OPENCODE/$FILE_OPENCODE_JSON")
            ?: readFileFromHome("$HOME_OPENCODE_DIR/$FILE_OPENCODE_JSONC")
            ?: readFileFromHome("$HOME_OPENCODE_DIR/$FILE_OPENCODE_JSON")
    }
    
    suspend fun writeOpencodeConfig(content: String): Boolean {
        // Write to .config/opencode/ (preferred location)
        return writeFileToHome("$HOME_CONFIG_OPENCODE/$FILE_OPENCODE_JSON", content)
    }
    
    suspend fun createDefaultOpencodeConfig(): Boolean {
        return writeFileToHome("$HOME_CONFIG_OPENCODE/$FILE_OPENCODE_JSON", DEFAULT_CONFIG_OPENCODE)
    }
    
    // Kilo config - checks multiple locations
    suspend fun readKiloConfig(): String? {
        return readFileFromHome("$HOME_CONFIG_KILO/$FILE_OPENCODE_JSONC")
            ?: readFileFromHome("$HOME_CONFIG_KILO/$FILE_OPENCODE_JSON")
            ?: readFileFromHome("$HOME_LOCAL_SHARE_KILO/$FILE_OPENCODE_JSONC")
            ?: readFileFromHome("$HOME_LOCAL_SHARE_KILO/$FILE_OPENCODE_JSON")
    }
    
    suspend fun writeKiloConfig(content: String): Boolean {
        return writeFileToHome("$HOME_CONFIG_KILO/$FILE_OPENCODE_JSON", content)
    }
    
    suspend fun createDefaultKiloConfig(): Boolean {
        return writeFileToHome("$HOME_CONFIG_KILO/$FILE_OPENCODE_JSON", DEFAULT_CONFIG_KILO)
    }
    
    // Check if config exists
    fun opencodeConfigExists(): Boolean {
        return fileExistsInHome("$HOME_CONFIG_OPENCODE/$FILE_OPENCODE_JSONC")
            || fileExistsInHome("$HOME_CONFIG_OPENCODE/$FILE_OPENCODE_JSON")
            || fileExistsInHome("$HOME_LOCAL_SHARE_OPENCODE/$FILE_OPENCODE_JSONC")
            || fileExistsInHome("$HOME_LOCAL_SHARE_OPENCODE/$FILE_OPENCODE_JSON")
            || fileExistsInHome("$HOME_OPENCODE_DIR/$FILE_OPENCODE_JSONC")
            || fileExistsInHome("$HOME_OPENCODE_DIR/$FILE_OPENCODE_JSON")
    }
    
    fun kiloConfigExists(): Boolean {
        return fileExistsInHome("$HOME_CONFIG_KILO/$FILE_OPENCODE_JSONC")
            || fileExistsInHome("$HOME_CONFIG_KILO/$FILE_OPENCODE_JSON")
            || fileExistsInHome("$HOME_LOCAL_SHARE_KILO/$FILE_OPENCODE_JSONC")
            || fileExistsInHome("$HOME_LOCAL_SHARE_KILO/$FILE_OPENCODE_JSON")
    }
    
    suspend fun readAuthFile(): String? {
        return readFileFromHome("$HOME_LOCAL_SHARE_OPENCODE/$FILE_AUTH_JSON")
    }
    
    suspend fun writeAuthFile(content: String): Boolean {
        return writeFileToHome("$HOME_LOCAL_SHARE_OPENCODE/$FILE_AUTH_JSON", content)
    }
    
    /**
     * Get the home directory path
     * @return Path to home directory or null if not available
     */
    fun getHomeDirectoryPath(): String? {
        return getHomeDirectory()?.absolutePath
    }
    
    /**
     * Get the workspace path (alias for getHomeDirectoryPath)
     * @return Path to workspace or null if not available
     */
    fun getWorkspacePath(): String? {
        return getHomeDirectoryPath()
    }
}
