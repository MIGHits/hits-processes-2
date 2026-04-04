package com.example.hits_processes_2.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hits_processes_2.feature.authorization.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeScreenState(
    val isLoggingOut: Boolean = false,
    val isLoggedOut: Boolean = false,
)

class HomeViewModel(
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()

    fun logout() {
        if (_state.value.isLoggingOut) return

        viewModelScope.launch {
            _state.value = HomeScreenState(isLoggingOut = true)
            logoutUseCase()
            _state.value = HomeScreenState(isLoggedOut = true)
        }
    }
}
