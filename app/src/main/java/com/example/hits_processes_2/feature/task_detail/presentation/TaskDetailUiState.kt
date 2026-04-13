package com.example.hits_processes_2.feature.task_detail.presentation

import com.example.hits_processes_2.feature.course_detail.domain.model.CourseDetailsRole
import com.example.hits_processes_2.feature.file_attachment.presentation.model.SelectedFileAttachment
import com.example.hits_processes_2.feature.task_detail.domain.model.TaskDetail

data class TaskDetailUiState(
    val isLoading: Boolean = false,
    val task: TaskDetail? = null,
    val userRole: CourseDetailsRole = CourseDetailsRole.STUDENT,
    val submissionFiles: List<SelectedFileAttachment> = emptyList(),
    val errorMessage: String? = null,
)
