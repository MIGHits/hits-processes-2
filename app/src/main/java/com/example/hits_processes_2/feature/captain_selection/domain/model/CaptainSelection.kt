package com.example.hits_processes_2.feature.captain_selection.domain.model

data class CaptainSelection(
    val candidates: List<CaptainSelectionCandidate>,
    val requiredCaptainsCount: Int,
)

data class CaptainSelectionCandidate(
    val id: String,
    val fullName: String,
    val isCaptain: Boolean,
    val teamId: String?,
)
