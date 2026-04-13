package com.example.hits_processes_2.feature.task_detail.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.R
import com.example.hits_processes_2.feature.task_detail.domain.model.TaskDetail
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TaskDetailInfoCard(
    task: TaskDetail,
    onFileClick: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DetailSection(
                title = stringResource(R.string.task_detail_title_label),
                value = task.title,
            )

            DetailSection(
                title = stringResource(R.string.task_detail_text_label),
                value = task.text,
            )

            task.author?.fullName
                ?.takeIf(String::isNotBlank)
                ?.let { authorName ->
                    DetailSection(
                        title = stringResource(R.string.task_detail_author_label),
                        value = authorName,
                    )
                }

            DetailSection(
                title = stringResource(R.string.task_detail_deadline_label),
                value = formatDateTime(task.deadlineIso),
            )

            DetailSection(
                title = stringResource(R.string.task_detail_max_score_label),
                value = task.maxScore.toString(),
            )

            DetailSection(
                title = stringResource(R.string.task_detail_team_formation_label),
                value = task.teamFormationType.toDisplayValue(),
            )

            DetailSection(
                title = stringResource(R.string.task_detail_attached_files_label),
                value = "",
            ) {
                if (task.files.isEmpty()) {
                    Text(
                        text = stringResource(R.string.task_detail_files_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        task.files.forEach { file ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(10.dp),
                                    )
                                    .clickable { onFileClick(file.id) }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Description,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = file.fileName?.takeIf(String::isNotBlank) ?: file.id,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 8.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSection(
    title: String,
    value: String,
    content: @Composable (() -> Unit)? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (content != null) {
            content()
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

private fun formatDateTime(value: String?): String {
    if (value.isNullOrBlank()) return "—"

    return runCatching {
        OffsetDateTime.parse(value).format(
            DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss", Locale("ru")),
        )
    }.getOrElse { value }
}

private fun String.toDisplayValue(): String {
    return when (this) {
        "RANDOM" -> "Случайным образом"
        "FREE" -> "Свободное формирование"
        "CUSTOM" -> "Преподаватель формирует"
        "DRAFT" -> "Драфт"
        else -> ifBlank { "—" }
    }
}
