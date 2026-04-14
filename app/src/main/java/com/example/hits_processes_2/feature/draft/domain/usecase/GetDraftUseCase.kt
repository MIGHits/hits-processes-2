package com.example.hits_processes_2.feature.draft.domain.usecase

import com.example.hits_processes_2.feature.draft.domain.repository.DraftRepository

class GetDraftUseCase(
    private val repository: DraftRepository,
) {
    suspend operator fun invoke(draftId: String) = repository.getDraft(draftId)
}
