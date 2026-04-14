package com.example.hits_processes_2.feature.draft.presentation

internal val previewDraftTeams = listOf(
    DraftTeam(
        id = "team-1",
        number = 1,
        members = listOf(
            DraftTeamMember(id = "student-1", fullName = "Иван Петров", isCaptain = true),
            DraftTeamMember(id = "student-2", fullName = "Мария Соколова"),
        ),
    ),
    DraftTeam(
        id = "team-2",
        number = 2,
        members = listOf(
            DraftTeamMember(id = "student-3", fullName = "Алексей Смирнов", isCaptain = true),
        ),
    ),
)

internal val previewDraftStudents = listOf(
    DraftStudent(id = "student-4", fullName = "Ольга Кузнецова"),
    DraftStudent(id = "student-5", fullName = "Екатерина Орлова"),
    DraftStudent(id = "student-6", fullName = "Дмитрий Фролов"),
)
