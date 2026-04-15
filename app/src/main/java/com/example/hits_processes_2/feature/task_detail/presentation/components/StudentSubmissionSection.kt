package com.example.hits_processes_2.feature.task_detail.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.R
import com.example.hits_processes_2.feature.file_attachment.domain.model.UploadedFileAttachment
import com.example.hits_processes_2.feature.file_attachment.presentation.model.SelectedFileAttachment
import com.example.hits_processes_2.feature.file_attachment.presentation.rememberFileAttachmentPicker
import com.example.hits_processes_2.feature.file_attachment.presentation.FileAttachmentEditorSection
import com.example.hits_processes_2.feature.file_attachment.presentation.RemovableAttachmentUiItem
import com.example.hits_processes_2.feature.task_detail.domain.model.TeamFinalAnswer

@Composable
fun StudentSubmissionSection(
    uploadedFiles: List<UploadedFileAttachment>,
    myAttachedFiles: List<UploadedFileAttachment>,
    teamFinalAnswer: TeamFinalAnswer?,
    maxScore: Int,
    isCaptain: Boolean,
    isUploadingFiles: Boolean,
    isAttaching: Boolean,
    isSubmitting: Boolean,
    showVotingButton: Boolean,
    isVotingLoading: Boolean,
    showCaptainChoiceButton: Boolean,
    isCaptainChoiceLoading: Boolean,
    onFilesPicked: (List<SelectedFileAttachment>) -> Unit,
    onUploadedFileRemoved: (Int) -> Unit,
    onAttachAnswerClicked: () -> Unit,
    onCancelSubmissionClicked: () -> Unit,
    onCancelMyAttachedAnswersClicked: () -> Unit,
    onSubmitAnswerClicked: () -> Unit,
    onUnsubmitAnswerClicked: () -> Unit,
    onVotingClicked: () -> Unit,
    onCaptainChoiceClicked: () -> Unit,
    onFileClick: (String) -> Unit,
) {
    val pickerState = rememberFileAttachmentPicker(
        onFilesPicked = onFilesPicked,
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (myAttachedFiles.isNotEmpty()) {
            Text(
                text = stringResource(R.string.task_detail_submission_my_files_title),
                style = MaterialTheme.typography.titleMedium,
            )
            ReadOnlyFilesList(
                files = myAttachedFiles,
                onFileClick = onFileClick,
            )
            HorizontalDivider()
        }

        if (teamFinalAnswer != null) {
            Text(
                text = stringResource(R.string.task_detail_submission_team_final_title),
                style = MaterialTheme.typography.titleMedium,
            )
            if (teamFinalAnswer.files.isNotEmpty()) {
                ReadOnlyFilesList(
                    files = teamFinalAnswer.files,
                    onFileClick = onFileClick,
                )
            } else {
                Text(
                    text = stringResource(R.string.task_detail_files_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = if (teamFinalAnswer.score > 0) {
                    "Оценка: ${teamFinalAnswer.score} из $maxScore баллов"
                } else {
                    "Оценка: не выставлена"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            HorizontalDivider()
        }

        if (showVotingButton) {
            Button(
                onClick = onVotingClicked,
                enabled = !isVotingLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Голосование")
            }
        }

        if (showCaptainChoiceButton) {
            Button(
                onClick = onCaptainChoiceClicked,
                enabled = !isCaptainChoiceLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "\u0412\u044b\u0431\u0440\u0430\u0442\u044c \u0440\u0435\u0448\u0435\u043d\u0438\u0435")
            }
        }

        Text(
            text = stringResource(R.string.task_detail_submission_title),
            style = MaterialTheme.typography.titleMedium,
        )

        val isBlockedByExistingAttachments = myAttachedFiles.isNotEmpty()
        val isGraded = (teamFinalAnswer?.score ?: 0) > 0

        if (isGraded) {
            Text(
                text = stringResource(R.string.task_detail_submission_graded_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else if (isBlockedByExistingAttachments) {
            Text(
                text = stringResource(R.string.task_detail_submission_my_files_blocked_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedButton(
                onClick = onCancelMyAttachedAnswersClicked,
                enabled = !isUploadingFiles && !isAttaching,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.task_detail_submission_cancel_my_files_button))
            }
        } else {
            Text(
                text = stringResource(R.string.task_detail_submission_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            FileAttachmentEditorSection(
                label = stringResource(R.string.file_attachment_label),
                items = uploadedFiles.mapIndexed { index, file ->
                    RemovableAttachmentUiItem(
                        key = file.id,
                        fileName = file.fileName,
                        onRemove = { onUploadedFileRemoved(index) },
                    )
                },
                addFileTitle = stringResource(R.string.file_attachment_add_file_title),
                pickDocumentsButtonText = stringResource(R.string.file_attachment_select_button),
                pickGalleryButtonText = stringResource(R.string.file_attachment_gallery_button),
                removeFileContentDescription = stringResource(R.string.file_attachment_remove_content_description),
                onPickDocuments = pickerState::launchDocuments,
                onPickGallery = pickerState::launchGallery,
            )
        }

        if (!isGraded && !isBlockedByExistingAttachments && (isUploadingFiles || isAttaching)) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (!isGraded && !isBlockedByExistingAttachments) {
            Button(
                onClick = onAttachAnswerClicked,
                enabled = uploadedFiles.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.task_detail_attach_answer_button))
            }

            if (uploadedFiles.isNotEmpty()) {
                OutlinedButton(
                    onClick = onCancelSubmissionClicked,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.task_detail_cancel_submission_button))
                }
            }
        }

        if (isCaptain) {
            HorizontalDivider()

            Text(
                text = stringResource(R.string.task_detail_captain_section_title),
                style = MaterialTheme.typography.titleMedium,
            )

            Text(
                text = stringResource(R.string.task_detail_captain_section_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            val hasSubmittedFinal = !teamFinalAnswer?.submittedAtIso.isNullOrBlank()

            if (isUploadingFiles || isAttaching || isSubmitting) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                if (isGraded) {
                    Text(
                        text = stringResource(R.string.task_detail_submission_graded_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else if (hasSubmittedFinal) {
                    OutlinedButton(
                        onClick = onUnsubmitAnswerClicked,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = stringResource(R.string.task_detail_unsubmit_final_button))
                    }
                } else {
                    Button(
                        onClick = onSubmitAnswerClicked,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = stringResource(R.string.task_detail_submit_button))
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadOnlyFilesList(
    files: List<UploadedFileAttachment>,
    onFileClick: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        files.forEach { file ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFileClick(file.id) },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Description,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = file.fileName.ifBlank { file.id },
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = stringResource(R.string.task_detail_download_file_content_description),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}
