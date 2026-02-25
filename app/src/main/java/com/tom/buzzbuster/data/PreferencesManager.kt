package com.tom.buzzbuster.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "buzzbuster_prefs")

class PreferencesManager(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val THEME_MODE = stringPreferencesKey("theme_mode") // "dark", "light", "system"
        val HISTORY_LIMIT = intPreferencesKey("history_limit")
        val IS_INTERCEPTOR_ENABLED = booleanPreferencesKey("interceptor_enabled")
        val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
        val AUTO_WIPE_ENABLED = booleanPreferencesKey("auto_wipe_enabled")

        const val DEFAULT_HISTORY_LIMIT = 500
        const val DEFAULT_THEME = "dark"
    }

    val themeMode: Flow<String> = dataStore.data.map { prefs ->
        prefs[THEME_MODE] ?: DEFAULT_THEME
    }

    val historyLimit: Flow<Int> = dataStore.data.map { prefs ->
        prefs[HISTORY_LIMIT] ?: DEFAULT_HISTORY_LIMIT
    }

    val isInterceptorEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[IS_INTERCEPTOR_ENABLED] ?: true
    }

    val geminiApiKey: Flow<String> = dataStore.data.map { prefs ->
        prefs[GEMINI_API_KEY] ?: ""
    }

    val autoWipeEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[AUTO_WIPE_ENABLED] ?: false
    }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { it[THEME_MODE] = mode }
    }

    suspend fun setHistoryLimit(limit: Int) {
        dataStore.edit { it[HISTORY_LIMIT] = limit }
    }

    suspend fun setInterceptorEnabled(enabled: Boolean) {
        dataStore.edit { it[IS_INTERCEPTOR_ENABLED] = enabled }
    }

    suspend fun setGeminiApiKey(key: String) {
        dataStore.edit { it[GEMINI_API_KEY] = key }
    }

    suspend fun setAutoWipeEnabled(enabled: Boolean) {
        dataStore.edit { it[AUTO_WIPE_ENABLED] = enabled }
    }

    suspend fun resetAll() {
        dataStore.edit { it.clear() }
    }
}
