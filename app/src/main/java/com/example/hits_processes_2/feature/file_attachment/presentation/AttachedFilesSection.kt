package com.example.hits_processes_2.feature.file_attachment.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.hits_processes_2.R
import com.example.hits_processes_2.feature.file_attachment.presentation.model.SelectedFileAttachment

@Composable
fun AttachedFilesSection(
    files: List<SelectedFileAttachment>,
    onFilesSelected: (List<SelectedFileAttachment>) -> Unit,
    onFileRemoved: (Int) -> Unit,
    onPermissionDenied: (String) -> Unit = {},
) {
    val pickerState = rememberFileAttachmentPicker(
        onFilesPicked = { selectedFiles ->
            val mergedFiles = (files + selectedFiles).distinctBy { it.uriString }
            onFilesSelected(mergedFiles)
        },
        onPermissionDenied = onPermissionDenied,
    )

    FileAttachmentEditorSection(
        label = stringResource(R.string.file_attachment_label),
        items = files.mapIndexed { index, file ->
            RemovableAttachmentUiItem(
                key = file.uriString,
                fileName = file.name,
                onRemove = { onFileRemoved(index) },
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
