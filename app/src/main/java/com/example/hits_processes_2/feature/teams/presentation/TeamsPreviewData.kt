package com.example.hits_processes_2.feature.teams.presentation

internal val previewTeams = listOf(
    Team(
        id = "1",
        number = 1,
        members = listOf(
            TeamMember(id = "1", fullName = "Ivan Petrov", isCaptain = true),
            TeamMember(id = "2", fullName = "Maria Sokolova"),
        ),
        submission = "team1_solution.pdf",
        submittedAt = "04.04.2026 18:30",
        status = SubmissionStatus.SUBMITTED,
        grade = 9,
    ),
    Team(
        id = "2",
        number = 2,
        members = listOf(
            TeamMember(id = "3", fullName = "Alexey Smirnov", isCaptain = true),
            TeamMember(id = "4", fullName = "Olga Kuznetsova"),
        ),
        status = SubmissionStatus.NOT_SUBMITTED,
    ),
)

internal val previewAvailableStudents = listOf(
    TeamMember(id = "5", fullName = "Ekaterina Orlova"),
    TeamMember(id = "6", fullName = "Dmitry Frolov"),
)
