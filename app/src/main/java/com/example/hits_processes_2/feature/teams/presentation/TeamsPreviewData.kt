package com.example.hits_processes_2.feature.teams.presentation

internal val previewTeams = listOf(
    Team(
        id = 1,
        number = 1,
        members = listOf(
            TeamMember(id = 1, fullName = "Иван Петров", isCaptain = true),
            TeamMember(id = 2, fullName = "Мария Соколова"),
        ),
        submission = "team1_solution.pdf",
        submittedAt = "04.04.2026 18:30",
        status = SubmissionStatus.SUBMITTED,
        grade = 9,
    ),
    Team(
        id = 2,
        number = 2,
        members = listOf(
            TeamMember(id = 3, fullName = "Алексей Смирнов", isCaptain = true),
            TeamMember(id = 4, fullName = "Ольга Кузнецова"),
        ),
        status = SubmissionStatus.NOT_SUBMITTED,
    ),
)

internal val previewAvailableStudents = listOf(
    TeamMember(id = 5, fullName = "Екатерина Орлова"),
    TeamMember(id = 6, fullName = "Дмитрий Фролов"),
)
