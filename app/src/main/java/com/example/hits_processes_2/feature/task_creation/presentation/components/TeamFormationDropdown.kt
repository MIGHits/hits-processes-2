package com.example.hits_processes_2.feature.task_creation.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.R
import com.example.hits_processes_2.feature.task_creation.presentation.TeamFormationRule

@Composable
fun TeamFormationDropdown(
    selected: TeamFormationRule?,
    expanded: Boolean,
    onToggle: () -> Unit,
    onSelect: (TeamFormationRule) -> Unit,
) {
    val density = LocalDensity.current
    var anchorWidthPx by remember { mutableIntStateOf(0) }

    Column {
        Text(
            text = stringResource(R.string.task_creation_team_rule_label),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { anchorWidthPx = it.width },
        ) {
            TextField(
                value = selected?.displayText() ?: "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = stringResource(R.string.task_creation_team_rule_placeholder)) },
                readOnly = true,
                enabled = false,
                singleLine = true,
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                    )
                },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                colors = taskCreationFieldColors(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onToggle),
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onToggle,
                modifier = Modifier.width(with(density) { anchorWidthPx.toDp() }),
            ) {
                TeamFormationRule.entries.forEach { rule ->
                    DropdownMenuItem(
                        text = { Text(text = rule.displayText()) },
                        onClick = { onSelect(rule) },
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamFormationRule.displayText(): String {
    return when (this) {
        TeamFormationRule.RANDOM -> stringResource(R.string.task_creation_team_rule_random)
        TeamFormationRule.STUDENTS -> stringResource(R.string.task_creation_team_rule_students)
        TeamFormationRule.TEACHER -> stringResource(R.string.task_creation_team_rule_teacher)
        TeamFormationRule.DRAFT -> stringResource(R.string.task_creation_team_rule_draft)
    }
}
