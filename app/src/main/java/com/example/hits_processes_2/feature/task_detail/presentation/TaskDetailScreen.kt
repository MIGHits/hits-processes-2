package com.example.hits_processes_2.feature.task_detail.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.hits_processes_2.R
import com.example.hits_processes_2.feature.task_detail.presentation.components.TaskDetailContent
import com.example.hits_processes_2.feature.voting.presentation.VotingDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    state: TaskDetailUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (TaskDetailUiEvent) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.task_detail_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = { onEvent(TaskDetailUiEvent.BackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                        )
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    actionColor = MaterialTheme.colorScheme.primary,
                )
            }
        },
    ) { paddingValues ->
        when {
            state.isLoading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            else -> TaskDetailContent(
                state = state,
                paddingValues = paddingValues,
                onEvent = onEvent,
            )
        }
    }

    if (state.isVotingDialogVisible) {
        VotingDialog(
            options = state.votingOptions,
            selectedOptionId = state.selectedVotingAnswerId,
            isLoading = state.isVotingLoading,
            title = "\u0413\u043e\u043b\u043e\u0441\u043e\u0432\u0430\u043d\u0438\u0435",
            description = "\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u043d\u0430 \u0440\u0435\u0448\u0435\u043d\u0438\u0435, \u0447\u0442\u043e\u0431\u044b \u043f\u0440\u043e\u0433\u043e\u043b\u043e\u0441\u043e\u0432\u0430\u0442\u044c",
            loadingText = "\u041e\u0442\u043f\u0440\u0430\u0432\u043b\u044f\u0435\u043c \u0433\u043e\u043b\u043e\u0441...",
            showVotesCount = true,
            onDismiss = { onEvent(TaskDetailUiEvent.VotingDismissed) },
            onSelectOption = { onEvent(TaskDetailUiEvent.VotingOptionSelected(it)) },
            onDownloadSolution = { onEvent(TaskDetailUiEvent.FileClicked(it)) },
        )
    }

    if (state.isCaptainChoiceDialogVisible) {
        VotingDialog(
            options = state.votingOptions,
            selectedOptionId = state.selectedCaptainChoiceAnswerId,
            isLoading = state.isCaptainChoiceLoading,
            title = "\u0412\u044b\u0431\u043e\u0440 \u0440\u0435\u0448\u0435\u043d\u0438\u044f",
            description = "\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u043d\u0430 \u0440\u0435\u0448\u0435\u043d\u0438\u0435, \u0447\u0442\u043e\u0431\u044b \u0441\u0434\u0435\u043b\u0430\u0442\u044c \u0435\u0433\u043e \u0438\u0442\u043e\u0433\u043e\u0432\u044b\u043c",
            loadingText = "\u0412\u044b\u0431\u0438\u0440\u0430\u0435\u043c \u0440\u0435\u0448\u0435\u043d\u0438\u0435...",
            showVotesCount = false,
            onDismiss = { onEvent(TaskDetailUiEvent.CaptainChoiceDismissed) },
            onSelectOption = { onEvent(TaskDetailUiEvent.CaptainChoiceOptionSelected(it)) },
            onDownloadSolution = { onEvent(TaskDetailUiEvent.FileClicked(it)) },
        )
    }
}
