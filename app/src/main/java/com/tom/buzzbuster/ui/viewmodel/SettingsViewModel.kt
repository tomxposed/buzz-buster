package com.tom.buzzbuster.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tom.buzzbuster.data.BuzzBusterRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SettingsUiState(
    val themeMode: String = "dark",
    val historyLimit: Int = 500,
    val isInterceptorEnabled: Boolean = true,
    val geminiApiKey: String = "",
    val autoWipeEnabled: Boolean = false
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BuzzBusterRepository.getInstance(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.preferences.themeMode,
                repository.preferences.historyLimit,
                repository.preferences.isInterceptorEnabled,
                repository.preferences.geminiApiKey,
                repository.preferences.autoWipeEnabled
            ) { theme, limit, interceptor, apiKey, autoWipe ->
                SettingsUiState(
                    themeMode = theme,
                    historyLimit = limit,
                    isInterceptorEnabled = interceptor,
                    geminiApiKey = apiKey,
                    autoWipeEnabled = autoWipe
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch { repository.preferences.setThemeMode(mode) }
    }

    fun setHistoryLimit(limit: Int) {
        viewModelScope.launch {
            repository.preferences.setHistoryLimit(limit)
            repository.trimHistory(limit)
        }
    }

    fun setInterceptorEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.preferences.setInterceptorEnabled(enabled) }
    }

    fun setGeminiApiKey(key: String) {
        viewModelScope.launch { repository.preferences.setGeminiApiKey(key) }
    }

    fun setAutoWipeEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.preferences.setAutoWipeEnabled(enabled) }
    }

    fun resetDefaults() {
        viewModelScope.launch { repository.preferences.resetAll() }
    }

    fun nukeAllData() {
        viewModelScope.launch {
            repository.deleteAllBlocked()
            repository.preferences.resetAll()
        }
    }
}
