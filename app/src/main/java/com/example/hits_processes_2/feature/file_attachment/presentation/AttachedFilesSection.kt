package com.example.hits_processes_2.feature.file_attachment.presentation

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.R
import com.example.hits_processes_2.feature.file_attachment.presentation.model.SelectedFileAttachment

@Composable
fun AttachedFilesSection(
    files: List<SelectedFileAttachment>,
    onFilesSelected: (List<SelectedFileAttachment>) -> Unit,
    onFileRemoved: (Int) -> Unit,
) {
    val context = LocalContext.current
    val dashedBorderColor = MaterialTheme.colorScheme.outline

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
    ) { uris: List<Uri> ->
        val selectedFiles = uris.map { uri ->
            SelectedFileAttachment(
                name = resolveFileName(context, uri),
                uriString = uri.toString(),
            )
        }
        if (selectedFiles.isNotEmpty()) {
            onFilesSelected(selectedFiles)
        }
    }

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

            OutlinedButton(
                onClick = { launcher.launch("*/*") },
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(text = stringResource(R.string.file_attachment_select_button))
            }
        }
    }
}

private fun resolveFileName(context: Context, uri: Uri): String {
    var name = context.getString(R.string.file_attachment_default_name)
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && cursor.moveToFirst()) {
            name = cursor.getString(nameIndex)
        }
    }
    return name
}
