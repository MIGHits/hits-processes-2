package com.example.hits_processes_2.feature.file_attachment.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.R
import com.example.hits_processes_2.feature.file_attachment.presentation.model.SelectedFileAttachment

@Composable
fun AttachedFilesSection(
    files: List<SelectedFileAttachment>,
    onFilesSelected: (List<SelectedFileAttachment>) -> Unit,
    onFileRemoved: (Int) -> Unit,
    onPermissionDenied: (String) -> Unit = {},
) {
    val dashedBorderColor = MaterialTheme.colorScheme.outline
    val pickerState = rememberFileAttachmentPicker(
        onFilesPicked = { selectedFiles ->
            val mergedFiles = (files + selectedFiles).distinctBy { it.uriString }
            onFilesSelected(mergedFiles)
        },
        onPermissionDenied = onPermissionDenied,
    )

    Column {
        Text(
            text = stringResource(R.string.file_attachment_label),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp),
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRoundRect(
                        color = dashedBorderColor,
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f),
                        ),
                        cornerRadius = CornerRadius(8.dp.toPx()),
                    )
                }
                .padding(16.dp),
        ) {
            if (files.isEmpty()) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(R.string.file_attachment_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
                )
            } else {
                files.forEachIndexed { index, file ->
                    InputChip(
                        selected = false,
                        onClick = {},
                        label = { Text(text = file.name, maxLines = 1) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(R.string.file_attachment_remove_content_description),
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onFileRemoved(index) },
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedButton(
                    onClick = pickerState::launchDocuments,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = stringResource(R.string.file_attachment_select_button))
                }

                OutlinedButton(
                    onClick = pickerState::launchGallery,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(text = stringResource(R.string.file_attachment_gallery_button))
                }
            }
        }
    }
}
