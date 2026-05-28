package com.helix.app.core.data.local.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "app_settings")

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val PREFERRED_PROVIDER = stringPreferencesKey("preferred_provider")
    private val PREFERRED_MODEL = stringPreferencesKey("preferred_model")

    val preferredProvider: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PREFERRED_PROVIDER] ?: "claude"
    }

    suspend fun saveProvider(provider: String) {
        context.dataStore.edit { preferences ->
            preferences[PREFERRED_PROVIDER] = provider
        }
    }
}
