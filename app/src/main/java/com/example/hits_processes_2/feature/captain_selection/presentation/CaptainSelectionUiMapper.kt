package com.example.hits_processes_2.feature.captain_selection.presentation

import com.example.hits_processes_2.feature.captain_selection.domain.model.CaptainSelection
import com.example.hits_processes_2.feature.captain_selection.domain.model.CaptainSelectionCandidate

fun CaptainSelection.toUi(): CaptainSelectionScreenState.Content {
    return CaptainSelectionScreenState.Content(
        candidates = candidates.map(CaptainSelectionCandidate::toUi),
        requiredCaptainsCount = requiredCaptainsCount,
    )
}

private fun CaptainSelectionCandidate.toUi(): CaptainCandidate {
    return CaptainCandidate(
        id = id,
        fullName = fullName,
        isCaptain = isCaptain,
        teamId = teamId,
    )
}
