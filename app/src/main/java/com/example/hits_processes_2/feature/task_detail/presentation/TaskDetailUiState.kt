package com.example.hits_processes_2.feature.task_detail.presentation

import com.example.hits_processes_2.feature.course_detail.domain.model.CourseDetailsRole
import com.example.hits_processes_2.feature.file_attachment.domain.model.UploadedFileAttachment
import com.example.hits_processes_2.feature.task_detail.domain.model.TaskAnswer
import com.example.hits_processes_2.feature.task_detail.domain.model.TaskDetail
import com.example.hits_processes_2.feature.task_detail.domain.model.TeamFinalAnswer
import com.example.hits_processes_2.feature.teams.domain.model.Team
import com.example.hits_processes_2.feature.voting.presentation.VotingOption

data class TaskDetailUiState(
    val isLoading: Boolean = false,
    val task: TaskDetail? = null,
    val userRole: CourseDetailsRole = CourseDetailsRole.STUDENT,
    val showCaptainSelectionAction: Boolean = false,
    val isDraftEnded: Boolean = false,
    val isInTeam: Boolean = false,
    val isCaptain: Boolean = false,
    val myTeamId: String? = null,
    val uploadedSubmissionFiles: List<UploadedFileAttachment> = emptyList(),
    val myAttachedAnswers: List<TaskAnswer> = emptyList(),
    val teamFinalAnswer: TeamFinalAnswer? = null,
    val isUploadingFiles: Boolean = false,
    val isAttaching: Boolean = false,
    val isSubmitting: Boolean = false,
    val teacherTeams: List<Team> = emptyList(),
    val votingOptions: List<VotingOption> = emptyList(),
    val selectedVotingAnswerId: String? = null,
    val isVotingDialogVisible: Boolean = false,
    val isVotingLoading: Boolean = false,
    val selectedCaptainChoiceAnswerId: String? = null,
    val isCaptainChoiceDialogVisible: Boolean = false,
    val isCaptainChoiceLoading: Boolean = false,
    val errorMessage: String? = null,
)
