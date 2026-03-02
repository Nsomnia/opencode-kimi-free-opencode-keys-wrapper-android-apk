// =============================================================================
// KiloCompanionApplication.kt - Application class
// =============================================================================
package com.kilo.companion

import android.app.Application
import android.util.Log

class KiloCompanionApplication : Application() {
    
    companion object {
        private const val TAG = "KiloCompanion"
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Kilo Companion starting...")
    }
}
