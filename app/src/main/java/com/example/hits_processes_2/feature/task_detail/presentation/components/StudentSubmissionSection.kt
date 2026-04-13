package com.example.hits_processes_2.feature.task_detail.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.R
import com.example.hits_processes_2.feature.file_attachment.presentation.AttachedFilesSection
import com.example.hits_processes_2.feature.file_attachment.presentation.model.SelectedFileAttachment

@Composable
fun StudentSubmissionSection(
    files: List<SelectedFileAttachment>,
    onFilesChanged: (List<SelectedFileAttachment>) -> Unit,
    onFileRemoved: (Int) -> Unit,
    onSubmitClicked: () -> Unit,
    onCancelSubmissionClicked: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.task_detail_submission_title),
            style = MaterialTheme.typography.titleMedium,
        )

        Text(
            text = stringResource(R.string.task_detail_submission_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        AttachedFilesSection(
            files = files,
            onFilesSelected = onFilesChanged,
            onFileRemoved = onFileRemoved,
        )

        Button(
            onClick = onSubmitClicked,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.task_detail_submit_button))
        }

        if (files.isNotEmpty()) {
            OutlinedButton(
                onClick = onCancelSubmissionClicked,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.task_detail_cancel_submission_button))
            }
        }
    }
}
