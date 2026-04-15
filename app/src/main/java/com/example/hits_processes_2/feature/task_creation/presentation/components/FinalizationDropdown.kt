package com.example.hits_processes_2.feature.task_creation.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.feature.task_creation.presentation.TaskAnswerFinalizationRule

@Composable
fun FinalizationDropdown(
    selected: TaskAnswerFinalizationRule?,
    expanded: Boolean,
    onToggle: () -> Unit,
    onSelect: (TaskAnswerFinalizationRule) -> Unit,
) {
    val density = LocalDensity.current
    var anchorWidthPx by remember { mutableIntStateOf(0) }

    Column {
        Text(
            text = "Способ определения итогового решения",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { anchorWidthPx = it.width }
                .clickable(onClick = onToggle),
        ) {
            TextField(
                value = selected?.displayText().orEmpty(),
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Выберите способ") },
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
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onToggle,
                modifier = Modifier.width(with(density) { anchorWidthPx.toDp() }),
            ) {
                TaskAnswerFinalizationRule.entries.forEach { rule ->
                    DropdownMenuItem(
                        text = { Text(text = rule.displayText()) },
                        onClick = { onSelect(rule) },
                    )
                }
            }
        }
    }
}

private fun TaskAnswerFinalizationRule.displayText(): String {
    return when (this) {
        TaskAnswerFinalizationRule.FIRST -> "Первое решение"
        TaskAnswerFinalizationRule.LAST -> "Последнее решение"
        TaskAnswerFinalizationRule.CAPTAIN -> "Решение капитана"
        TaskAnswerFinalizationRule.MOST_VOTES -> "Большинство"
        TaskAnswerFinalizationRule.QUALIFIED_MAJORITY -> "2/3 голосов"
    }
}
