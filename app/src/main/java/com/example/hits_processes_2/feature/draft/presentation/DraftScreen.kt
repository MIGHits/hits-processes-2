package com.example.hits_processes_2.feature.draft.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.hits_processes_2.feature.draft.presentation.components.DraftMemberItem
import com.example.hits_processes_2.feature.draft.presentation.components.DraftPickDialog
import com.example.hits_processes_2.feature.draft.presentation.components.DraftTeamCard
import com.example.hits_processes_2.feature.draft.presentation.components.DraftTopBar
import com.example.hits_processes_2.ui.theme.Hitsprocesses2Theme
import org.koin.androidx.compose.koinViewModel

@Composable
fun DraftRoute(
    courseId: String,
    taskId: String,
    draftId: String,
    userRoleName: String,
    currentUserId: String?,
    onNavigateBack: () -> Unit,
    onOpenTeams: (courseId: String, taskId: String, role: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DraftViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(courseId, taskId, draftId, currentUserId) {
        viewModel.load(
            courseId = courseId,
            taskId = taskId,
            draftId = draftId,
            currentUserId = currentUserId,
        )
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                DraftUiEffect.OpenTeams -> onOpenTeams(courseId, taskId, userRoleName)
            }
        }
    }

    DraftScreen(
        state = state,
        onNavigateBack = onNavigateBack,
        onDismissPickDialog = viewModel::dismissPickDialog,
        onSelectStudent = viewModel::selectStudent,
        onRetry = { viewModel.retry(courseId, taskId, draftId, currentUserId) },
        modifier = modifier,
    )
}

@Composable
fun DraftScreen(
    state: DraftScreenState,
    onNavigateBack: () -> Unit,
    onDismissPickDialog: () -> Unit,
    onSelectStudent: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        DraftScreenState.Loading -> DraftScaffold(
            onNavigateBack = onNavigateBack,
            modifier = modifier,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
        is DraftScreenState.Error -> DraftScaffold(
            onNavigateBack = onNavigateBack,
            modifier = modifier,
        ) {
            DraftErrorState(
                message = state.message,
                onRetry = onRetry,
                modifier = Modifier.fillMaxSize(),
            )
        }
        is DraftScreenState.Content -> {
            DraftScreen(
                teams = state.draft.toUiTeams(),
                availableStudents = state.availableStudents,
                isCaptainTurn = state.isPickDialogVisible,
                currentCaptainName = state.draft.currentPickerName(),
                onNavigateBack = onNavigateBack,
                onDismissPickDialog = onDismissPickDialog,
                onSelectStudent = onSelectStudent,
                modifier = modifier,
            )
        }
    }
}

@Composable
fun DraftScreen(
    teams: List<DraftTeam>,
    availableStudents: List<DraftStudent>,
    isCaptainTurn: Boolean,
    currentCaptainName: String?,
    onNavigateBack: () -> Unit,
    onDismissPickDialog: () -> Unit,
    onSelectStudent: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    DraftScaffold(
        onNavigateBack = onNavigateBack,
        modifier = modifier,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (!isCaptainTurn && currentCaptainName != null) {
                item {
                    Text(
                        text = "Сейчас выбирает: $currentCaptainName",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            items(teams, key = DraftTeam::id) { team ->
                DraftTeamCard(teamNumber = team.number) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        team.members.forEach { member ->
                            DraftMemberItem(member = member)
                        }
                    }
                }
            }
        }
    }

    if (isCaptainTurn) {
        DraftPickDialog(
            students = availableStudents,
            onDismiss = onDismissPickDialog,
            onSelectStudent = onSelectStudent,
        )
    }
}

@Composable
private fun DraftScaffold(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            DraftTopBar(onNavigateBack = onNavigateBack)
        },
        content = content,
    )
}

@Composable
private fun DraftErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
            )
            Button(onClick = onRetry) {
                Text("Повторить")
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 900)
@Composable
private fun DraftScreenCaptainTurnPreview() {
    Hitsprocesses2Theme {
        DraftScreen(
            teams = previewDraftTeams,
            availableStudents = previewDraftStudents,
            isCaptainTurn = true,
            currentCaptainName = "Иван Петров",
            onNavigateBack = {},
            onDismissPickDialog = {},
            onSelectStudent = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 900)
@Composable
private fun DraftScreenObserverPreview() {
    Hitsprocesses2Theme {
        DraftScreen(
            teams = previewDraftTeams,
            availableStudents = previewDraftStudents,
            isCaptainTurn = false,
            currentCaptainName = "Алексей Смирнов",
            onNavigateBack = {},
            onDismissPickDialog = {},
            onSelectStudent = {},
        )
    }
}
