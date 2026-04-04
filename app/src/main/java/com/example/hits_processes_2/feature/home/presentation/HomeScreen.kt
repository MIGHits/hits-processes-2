package com.example.hits_processes_2.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onLoggedOut: () -> Unit,
) {
    val viewModel: HomeViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            onLoggedOut()
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.home_title),
                style = MaterialTheme.typography.headlineMedium,
            )

            Text(
                text = stringResource(R.string.home_description),
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
                style = MaterialTheme.typography.bodyLarge,
            )

            Button(
                onClick = viewModel::logout,
                enabled = !state.isLoggingOut,
            ) {
                if (state.isLoggingOut) {
                    CircularProgressIndicator()
                } else {
                    Text(text = stringResource(R.string.logout_button))
                }
            }
        }
    }
}
