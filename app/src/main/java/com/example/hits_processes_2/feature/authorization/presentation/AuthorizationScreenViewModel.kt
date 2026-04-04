package com.example.hits_processes_2.feature.authorization.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hits_processes_2.feature.authorization.data.repository.AuthException
import com.example.hits_processes_2.feature.authorization.domain.model.RegisterData
import com.example.hits_processes_2.feature.authorization.domain.model.TokenPair
import com.example.hits_processes_2.feature.authorization.domain.model.UserCredentials
import com.example.hits_processes_2.feature.authorization.domain.usecase.LoginUseCase
import com.example.hits_processes_2.feature.authorization.domain.usecase.RegisterUseCase
import com.example.hits_processes_2.feature.authorization.presentation.validators.isEmailValid
import com.example.hits_processes_2.feature.authorization.presentation.validators.isNameValid
import com.example.hits_processes_2.feature.authorization.presentation.validators.isPasswordValid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthorizationScreenViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthorizationUiState())
    val state: StateFlow<AuthorizationUiState> = _state.asStateFlow()

    fun onTabSelected(tab: AuthTab) {
        updateState { copy(selectedTab = tab, errorMessage = null) }
    }

    fun onLoginEmailChange(value: String) {
        updateState {
            copy(
                login = login.copy(email = value),
                errorMessage = null,
            )
        }
    }

    fun onLoginPasswordChange(value: String) {
        updateState {
            copy(
                login = login.copy(password = value),
                errorMessage = null,
            )
        }
    }

    fun onRegisterFirstNameChange(value: String) {
        updateState {
            copy(
                register = register.copy(firstName = value),
                errorMessage = null,
            )
        }
    }

    fun onRegisterLastNameChange(value: String) {
        updateState {
            copy(
                register = register.copy(lastName = value),
                errorMessage = null,
            )
        }
    }

    fun onRegisterEmailChange(value: String) {
        updateState {
            copy(
                register = register.copy(email = value),
                errorMessage = null,
            )
        }
    }

    fun onRegisterPasswordChange(value: String) {
        updateState {
            copy(
                register = register.copy(password = value),
                errorMessage = null,
            )
        }
    }

    fun onLoginClick() {
        val snapshot = state.value
        val credentials = snapshot.login.toCredentials()

        if (!isEmailValid(credentials.email) || !isPasswordValid(credentials.password)) {
            showValidationError("Проверьте email и пароль")
            return
        }

        submit(
            snapshot = snapshot,
            failureTab = AuthTab.Login,
        ) {
            loginUseCase(credentials)
        }
    }

    fun onRegisterClick() {
        val snapshot = state.value
        val registerData = snapshot.register.toRegisterData()

        if (!registerData.isValid()) {
            showValidationError("Проверьте данные регистрации")
            return
        }

        submit(
            snapshot = snapshot,
            failureTab = AuthTab.Register,
        ) {
            registerUseCase(registerData)
        }
    }

    private fun submit(
        snapshot: AuthorizationUiState,
        failureTab: AuthTab,
        request: suspend () -> Result<TokenPair>,
    ) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, errorMessage = null) }

            request()
                .onSuccess {
                    updateState {
                        copy(
                            isLoading = false,
                            isAuthorized = true,
                            errorMessage = null,
                        )
                    }
                }
                .onFailure { error ->
                    _state.value = snapshot.copy(
                        selectedTab = failureTab,
                        isLoading = false,
                        isAuthorized = false,
                        errorMessage = error.toReadableMessage(),
                    )
                }
        }
    }

    private fun showValidationError(message: String) {
        updateState { copy(errorMessage = message) }
    }

    private fun updateState(transform: AuthorizationUiState.() -> AuthorizationUiState) {
        _state.value = _state.value.transform()
    }

    private fun RegisterData.isValid(): Boolean {
        return isNameValid(firstName) &&
            isNameValid(lastName) &&
            isEmailValid(email) &&
            isPasswordValid(password)
    }

    private fun LoginFormState.toCredentials(): UserCredentials = UserCredentials(
        email = email.trim(),
        password = password,
    )

    private fun RegisterFormState.toRegisterData(): RegisterData = RegisterData(
        email = email.trim(),
        password = password,
        firstName = firstName.trim(),
        lastName = lastName.trim(),
    )

    private fun Throwable.toReadableMessage(): String {
        return when (this) {
            is AuthException -> message
            else -> message ?: "Something went wrong"
        }
    }
}
