package com.tom.buzzbuster.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tom.buzzbuster.data.BuzzBusterRepository
import com.tom.buzzbuster.data.model.BlockedNotification
import com.tom.buzzbuster.service.NotificationInterceptorService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class HistoryUiState(
    val notifications: List<BlockedNotification> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = ""
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BuzzBusterRepository.getInstance(application)

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _searchQuery.collectLatest { query ->
                val flow = if (query.isBlank()) {
                    repository.getAllBlocked()
                } else {
                    repository.searchBlocked(query)
                }
                flow.collect { list ->
                    _uiState.value = HistoryUiState(
                        notifications = list,
                        isLoading = false,
                        searchQuery = query
                    )
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun restoreNotification(notification: BlockedNotification) {
        viewModelScope.launch {
            repository.markRestored(notification.id)
            NotificationInterceptorService.restoreNotification(
                getApplication(),
                notification
            )
        }
    }

    fun deleteNotification(notification: BlockedNotification) {
        viewModelScope.launch {
            repository.deleteBlocked(notification)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAllBlocked()
        }
    }

    fun getGroupedByDate(): Map<String, List<BlockedNotification>> {
        val notifications = _uiState.value.notifications
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val yesterday = (today.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }

        return notifications.groupBy { notification ->
            val cal = Calendar.getInstance().apply { timeInMillis = notification.blockedAt }
            when {
                cal.after(today) -> "Today"
                cal.after(yesterday) -> "Yesterday"
                else -> {
                    val diff = ((today.timeInMillis - cal.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                    when {
                        diff < 7 -> {
                            val dayNames = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
                            dayNames[cal.get(Calendar.DAY_OF_WEEK) - 1]
                        }
                        else -> {
                            val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                            "${months[cal.get(Calendar.MONTH)]} ${cal.get(Calendar.DAY_OF_MONTH)}, ${cal.get(Calendar.YEAR)}"
                        }
                    }
                }
            }
        }
    }
}
