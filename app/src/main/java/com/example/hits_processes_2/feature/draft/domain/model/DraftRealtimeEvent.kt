package com.example.hits_processes_2.feature.draft.domain.model

sealed class DraftRealtimeEvent {
    data class DraftStarted(val draft: Draft) : DraftRealtimeEvent()
    data class OrderOfSelectionChanged(val pickTurns: List<DraftPickTurn>) : DraftRealtimeEvent()
    data class DraftEnded(val draft: Draft?) : DraftRealtimeEvent()
    data class TeamStructureChanged(val changedTeam: DraftTeam) : DraftRealtimeEvent()
    data class StudentJoinedTeam(
        val teamId: String,
        val user: DraftUser,
    ) : DraftRealtimeEvent()
    data object TimeToChooseStudent : DraftRealtimeEvent()
    data object AutoSelectionPerformed : DraftRealtimeEvent()
    data class Error(val message: String) : DraftRealtimeEvent()
    data class Unknown(val type: String, val rawData: String?) : DraftRealtimeEvent()
}
