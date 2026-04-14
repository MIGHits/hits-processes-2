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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

data class RemovableAttachmentUiItem(
    val key: String,
    val fileName: String,
    val onRemove: () -> Unit,
)

@Composable
fun FileAttachmentEditorSection(
    label: String,
    items: List<RemovableAttachmentUiItem>,
    addFileTitle: String,
    pickDocumentsButtonText: String,
    removeFileContentDescription: String,
    onPickDocuments: () -> Unit,
    modifier: Modifier = Modifier,
    pickGalleryButtonText: String? = null,
    onPickGallery: (() -> Unit)? = null,
) {
    val outlineColor = MaterialTheme.colorScheme.outline

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
        )

        items.forEach { item ->
            RemovableAttachmentRow(
                fileName = item.fileName,
                removeFileContentDescription = removeFileContentDescription,
                onRemove = item.onRemove,
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRoundRect(
                        color = outlineColor,
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f),
                        ),
                        cornerRadius = CornerRadius(12.dp.toPx()),
                    )
                },
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(28.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = addFileTitle,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                ) {
                    OutlinedButton(
                        onClick = onPickDocuments,
                        shape = RoundedCornerShape(12.dp),
                        modifier = if (onPickGallery != null) Modifier.weight(1f) else Modifier,
                    ) {
                        Text(text = pickDocumentsButtonText)
                    }

                    if (pickGalleryButtonText != null && onPickGallery != null) {
                        OutlinedButton(
                            onClick = onPickGallery,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f),
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(text = pickGalleryButtonText)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RemovableAttachmentRow(
    fileName: String,
    removeFileContentDescription: String,
    onRemove: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = fileName,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = removeFileContentDescription,
                modifier = Modifier
                    .size(18.dp)
                    .clickable(onClick = onRemove),
            )
        }
    }
}
