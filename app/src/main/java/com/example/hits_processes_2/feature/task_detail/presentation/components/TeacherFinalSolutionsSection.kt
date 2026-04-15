package com.example.hits_processes_2.feature.task_detail.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
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
import com.example.hits_processes_2.feature.teams.domain.model.Team

@Composable
fun TeacherFinalSolutionsSection(
    teams: List<Team>,
    onFileClick: (String) -> Unit,
) {
    val submittedTeams = teams.filter { it.submissionFileId != null && it.submission != null }
    if (submittedTeams.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.task_detail_teacher_final_solutions_title),
            style = MaterialTheme.typography.titleMedium,
        )
        submittedTeams.forEach { team ->
            Text(
                text = "Команда ${team.number}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            ClickableFileRow(
                fileName = team.submission.orEmpty(),
                onClick = { onFileClick(team.submissionFileId!!) },
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun ClickableFileRow(
    fileName: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
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
                text = fileName,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

