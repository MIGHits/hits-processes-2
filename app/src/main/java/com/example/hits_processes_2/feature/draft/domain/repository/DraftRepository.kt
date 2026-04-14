package com.example.hits_processes_2.feature.draft.domain.repository

import com.example.hits_processes_2.feature.draft.domain.model.Draft
import com.example.hits_processes_2.feature.draft.domain.model.DraftRealtimeEvent
import kotlinx.coroutines.flow.Flow

interface DraftRepository {
    suspend fun getDraft(draftId: String): Result<Draft>
    fun observeDraft(draftId: String): Flow<DraftRealtimeEvent>
}
