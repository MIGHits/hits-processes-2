package com.example.hits_processes_2.feature.draft.presentation

import com.example.hits_processes_2.feature.draft.domain.model.Draft
import com.example.hits_processes_2.feature.draft.domain.model.DraftRealtimeEvent

sealed class DraftScreenState {
    data object Loading : DraftScreenState()

    data class Content(
        val draft: Draft,
        val currentUserId: String?,
        val availableStudents: List<DraftStudent> = emptyList(),
        val isPickDialogVisible: Boolean = false,
        val pickDialogGeneration: Int = 0,
        val lastRealtimeEvent: DraftRealtimeEvent? = null,
        val errorMessage: String? = null,
        val isRefreshing: Boolean = false,
    ) : DraftScreenState()

    data class Error(
        val message: String,
    ) : DraftScreenState()
}
