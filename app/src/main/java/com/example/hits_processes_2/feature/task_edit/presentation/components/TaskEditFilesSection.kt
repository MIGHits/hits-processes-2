package com.example.hits_processes_2.feature.task_edit.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.hits_processes_2.R
import com.example.hits_processes_2.feature.file_attachment.presentation.FileAttachmentEditorSection
import com.example.hits_processes_2.feature.file_attachment.presentation.RemovableAttachmentUiItem
import com.example.hits_processes_2.feature.file_attachment.presentation.model.SelectedFileAttachment
import com.example.hits_processes_2.feature.file_attachment.presentation.rememberFileAttachmentPicker
import com.example.hits_processes_2.feature.task_edit.domain.model.EditTaskExistingFile

@Composable
fun TaskEditFilesSection(
    existingFiles: List<EditTaskExistingFile>,
    newFiles: List<SelectedFileAttachment>,
    onExistingFileRemoved: (String) -> Unit,
    onNewFileRemoved: (Int) -> Unit,
    onNewFilesSelected: (List<SelectedFileAttachment>) -> Unit,
) {
    val pickerState = rememberFileAttachmentPicker(
        onFilesPicked = { pickedFiles ->
            val mergedFiles = (newFiles + pickedFiles).distinctBy { it.uriString }
            onNewFilesSelected(mergedFiles)
        },
    )

    FileAttachmentEditorSection(
        label = stringResource(R.string.task_edit_files_label),
        items = buildList {
            existingFiles.forEach { file ->
                add(
                    RemovableAttachmentUiItem(
                        key = file.id,
                        fileName = file.fileName?.takeIf(String::isNotBlank) ?: file.id,
                        onRemove = { onExistingFileRemoved(file.id) },
                    ),
                )
            }
            newFiles.forEachIndexed { index, file ->
                add(
                    RemovableAttachmentUiItem(
                        key = file.uriString,
                        fileName = file.name,
                        onRemove = { onNewFileRemoved(index) },
                    ),
                )
            }
        },
        addFileTitle = stringResource(R.string.task_edit_add_file_title),
        pickDocumentsButtonText = stringResource(R.string.task_edit_pick_files_button),
        removeFileContentDescription = stringResource(R.string.task_edit_remove_file_content_description),
        onPickDocuments = pickerState::launchDocuments,
    )
}
