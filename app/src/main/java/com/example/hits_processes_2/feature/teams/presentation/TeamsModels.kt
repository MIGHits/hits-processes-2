package com.example.hits_processes_2.feature.teams.presentation

data class Team(
    val id: Int,
    val number: Int,
    val members: List<TeamMember>,
    val submission: String? = null,
    val submittedAt: String? = null,
    val status: SubmissionStatus = SubmissionStatus.NOT_SUBMITTED,
    val grade: Int? = null,
)

data class TeamMember(
    val id: Int,
    val fullName: String,
    val isCaptain: Boolean = false,
)

enum class UserRole {
    STUDENT,
    TEACHER,
    MAIN_TEACHER,
}

val UserRole.title: String
    get() = when (this) {
        UserRole.STUDENT -> "Студент"
        UserRole.TEACHER -> "Преподаватель"
        UserRole.MAIN_TEACHER -> "Главный преподаватель"
    }

enum class TeamFormation {
    RANDOM,
    CUSTOM,
    DRAFT,
    STUDENTS,
}

val TeamFormation.title: String
    get() = when (this) {
        TeamFormation.RANDOM -> "Случайное распределение"
        TeamFormation.CUSTOM -> "Ручное формирование"
        TeamFormation.DRAFT -> "Драфт"
        TeamFormation.STUDENTS -> "Свободный выбор"
    }

enum class SubmissionStatus(val title: String) {
    SUBMITTED("Сдано"),
    LATE("Сдано с опозданием"),
    NOT_SUBMITTED("Не сдано"),
}
