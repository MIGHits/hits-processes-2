package com.example.hits_processes_2.feature.task_detail.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.R
import com.example.hits_processes_2.feature.task_detail.domain.model.TaskDetail
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
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
            DetailSection(title = stringResource(R.string.task_detail_title_label)) {
                DetailValueBlock(value = task.title)
            }

            DetailSection(title = stringResource(R.string.task_detail_text_label)) {
                DetailValueBlock(value = task.text)
            }

            task.author?.fullName
                ?.takeIf(String::isNotBlank)
                ?.let { authorName ->
                    DetailSection(title = stringResource(R.string.task_detail_author_label)) {
                        DetailValueBlock(value = authorName)
                    }
                }

            DetailSection(title = stringResource(R.string.task_detail_deadline_label)) {
                DetailValueBlock(value = formatDateTime(task.deadlineIso))
            }

            DetailSection(title = stringResource(R.string.task_detail_max_score_label)) {
                DetailValueBlock(value = task.maxScore.toString())
            }

            DetailSection(title = stringResource(R.string.task_detail_team_formation_label)) {
                DetailValueBlock(value = task.teamFormationType.toDisplayValue())
            }

            DetailSection(title = stringResource(R.string.task_detail_attached_files_label)) {
                if (task.files.isEmpty()) {
                    Text(
                        text = stringResource(R.string.task_detail_files_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        task.files.forEach { file ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onFileClick(file.id) },
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Description,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Text(
                                        text = file.fileName?.takeIf(String::isNotBlank) ?: file.id,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp),
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = stringResource(R.string.task_detail_download_file_content_description),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
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
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        content()
    }
}

@Composable
private fun DetailValueBlock(value: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
        )
    }
}

private fun formatDateTime(value: String?): String {
    if (value.isNullOrBlank()) return "вЂ”"

    return parseOffsetDateTime(value)
        ?: parseInstant(value)
        ?: parseLocalDateTime(value)
        ?: value
}

private fun parseOffsetDateTime(value: String): String? {
    return runCatching {
        OffsetDateTime.parse(value).format(
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale("ru")),
        )
    }.getOrNull()
}

private fun parseInstant(value: String): String? {
    return runCatching {
        Instant.parse(value)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale("ru")))
    }.getOrNull()
}

private fun parseLocalDateTime(value: String): String? {
    return try {
        LocalDateTime.parse(value)
            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale("ru")))
    } catch (_: DateTimeParseException) {
        null
    }
}

@Composable
private fun String.toDisplayValue(): String {
    return when (this) {
        "RANDOM" -> stringResource(R.string.task_detail_team_rule_random)
        "FREE" -> stringResource(R.string.task_detail_team_rule_free)
        "CUSTOM" -> stringResource(R.string.task_detail_team_rule_custom)
        "DRAFT" -> stringResource(R.string.task_detail_team_rule_draft)
        else -> ifBlank { "—" }
    }
}
