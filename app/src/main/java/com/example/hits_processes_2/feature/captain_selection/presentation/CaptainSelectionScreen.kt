package com.example.hits_processes_2.feature.captain_selection.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.feature.captain_selection.presentation.components.CaptainCandidateItem
import com.example.hits_processes_2.feature.captain_selection.presentation.components.CaptainSelectionTopBar
import com.example.hits_processes_2.ui.theme.Hitsprocesses2Theme
import org.koin.androidx.compose.koinViewModel

@Composable
fun CaptainSelectionRoute(
    courseId: String,
    taskId: String,
    onNavigateBack: () -> Unit,
    onCaptainsSelected: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CaptainSelectionViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(courseId, taskId) {
        viewModel.load(courseId, taskId)
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                CaptainSelectionUiEffect.CaptainsSelected -> onCaptainsSelected()
            }
        }
    }

    CaptainSelectionScreen(
        state = state,
        onNavigateBack = onNavigateBack,
        onAssignCaptain = viewModel::assignCaptain,
        onRemoveCaptain = viewModel::removeCaptain,
        onRetry = viewModel::retry,
        modifier = modifier,
    )
}

@Composable
fun CaptainSelectionScreen(
    state: CaptainSelectionScreenState,
    onNavigateBack: () -> Unit,
    onAssignCaptain: (String) -> Unit,
    onRemoveCaptain: (CaptainCandidate) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CaptainSelectionTopBar(onNavigateBack = onNavigateBack)
        },
    ) { paddingValues ->
        when (state) {
            CaptainSelectionScreenState.Loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            is CaptainSelectionScreenState.Error -> CaptainSelectionError(
                message = state.message,
                onRetry = onRetry,
                paddingValues = paddingValues,
            )

            is CaptainSelectionScreenState.Content -> CaptainSelectionContent(
                state = state,
                onAssignCaptain = onAssignCaptain,
                onRemoveCaptain = onRemoveCaptain,
                paddingValues = paddingValues,
            )
        }
    }
}

@Composable
private fun CaptainSelectionContent(
    state: CaptainSelectionScreenState.Content,
    onAssignCaptain: (String) -> Unit,
    onRemoveCaptain: (CaptainCandidate) -> Unit,
    paddingValues: PaddingValues,
) {
    val selectedCaptainsCount = state.candidates.count(CaptainCandidate::isCaptain)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "$selectedCaptainsCount/${state.requiredCaptainsCount}",
                    style = MaterialTheme.typography.headlineSmall,
                )
                if (state.isRefreshing) {
                    Text(
                        text = "Обновляем список...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                state.errorMessage?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }

        items(state.candidates, key = CaptainCandidate::id) { candidate ->
            CaptainCandidateItem(
                candidate = candidate,
                onAssignCaptain = onAssignCaptain,
                onRemoveCaptain = onRemoveCaptain,
                enabled = !state.isRefreshing &&
                    (candidate.isCaptain || selectedCaptainsCount < state.requiredCaptainsCount),
            )
        }
    }
}

@Composable
private fun CaptainSelectionError(
    message: String,
    onRetry: () -> Unit,
    paddingValues: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Повторить")
        }
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 900)
@Composable
private fun CaptainSelectionScreenPreview() {
    Hitsprocesses2Theme {
        CaptainSelectionScreen(
            state = CaptainSelectionScreenState.Content(
                candidates = previewCaptainCandidates,
                requiredCaptainsCount = 4,
            ),
            onNavigateBack = {},
            onAssignCaptain = {},
            onRemoveCaptain = {},
            onRetry = {},
        )
    }
}
