package com.example.hits_processes_2.feature.task_detail.presentation

import com.example.hits_processes_2.feature.file_attachment.domain.model.UploadedFileAttachment
import com.example.hits_processes_2.feature.file_attachment.presentation.model.SelectedFileAttachment

sealed interface TaskDetailUiEvent {
    data object BackClicked : TaskDetailUiEvent
    data object RetryClicked : TaskDetailUiEvent
    data class FileClicked(val fileId: String) : TaskDetailUiEvent
    // Student: pick files locally (triggers upload)
    data class SubmissionFilesPicked(val files: List<SelectedFileAttachment>) : TaskDetailUiEvent
    data class UploadedSubmissionFileRemoved(val index: Int) : TaskDetailUiEvent
    // Student: attach answer (uses already uploaded files)
    data object AttachAnswerClicked : TaskDetailUiEvent
    data class FilesUploaded(val files: List<UploadedFileAttachment>) : TaskDetailUiEvent
    data object CancelSubmissionClicked : TaskDetailUiEvent
    data object CancelMyAttachedAnswersClicked : TaskDetailUiEvent
    // Captain: finalize or cancel final submission
    data object SubmitAnswerClicked : TaskDetailUiEvent
    data object UnsubmitAnswerClicked : TaskDetailUiEvent
    data object VotingClicked : TaskDetailUiEvent
    data object VotingDismissed : TaskDetailUiEvent
    data class VotingOptionSelected(val answerId: String) : TaskDetailUiEvent
    data object CaptainChoiceClicked : TaskDetailUiEvent
    data object CaptainChoiceDismissed : TaskDetailUiEvent
    data class CaptainChoiceOptionSelected(val answerId: String) : TaskDetailUiEvent
    // Teacher/navigation events
    data object TeamsClicked : TaskDetailUiEvent
    data object CaptainSelectionClicked : TaskDetailUiEvent
    data object EvaluateClicked : TaskDetailUiEvent
    data object EditClicked : TaskDetailUiEvent
}
