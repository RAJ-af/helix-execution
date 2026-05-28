package com.helix.app.core.common

object Config {
    // Backend URL - Default for Android Emulator to host
    const val BASE_URL = "http://10.0.2.2:8000/api/v1/"

    // Feature Toggles
    const val ENABLE_DEBUG_LOGGING = true
    const val STREAMING_ENABLED = true

    // API Keys (Placeholders - Should be in local.properties for real projects)
    const val TAVILY_API_KEY = ""
    const val ANTHROPIC_API_KEY = ""
}
