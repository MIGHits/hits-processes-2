package com.example.hits_processes_2.feature.task_creation.presentation

enum class TeamFormationRule(val title: String) {
    RANDOM("Случайным образом"),
    STUDENTS("Студенты формируют"),
    TEACHER("Преподаватели формируют"),
    DRAFT("Драфт"),
}

enum class SubmissionStrategy(val title: String) {
    FIRST("Первое решение"),
    LAST("Последнее решение"),
    CAPTAIN("Решение капитана"),
    MAJORITY("Большинство"),
    TWO_THIRDS("2/3 голосов"),
}

data class AttachedFile(
    val name: String,
    val uriString: String,
)
