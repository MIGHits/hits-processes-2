package com.example.hits_processes_2.feature.authorization.presentation

data class LoginFormState(
    val email: String = "",
    val password: String = "",
)

data class RegisterFormState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
)

data class AuthorizationUiState(
    val selectedTab: AuthTab = AuthTab.Login,
    val login: LoginFormState = LoginFormState(),
    val register: RegisterFormState = RegisterFormState(),
    val isLoading: Boolean = false,
    val isAuthorized: Boolean = false,
    val errorMessage: String? = null,
)

enum class AuthTab {
    Login,
    Register,
}
