package com.tom.buzzbuster.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tom.buzzbuster.data.BuzzBusterRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class HomeUiState(
    val isInterceptorEnabled: Boolean = true,
    val totalRules: Int = 0,
    val activeRules: Int = 0,
    val blockedToday: Int = 0,
    val totalBlocked: Int = 0,
    val isLoading: Boolean = true
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BuzzBusterRepository.getInstance(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        viewModelScope.launch {
            combine(
                repository.preferences.isInterceptorEnabled,
                repository.getRuleCount(),
                repository.getActiveRuleCount(),
                repository.getBlockedCountSince(todayStart),
                repository.getBlockedCount()
            ) { enabled, total, active, today, allBlocked ->
                HomeUiState(
                    isInterceptorEnabled = enabled,
                    totalRules = total,
                    activeRules = active,
                    blockedToday = today,
                    totalBlocked = allBlocked,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleInterceptor() {
        viewModelScope.launch {
            repository.preferences.setInterceptorEnabled(!_uiState.value.isInterceptorEnabled)
        }
    }
}
