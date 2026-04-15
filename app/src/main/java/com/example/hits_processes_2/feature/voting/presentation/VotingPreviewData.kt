package com.example.hits_processes_2.feature.voting.presentation

internal val previewVotingOptions = listOf(
    VotingOption(
        id = "1",
        firstName = "Иван",
        lastName = "Петров",
        solutionFiles = listOf(
            VotingSolutionFile(id = "11", name = "Решение 1.pdf"),
            VotingSolutionFile(id = "12", name = "notes.txt"),
        ),
    ),
    VotingOption(
        id = "2",
        firstName = "Мария",
        lastName = "Соколова",
        solutionFiles = listOf(
            VotingSolutionFile(id = "21", name = "Лабораторная 2.zip"),
        ),
    ),
    VotingOption(
        id = "3",
        firstName = "Алексей",
        lastName = "Смирнов",
        solutionFiles = listOf(
            VotingSolutionFile(id = "31", name = "Финальный ответ.docx"),
        ),
    ),
    VotingOption(
        id = "4",
        firstName = "Ольга",
        lastName = "Кузнецова",
        solutionFiles = listOf(
            VotingSolutionFile(id = "41", name = "solution_final.kt"),
        ),
    ),
)
