package com.example.hits_processes_2.feature.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hits_processes_2.feature.authorization.domain.usecase.LogoutUseCase
import com.example.hits_processes_2.feature.profile.data.repository.ProfileException
import com.example.hits_processes_2.feature.profile.domain.usecase.GetMyProfileUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState(isLoading = true))
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isLoading = true,
                errorMessage = null,
                isLoggedOut = false,
            )

            getMyProfileUseCase()
                .onSuccess { user ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        user = user,
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

    fun logout() {
        if (_state.value.isLoggedOut) return

        viewModelScope.launch {
            logoutUseCase()
            _state.value = _state.value.copy(isLoggedOut = true)
        }
    }

    private fun Throwable.toReadableMessage(): String = when (this) {
        is ProfileException -> message
        else -> message ?: "Something went wrong"
    }
}
