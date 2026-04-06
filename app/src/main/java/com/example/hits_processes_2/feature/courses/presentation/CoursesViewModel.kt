package com.example.hits_processes_2.feature.courses.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hits_processes_2.feature.authorization.domain.usecase.LogoutUseCase
import com.example.hits_processes_2.feature.courses.data.repository.CoursesException
import com.example.hits_processes_2.feature.courses.domain.usecase.CreateCourseUseCase
import com.example.hits_processes_2.feature.courses.domain.usecase.GetMyCoursesUseCase
import com.example.hits_processes_2.feature.courses.domain.usecase.GetMyProfileUseCase
import com.example.hits_processes_2.feature.courses.domain.usecase.JoinCourseUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CoursesViewModel(
    private val getMyCoursesUseCase: GetMyCoursesUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val createCourseUseCase: CreateCourseUseCase,
    private val joinCourseUseCase: JoinCourseUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CoursesUiState(isLoading = true))
    val state: StateFlow<CoursesUiState> = _state.asStateFlow()

    private val _createDialogState = MutableStateFlow<CreateCourseDialogState?>(null)
    val createDialogState: StateFlow<CreateCourseDialogState?> = _createDialogState.asStateFlow()

    private val _joinDialogState = MutableStateFlow<JoinCourseDialogState?>(null)
    val joinDialogState: StateFlow<JoinCourseDialogState?> = _joinDialogState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        loadCourses()
        loadProfile()
    }

    fun loadCourses() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null,
                isLoggedOut = false,
            )

            getMyCoursesUseCase()
                .onSuccess { courses ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        courses = courses,
                        errorMessage = null,
                    )
                }
                .onFailure { throwable ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = throwable.toReadableMessage(),
                    )
                }
        }
    }

    fun openCreateCourseDialog() {
        _createDialogState.value = CreateCourseDialogState()
    }

    fun dismissCreateCourseDialog() {
        _createDialogState.value = null
    }

    fun onCourseNameChanged(value: String) {
        _createDialogState.value = _createDialogState.value?.copy(
            name = value,
            errorMessage = null,
        )
    }

    fun onCourseDescriptionChanged(value: String) {
        _createDialogState.value = _createDialogState.value?.copy(
            description = value,
            errorMessage = null,
        )
    }

    fun submitCreateCourse() {
        val dialogState = _createDialogState.value ?: return
        val name = dialogState.name.trim()
        val description = dialogState.description.trim()

        if (name.length < 3 || description.length < 3) {
            _createDialogState.value = dialogState.copy(
                errorMessage = "Название и описание должны содержать минимум 3 символа",
            )
            return
        }

        viewModelScope.launch {
            _createDialogState.value = dialogState.copy(
                isSubmitting = true,
                errorMessage = null,
            )

            createCourseUseCase(name, description)
                .onSuccess {
                    _createDialogState.value = null
                    loadCourses()
                }
                .onFailure { throwable ->
                    _createDialogState.value = dialogState.copy(
                        name = name,
                        description = description,
                        isSubmitting = false,
                        errorMessage = throwable.toReadableMessage(),
                    )
                }
        }
    }

    fun openJoinCourseDialog() {
        _joinDialogState.value = JoinCourseDialogState()
    }

    fun dismissJoinCourseDialog() {
        _joinDialogState.value = null
    }

    fun onJoinCodeChanged(value: String) {
        _joinDialogState.value = _joinDialogState.value?.copy(
            code = value,
            errorMessage = null,
        )
    }

    fun submitJoinCourse() {
        val dialogState = _joinDialogState.value ?: return
        val code = dialogState.code.trim()

        if (code.isBlank()) {
            _joinDialogState.value = dialogState.copy(
                errorMessage = "Введите код курса",
            )
            return
        }

        viewModelScope.launch {
            _joinDialogState.value = dialogState.copy(
                isSubmitting = true,
                errorMessage = null,
            )

            joinCourseUseCase(code)
                .onSuccess {
                    _joinDialogState.value = null
                    loadCourses()
                }
                .onFailure { throwable ->
                    _joinDialogState.value = dialogState.copy(
                        code = code,
                        isSubmitting = false,
                        errorMessage = throwable.toReadableMessage(),
                    )
                }
        }
    }

    fun logout() {
        if (_state.value.isLoggedOut) return

        viewModelScope.launch {
            logoutUseCase()
            _state.value = _state.value.copy(isLoggedOut = true)
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            getMyProfileUseCase()
                .onSuccess { profile ->
                    _state.value = _state.value.copy(userName = profile.fullName)
                }
                .onFailure {
                    // Keep header usable even if profile request fails.
                }
        }
    }

    private fun Throwable.toReadableMessage(): String {
        return when (this) {
            is CoursesException -> message
            else -> message ?: "Something went wrong"
        }
    }
}
