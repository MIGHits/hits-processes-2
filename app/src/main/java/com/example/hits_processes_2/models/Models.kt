package com.example.hits_processes_2.models

import com.example.hits_processes_2.feature.course_detail.presentation.formatDeadline
import java.time.LocalDateTime

// User Models
data class User(
	val id: Int,
	val firstName: String,
	val lastName: String,
	val email: String,
	val city: String? = null,
	val birthDate: String? = null
) {
	val fullName: String
		get() = "$firstName $lastName"

	val initials: String
		get() = "${firstName.firstOrNull()?.uppercase() ?: ""}${lastName.firstOrNull()?.uppercase() ?: ""}"
}

// Course Models
enum class UserRole {
	STUDENT,
	TEACHER,
	MAIN_TEACHER
}

data class Course(
	val id: Int,
	val name: String,
	val description: String,
	val code: String,
	val userRole: UserRole
)

// Assignment Models
enum class TeamFormation {
	RANDOM,
	STUDENTS,
	TEACHERS,
	DRAFT
}

enum class SubmissionStrategy {
	FIRST,
	LAST,
	CAPTAIN,
	MAJORITY,
	TWO_THIRDS
}

enum class SubmissionStatus {
	SUBMITTED,
	LATE,
	NOT_SUBMITTED
}

data class Assignment(
	val id: Int,
	val text: String,
	val deadline: LocalDateTime,
	val author: String,
	val files: List<String> = emptyList(),
	val teamFormation: TeamFormation,
	val teamCount: Int,
	val submissionStrategy: SubmissionStrategy
) {
	val formattedDeadline: String
		get() = formatDeadline(deadline)
}

// Team Models
data class TeamMember(
	val id: Int,
	val firstName: String,
	val lastName: String,
	val email: String? = null,
	val isCaptain: Boolean = false
) {
	val fullName: String
		get() = "$firstName $lastName"
}

data class Team(
	val id: Int,
	val number: Int,
	val members: List<TeamMember>,
	val submission: String? = null,
	val submittedAt: LocalDateTime? = null,
	val status: SubmissionStatus = SubmissionStatus.NOT_SUBMITTED,
	val grade: Int? = null
)

// Student Submission Models
data class StudentSubmission(
	val mySubmission: String?,
	val teamSubmission: String?,
	val submissionStatus: SubmissionStatus,
	val grade: Int?
)

// Participant Models
data class Participant(
	val id: Int,
	val firstName: String,
	val lastName: String,
	val email: String,
	val isMain: Boolean = false
) {
	val fullName: String
		get() = "$firstName $lastName"
}