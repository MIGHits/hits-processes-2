package com.example.hits_processes_2.feature.captain_selection.presentation

sealed class CaptainSelectionScreenState {
    data object Loading : CaptainSelectionScreenState()

    data class Content(
        val candidates: List<CaptainCandidate>,
        val requiredCaptainsCount: Int,
        val isRefreshing: Boolean = false,
        val errorMessage: String? = null,
    ) : CaptainSelectionScreenState()

    data class Error(
        val message: String,
    ) : CaptainSelectionScreenState()
}
