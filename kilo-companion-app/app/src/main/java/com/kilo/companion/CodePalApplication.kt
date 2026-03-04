// =============================================================================
// CodePalApplication.kt - Application class
// =============================================================================
package com.kilo.companion

import android.app.Application
import android.util.Log

class CodePalApplication : Application() {
    
    companion object {
        private const val TAG = "CodePal"
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "CodePal starting...")
    }
}
