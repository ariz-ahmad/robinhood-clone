package com.example.robinhoodclone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class WatchlistViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<WatchlistUiState>(WatchlistUiState.Loading)
    val uiState: StateFlow<WatchlistUiState> = _uiState.asStateFlow()

    private val repository = WatchlistRepository()

    private val _searchQuery = MutableStateFlow("")
    val searchResults = _searchQuery
        .debounce(300) // wait 300ms after last keystroke
        .distinctUntilChanged() // skip if query hasn't changed
        .flatMapLatest { query -> // cancel previous search, start new one
            if (query.isBlank()) flowOf(repository.getWatchlist())
            else repository.searchStocks(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())


    init {
        loadWatchlist()
    }

    fun onQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun loadWatchlist() {
        viewModelScope.launch {
            _uiState.value = WatchlistUiState.Loading
            _uiState.value = try {
                val stocks = repository.getWatchlist()
                if (stocks.isEmpty()) WatchlistUiState.Empty
                else WatchlistUiState.Success(stocks)
            } catch (e: Exception) {
                WatchlistUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }
}

// Placeholder for the repository
class WatchlistRepository {
    private val allStocks = listOf(
        Stock("1", "AAPL", "Apple Inc.", 182.50, 2.4),
        Stock("2", "GOOGL", "Alphabet Inc.", 2834.41, -1.3),
        Stock("3", "MSFT", "Microsoft Corporation", 305.22, 1.8),
        Stock("4", "AMZN", "Amazon.com, Inc.", 135.39, -0.9),
        Stock("5", "TSLA", "Tesla, Inc.", 263.62, 3.1),
        Stock("6", "NVDA", "NVIDIA Corporation", 460.18, 5.2),
        Stock("7", "META", "Meta Platforms, Inc.", 316.46, -2.1),
        Stock("8", "JPM", "JPMorgan Chase & Co.", 146.96, 0.5),
        Stock("9", "V", "Visa Inc.", 236.10, 0.8),
        Stock("10", "JNJ", "Johnson & Johnson", 162.77, -0.2)
    )

    fun getWatchlist(): List<Stock> {
        return allStocks
    }

    fun searchStocks(query: String): kotlinx.coroutines.flow.Flow<List<Stock>> {
        return flowOf(allStocks.filter { stock ->
            stock.symbol.contains(query, ignoreCase = true) ||
                    stock.companyName.contains(query, ignoreCase = true)
        })
    }
}
