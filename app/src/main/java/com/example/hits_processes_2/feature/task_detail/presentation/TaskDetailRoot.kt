package com.example.hits_processes_2.feature.task_detail.presentation

import android.content.Intent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.hits_processes_2.R
import com.example.hits_processes_2.feature.file_attachment.presentation.RememberFileTransferReceiver
import com.example.hits_processes_2.feature.file_attachment.service.FileTransferService
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TaskDetailRoot(
    courseId: String,
    taskId: String,
    userRoleName: String,
    onNavigateToEdit: (String, String) -> Unit,
    onNavigateBack: () -> Unit,
    onOpenTeams: (courseId: String, taskId: String, teamFormationType: String, userRoleName: String) -> Unit,
    onOpenDraft: (courseId: String, taskId: String, draftId: String, userRoleName: String) -> Unit,
    onOpenCaptainSelection: (courseId: String, taskId: String, draftId: String?, userRoleName: String) -> Unit,
) {
    val viewModel: TaskDetailViewModel = koinViewModel(
        parameters = { parametersOf(courseId, taskId, userRoleName) },
    )
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    RememberFileTransferReceiver(
        onDownloadFailed = { message ->
            val text = message.ifBlank { context.getString(R.string.file_attachment_error_download) }
            scope.launch {
                snackbarHostState.showSnackbar(text)
            }
        },
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshCaptainSelectionAction()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                TaskDetailUiEffect.NavigateBack -> onNavigateBack()
                is TaskDetailUiEffect.NavigateToEdit -> onNavigateToEdit(effect.courseId, effect.taskId)
                is TaskDetailUiEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                is TaskDetailUiEffect.StartFileDownload -> {
                    val intent = Intent(context, FileTransferService::class.java).apply {
                        action = FileTransferService.ACTION_DOWNLOAD
                        putExtra(FileTransferService.EXTRA_FILE_ID, effect.fileId)
                    }
                    ContextCompat.startForegroundService(context, intent)
                    snackbarHostState.showSnackbar(
                        context.getString(R.string.task_detail_download_started),
                    )
                }
                is TaskDetailUiEffect.OpenTeams -> onOpenTeams(
                    effect.courseId,
                    effect.taskId,
                    effect.teamFormationType,
                    effect.userRoleName,
                )
                is TaskDetailUiEffect.OpenDraft -> onOpenDraft(
                    effect.courseId,
                    effect.taskId,
                    effect.draftId,
                    effect.userRoleName,
                )
                is TaskDetailUiEffect.OpenCaptainSelection -> onOpenCaptainSelection(
                    effect.courseId,
                    effect.taskId,
                    effect.draftId,
                    effect.userRoleName,
                )
            }
        }
    }

    TaskDetailScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
    )
}
