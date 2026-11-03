package com.example.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.HistoryRepository
import com.example.data.local.PreferencesRepository
import com.example.data.local.ScanHistoryEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HistoryUiState(
    val searchQuery: String = "",
    val selectedFilterType: String = "All",
    val selectedItem: ScanHistoryEntity? = null,
    val showClearAllDialog: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModel(
    private val repository: HistoryRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    val isHistoryEnabled: StateFlow<Boolean> = preferencesRepository.historyEnabled

    val historyItems: StateFlow<List<ScanHistoryEntity>> = combine(
        _uiState
    ) { state ->
        val current = state[0]
        Pair(current.searchQuery, current.selectedFilterType)
    }.flatMapLatest { (query, filter) ->
        repository.searchHistory(query, filter)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onFilterTypeChanged(filter: String) {
        _uiState.value = _uiState.value.copy(selectedFilterType = filter)
    }

    fun onSelectItem(item: ScanHistoryEntity?) {
        _uiState.value = _uiState.value.copy(selectedItem = item)
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
            if (_uiState.value.selectedItem?.id == id) {
                _uiState.value = _uiState.value.copy(selectedItem = null)
            }
        }
    }

    fun toggleFavorite(id: Long, currentFav: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, !currentFav)
        }
    }

    fun showClearDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showClearAllDialog = show)
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAll()
            _uiState.value = _uiState.value.copy(selectedItem = null, showClearAllDialog = false)
        }
    }

    fun enableHistory(enabled: Boolean) {
        preferencesRepository.setHistoryEnabled(enabled)
    }

    companion object {
        fun provideFactory(
            repository: HistoryRepository,
            preferencesRepository: PreferencesRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HistoryViewModel(repository, preferencesRepository) as T
            }
        }
    }
}
