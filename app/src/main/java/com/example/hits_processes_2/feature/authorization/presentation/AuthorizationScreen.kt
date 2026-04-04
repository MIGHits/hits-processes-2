package com.example.hits_processes_2.feature.authorization.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.R
import com.example.hits_processes_2.feature.authorization.presentation.validators.isEmailValid
import com.example.hits_processes_2.feature.authorization.presentation.validators.isNameValid
import com.example.hits_processes_2.feature.authorization.presentation.validators.isPasswordValid
import org.koin.androidx.compose.koinViewModel

@Composable
fun AuthorizationScreen(
    onAuthSuccess: () -> Unit,
) {
    val viewModel: AuthorizationScreenViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isAuthorized) {
        if (state.isAuthorized) {
            onAuthSuccess()
        }
    }

    Scaffold { paddingValues ->
        if (state.isLoading) {
            LoadingContent()
        } else {
            AuthorizationContent(
                state = state,
                onTabSelected = viewModel::onTabSelected,
                onLoginEmailChange = viewModel::onLoginEmailChange,
                onLoginPasswordChange = viewModel::onLoginPasswordChange,
                onRegisterFirstNameChange = viewModel::onRegisterFirstNameChange,
                onRegisterLastNameChange = viewModel::onRegisterLastNameChange,
                onRegisterEmailChange = viewModel::onRegisterEmailChange,
                onRegisterPasswordChange = viewModel::onRegisterPasswordChange,
                onLoginClick = viewModel::onLoginClick,
                onRegisterClick = viewModel::onRegisterClick,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Composable
private fun AuthorizationContent(
    state: AuthorizationUiState,
    onTabSelected: (AuthTab) -> Unit,
    onLoginEmailChange: (String) -> Unit,
    onLoginPasswordChange: (String) -> Unit,
    onRegisterFirstNameChange: (String) -> Unit,
    onRegisterLastNameChange: (String) -> Unit,
    onRegisterEmailChange: (String) -> Unit,
    onRegisterPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var loginPasswordVisible by remember { mutableStateOf(false) }
    var registerPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val errorMessage = state.errorMessage
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.auth_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(24.dp))

                TabRow(selectedTabIndex = state.selectedTab.ordinal) {
                    Tab(
                        selected = state.selectedTab == AuthTab.Login,
                        onClick = { onTabSelected(AuthTab.Login) },
                        text = { Text(text = stringResource(R.string.tab_login)) },
                    )
                    Tab(
                        selected = state.selectedTab == AuthTab.Register,
                        onClick = { onTabSelected(AuthTab.Register) },
                        text = { Text(text = stringResource(R.string.tab_register)) },
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                when (state.selectedTab) {
                    AuthTab.Login -> LoginForm(
                        form = state.login,
                        passwordVisible = loginPasswordVisible,
                        onEmailChange = onLoginEmailChange,
                        onPasswordChange = onLoginPasswordChange,
                        onPasswordVisibilityToggle = { loginPasswordVisible = !loginPasswordVisible },
                        onLoginClick = onLoginClick,
                    )

                    AuthTab.Register -> RegisterForm(
                        form = state.register,
                        passwordVisible = registerPasswordVisible,
                        onFirstNameChange = onRegisterFirstNameChange,
                        onLastNameChange = onRegisterLastNameChange,
                        onEmailChange = onRegisterEmailChange,
                        onPasswordChange = onRegisterPasswordChange,
                        onPasswordVisibilityToggle = { registerPasswordVisible = !registerPasswordVisible },
                        onRegisterClick = onRegisterClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginForm(
    form: LoginFormState,
    passwordVisible: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onLoginClick: () -> Unit,
) {
    val emailValid = isEmailValid(form.email)
    val passwordValid = isPasswordValid(form.password)

    OutlinedTextField(
        value = form.email,
        onValueChange = onEmailChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = stringResource(R.string.email)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        isError = form.email.isNotBlank() && !emailValid,
        supportingText = {
            if (form.email.isNotBlank() && !emailValid) {
                Text(text = stringResource(R.string.email_error))
            }
        },
    )

    Spacer(modifier = Modifier.height(16.dp))

    PasswordField(
        value = form.password,
        passwordVisible = passwordVisible,
        onValueChange = onPasswordChange,
        onVisibilityToggle = onPasswordVisibilityToggle,
        isError = form.password.isNotBlank() && !passwordValid,
    )

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onLoginClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = emailValid && passwordValid,
    ) {
        Text(text = stringResource(R.string.login_button))
    }
}

@Composable
private fun RegisterForm(
    form: RegisterFormState,
    passwordVisible: Boolean,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    val firstNameValid = isNameValid(form.firstName)
    val lastNameValid = isNameValid(form.lastName)
    val emailValid = isEmailValid(form.email)
    val passwordValid = isPasswordValid(form.password)

    OutlinedTextField(
        value = form.firstName,
        onValueChange = onFirstNameChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = stringResource(R.string.first_name)) },
        singleLine = true,
        isError = form.firstName.isNotBlank() && !firstNameValid,
        supportingText = {
            if (form.firstName.isNotBlank() && !firstNameValid) {
                Text(text = stringResource(R.string.name_error))
            }
        },
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = form.lastName,
        onValueChange = onLastNameChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = stringResource(R.string.last_name)) },
        singleLine = true,
        isError = form.lastName.isNotBlank() && !lastNameValid,
        supportingText = {
            if (form.lastName.isNotBlank() && !lastNameValid) {
                Text(text = stringResource(R.string.name_error))
            }
        },
    )

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = form.email,
        onValueChange = onEmailChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = stringResource(R.string.email)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        isError = form.email.isNotBlank() && !emailValid,
        supportingText = {
            if (form.email.isNotBlank() && !emailValid) {
                Text(text = stringResource(R.string.email_error))
            }
        },
    )

    Spacer(modifier = Modifier.height(16.dp))

    PasswordField(
        value = form.password,
        passwordVisible = passwordVisible,
        onValueChange = onPasswordChange,
        onVisibilityToggle = onPasswordVisibilityToggle,
        isError = form.password.isNotBlank() && !passwordValid,
    )

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onRegisterClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = firstNameValid && lastNameValid && emailValid && passwordValid,
    ) {
        Text(text = stringResource(R.string.register_button))
    }
}

@Composable
private fun PasswordField(
    value: String,
    passwordVisible: Boolean,
    onValueChange: (String) -> Unit,
    onVisibilityToggle: () -> Unit,
    isError: Boolean,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = stringResource(R.string.password)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        isError = isError,
        supportingText = {
            if (isError) {
                Text(text = stringResource(R.string.password_error))
            }
        },
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            TextButton(onClick = onVisibilityToggle) {
                Text(
                    text = if (passwordVisible) {
                        stringResource(R.string.hide_password)
                    } else {
                        stringResource(R.string.show_password)
                    },
                )
            }
        },
    )
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}
