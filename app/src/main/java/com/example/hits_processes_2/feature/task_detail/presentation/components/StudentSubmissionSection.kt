package com.example.hits_processes_2.feature.task_detail.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.R
import com.example.hits_processes_2.common.ui.component.FormPrimaryButton
import com.example.hits_processes_2.common.ui.component.FormSecondaryButton
import com.example.hits_processes_2.feature.file_attachment.domain.model.UploadedFileAttachment
import com.example.hits_processes_2.feature.file_attachment.presentation.FileAttachmentEditorSection
import com.example.hits_processes_2.feature.file_attachment.presentation.RemovableAttachmentUiItem
import com.example.hits_processes_2.feature.file_attachment.presentation.model.SelectedFileAttachment
import com.example.hits_processes_2.feature.file_attachment.presentation.rememberFileAttachmentPicker
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
    val isBlockedByExistingAttachments = myAttachedFiles.isNotEmpty()
    val isGraded = (teamFinalAnswer?.score ?: 0) > 0

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (myAttachedFiles.isNotEmpty()) {
            StudentSectionCard(
                title = stringResource(R.string.task_detail_submission_my_files_title),
            ) {
                ReadOnlyFilesList(
                    files = myAttachedFiles,
                    onFileClick = onFileClick,
                )
            }
        }

        teamFinalAnswer?.let { finalAnswer ->
            StudentSectionCard(
                title = stringResource(R.string.task_detail_submission_team_final_title),
            ) {
                if (finalAnswer.files.isNotEmpty()) {
                    ReadOnlyFilesList(
                        files = finalAnswer.files,
                        onFileClick = onFileClick,
                    )
                } else {
                    SecondaryInfoText(
                        text = stringResource(R.string.task_detail_files_empty),
                    )
                }

                HorizontalDivider()

                SecondaryInfoText(
                    text = if (finalAnswer.score > 0) {
                        "\u041e\u0446\u0435\u043d\u043a\u0430: ${finalAnswer.score} \u0438\u0437 $maxScore \u0431\u0430\u043b\u043b\u043e\u0432"
                    } else {
                        "\u041e\u0446\u0435\u043d\u043a\u0430: \u043d\u0435 \u0432\u044b\u0441\u0442\u0430\u0432\u043b\u0435\u043d\u0430"
                    },
                )
            }
        }

        if (showVotingButton || showCaptainChoiceButton) {
            StudentSectionCard(
                title = "\u0418\u0442\u043e\u0433\u043e\u0432\u043e\u0435 \u0440\u0435\u0448\u0435\u043d\u0438\u0435",
                subtitle = "\u0412\u044b\u0431\u0435\u0440\u0438\u0442\u0435, \u043a\u0430\u043a\u043e\u0435 \u0440\u0435\u0448\u0435\u043d\u0438\u0435 \u043f\u043e\u0439\u0434\u0435\u0442 \u0432 \u0438\u0442\u043e\u0433 \u043a\u043e\u043c\u0430\u043d\u0434\u044b.",
            ) {
                if (showVotingButton) {
                    FormPrimaryButton(
                        text = "\u0413\u043e\u043b\u043e\u0441\u043e\u0432\u0430\u043d\u0438\u0435",
                        onClick = onVotingClicked,
                        enabled = !isVotingLoading,
                        isLoading = isVotingLoading,
                    )
                }

                if (showCaptainChoiceButton) {
                    FormPrimaryButton(
                        text = "\u0412\u044b\u0431\u0440\u0430\u0442\u044c \u0440\u0435\u0448\u0435\u043d\u0438\u0435",
                        onClick = onCaptainChoiceClicked,
                        enabled = !isCaptainChoiceLoading,
                        isLoading = isCaptainChoiceLoading,
                    )
                }
            }
        }

        StudentSectionCard(
            title = stringResource(R.string.task_detail_submission_title),
            subtitle = when {
                isGraded -> stringResource(R.string.task_detail_submission_graded_hint)
                isBlockedByExistingAttachments -> stringResource(R.string.task_detail_submission_my_files_blocked_hint)
                else -> stringResource(R.string.task_detail_submission_hint)
            },
        ) {
            if (isBlockedByExistingAttachments) {
                FormSecondaryButton(
                    text = stringResource(R.string.task_detail_submission_cancel_my_files_button),
                    onClick = onCancelMyAttachedAnswersClicked,
                    enabled = !isUploadingFiles && !isAttaching,
                )
            } else {
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
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else if (!isGraded && !isBlockedByExistingAttachments) {
                FormPrimaryButton(
                    text = stringResource(R.string.task_detail_attach_answer_button),
                    onClick = onAttachAnswerClicked,
                    enabled = uploadedFiles.isNotEmpty(),
                )

                if (uploadedFiles.isNotEmpty()) {
                    FormSecondaryButton(
                        text = stringResource(R.string.task_detail_cancel_submission_button),
                        onClick = onCancelSubmissionClicked,
                    )
                }
            }
        }

        if (isCaptain) {
            StudentSectionCard(
                title = stringResource(R.string.task_detail_captain_section_title),
                subtitle = stringResource(R.string.task_detail_captain_section_hint),
            ) {
                val hasSubmittedFinal = !teamFinalAnswer?.submittedAtIso.isNullOrBlank()

                if (isUploadingFiles || isAttaching || isSubmitting) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (isGraded) {
                    SecondaryInfoText(
                        text = stringResource(R.string.task_detail_submission_graded_hint),
                    )
                } else if (hasSubmittedFinal) {
                    FormSecondaryButton(
                        text = stringResource(R.string.task_detail_unsubmit_final_button),
                        onClick = onUnsubmitAnswerClicked,
                    )
                } else {
                    FormPrimaryButton(
                        text = stringResource(R.string.task_detail_submit_button),
                        onClick = onSubmitAnswerClicked,
                    )
                }
            }
        }
    }
}

@Composable
private fun StudentSectionCard(
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                content()
            },
        )
    }
}

@Composable
private fun SecondaryInfoText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
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
                shape = RoundedCornerShape(12.dp),
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
