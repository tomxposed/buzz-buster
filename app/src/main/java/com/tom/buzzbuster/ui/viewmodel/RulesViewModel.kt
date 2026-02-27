package com.tom.buzzbuster.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tom.buzzbuster.data.BuzzBusterRepository
import com.tom.buzzbuster.data.model.FilterRule
import com.tom.buzzbuster.data.model.FilterType
import com.tom.buzzbuster.service.GeminiApiClient
import com.tom.buzzbuster.service.GeminiResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class RulesUiState(
    val rules: List<FilterRule> = emptyList(),
    val isLoading: Boolean = true,
    val aiGenerating: Boolean = false,
    val aiResult: String? = null,
    val aiError: String? = null,
    val searchQuery: String = "",
    val hasApiKey: Boolean = false
)

class RulesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BuzzBusterRepository.getInstance(application)

    private val _uiState = MutableStateFlow(RulesUiState())
    val uiState: StateFlow<RulesUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            combine(
                repository.getAllRules(),
                _searchQuery,
                repository.preferences.geminiApiKey
            ) { rules, query, apiKey ->
                val filtered = if (query.isBlank()) rules
                else rules.filter {
                    it.name.contains(query, ignoreCase = true) ||
                    it.pattern.contains(query, ignoreCase = true) ||
                    (it.targetPackage?.contains(query, ignoreCase = true) == true)
                }
                _uiState.value = _uiState.value.copy(
                    rules = filtered,
                    isLoading = false,
                    searchQuery = query,
                    hasApiKey = apiKey.isNotBlank()
                )
            }.collect()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addRule(name: String, filterType: FilterType, pattern: String, targetPackage: String?, originalPrompt: String? = null) {
        viewModelScope.launch {
            repository.insertRule(
                FilterRule(
                    name = name,
                    filterType = filterType,
                    pattern = pattern,
                    targetPackage = targetPackage?.takeIf { it.isNotBlank() },
                    originalPrompt = originalPrompt
                )
            )
        }
    }

    fun updateRule(rule: FilterRule) {
        viewModelScope.launch {
            repository.updateRule(rule.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun toggleRule(rule: FilterRule) {
        viewModelScope.launch {
            repository.updateRule(rule.copy(isEnabled = !rule.isEnabled, updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteRule(rule: FilterRule) {
        viewModelScope.launch {
            repository.deleteRule(rule)
        }
    }

    fun generateRegexFromIntent(intent: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(aiGenerating = true, aiResult = null, aiError = null)

            val apiKey = repository.preferences.geminiApiKey.first()
            if (apiKey.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    aiGenerating = false,
                    aiError = "Gemini API key not configured. Set it in Settings."
                )
                return@launch
            }

            when (val result = GeminiApiClient.generateRegex(apiKey, intent)) {
                is GeminiResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        aiGenerating = false,
                        aiResult = result.regex,
                        aiError = null
                    )
                }
                is GeminiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        aiGenerating = false,
                        aiResult = null,
                        aiError = result.message
                    )
                }
            }
        }
    }

    fun clearAiState() {
        _uiState.value = _uiState.value.copy(aiResult = null, aiError = null, aiGenerating = false)
    }
}
