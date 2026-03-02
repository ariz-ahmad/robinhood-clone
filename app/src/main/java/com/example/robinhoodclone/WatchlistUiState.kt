package com.example.robinhoodclone

sealed class WatchlistUiState {
    object Loading : WatchlistUiState()
    object Empty : WatchlistUiState()
    data class Success(val stocks: List<Stock>) : WatchlistUiState()
    data class Error(val message: String) : WatchlistUiState()
}
