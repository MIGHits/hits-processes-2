package com.example.hits_processes_2.feature.captain_selection.presentation

sealed interface CaptainSelectionUiEffect {
    data object CaptainsSelected : CaptainSelectionUiEffect
}
