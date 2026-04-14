package com.example.hits_processes_2.feature.captain_selection.presentation

data class CaptainCandidate(
    val id: String,
    val fullName: String,
    val isCaptain: Boolean,
    val teamId: String? = null,
)
