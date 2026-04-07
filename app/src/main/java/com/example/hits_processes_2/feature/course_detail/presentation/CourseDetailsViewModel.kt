package com.example.hits_processes_2.feature.course_detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hits_processes_2.feature.course_detail.data.repository.CourseDetailsException
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseDetailsRole
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseParticipant
import com.example.hits_processes_2.feature.course_detail.domain.usecase.ChangeUserRoleUseCase
import com.example.hits_processes_2.feature.course_detail.domain.usecase.EditCourseUseCase
import com.example.hits_processes_2.feature.course_detail.domain.usecase.GetCourseDetailsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CourseDetailsViewModel(
    private val courseId: String,
    private val getCourseDetailsUseCase: GetCourseDetailsUseCase,
    private val editCourseUseCase: EditCourseUseCase,
    private val changeUserRoleUseCase: ChangeUserRoleUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CourseDetailsUiState(isLoading = true))
    val state: StateFlow<CourseDetailsUiState> = _state.asStateFlow()

    private val _editDialogState = MutableStateFlow<CourseEditDialogState?>(null)
    val editDialogState: StateFlow<CourseEditDialogState?> = _editDialogState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null,
                isRefreshingRoles = false,
            )

            val (courseResult, tasksResult, participantsResult) = getCourseDetailsUseCase(courseId)

            val error = listOf(
                courseResult.exceptionOrNull(),
                tasksResult.exceptionOrNull(),
                participantsResult.exceptionOrNull(),
            ).firstOrNull()

            if (error != null) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = error.toReadableMessage(),
                )
                return@launch
            }

            val participants = participantsResult.getOrDefault(emptyList())
            _state.value = _state.value.copy(
                isLoading = false,
                course = courseResult.getOrNull(),
                tasks = tasksResult.getOrDefault(emptyList()),
                teachers = participants.filter { it.role != CourseDetailsRole.STUDENT },
                students = participants.filter { it.role == CourseDetailsRole.STUDENT },
                errorMessage = null,
                isRefreshingRoles = false,
            )
        }
    }

    fun openEditCourseDialog() {
        val course = _state.value.course ?: return
        _editDialogState.value = CourseEditDialogState(
            name = course.name,
            description = course.description,
        )
    }

    fun dismissEditCourseDialog() {
        _editDialogState.value = null
    }

    fun onEditCourseNameChanged(value: String) {
        _editDialogState.value = _editDialogState.value?.copy(
            name = value,
            errorMessage = null,
        )
    }

    fun onEditCourseDescriptionChanged(value: String) {
        _editDialogState.value = _editDialogState.value?.copy(
            description = value,
            errorMessage = null,
        )
    }

    fun submitCourseEdit() {
        val dialogState = _editDialogState.value ?: return
        val trimmedName = dialogState.name.trim()
        val trimmedDescription = dialogState.description.trim()

        if (trimmedName.length < 3 || trimmedDescription.length < 3) {
            _editDialogState.value = dialogState.copy(
                errorMessage = "Название и описание должны содержать минимум 3 символа",
            )
            return
        }

        viewModelScope.launch {
            _editDialogState.value = dialogState.copy(
                isSubmitting = true,
                errorMessage = null,
            )

            editCourseUseCase(courseId, trimmedName, trimmedDescription)
                .onSuccess { updatedCourse ->
                    _state.value = _state.value.copy(course = updatedCourse)
                    _editDialogState.value = null
                }
                .onFailure { throwable ->
                    _editDialogState.value = dialogState.copy(
                        name = trimmedName,
                        description = trimmedDescription,
                        isSubmitting = false,
                        errorMessage = throwable.toReadableMessage(),
                    )
                }
        }
    }

    fun promoteParticipant(participant: CourseParticipant) {
        when (participant.role) {
            CourseDetailsRole.STUDENT -> changeParticipantRole(participant.id, CourseDetailsRole.TEACHER)
            CourseDetailsRole.TEACHER -> changeParticipantRole(participant.id, CourseDetailsRole.HEAD_TEACHER)
            CourseDetailsRole.HEAD_TEACHER -> Unit
        }
    }

    fun demoteParticipant(participant: CourseParticipant) {
        when (participant.role) {
            CourseDetailsRole.HEAD_TEACHER -> changeParticipantRole(participant.id, CourseDetailsRole.TEACHER)
            CourseDetailsRole.TEACHER -> changeParticipantRole(participant.id, CourseDetailsRole.STUDENT)
            CourseDetailsRole.STUDENT -> Unit
        }
    }

    private fun changeParticipantRole(
        userId: String,
        newRole: CourseDetailsRole,
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isRefreshingRoles = true, errorMessage = null)
            changeUserRoleUseCase(courseId, userId, newRole)
                .onSuccess { refresh() }
                .onFailure { throwable ->
                    _state.value = _state.value.copy(
                        isRefreshingRoles = false,
                        errorMessage = throwable.toReadableMessage(),
                    )
                }
        }
    }

    private fun Throwable.toReadableMessage(): String = when (this) {
        is CourseDetailsException -> message
        else -> message ?: "Something went wrong"
    }
}
