package com.helix.app.core.common

import android.util.Log

object DebugLogger {
    private const val TAG = "HelixApp"

    fun d(message: String) {
        if (Config.ENABLE_DEBUG_LOGGING) {
            Log.d(TAG, message)
        }
    }

    fun e(message: String, throwable: Throwable? = null) {
        Log.e(TAG, message, throwable)
    }
}
