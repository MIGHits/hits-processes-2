package com.example.hits_processes_2.feature.draft.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.feature.draft.presentation.DraftStudent
import com.example.hits_processes_2.feature.draft.presentation.DraftTeamMember
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DraftTopBar(
    onNavigateBack: () -> Unit,
) {
    TopAppBar(
        title = { Text("Драфт") },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Назад",
                )
            }
        },
    )
}

private fun Int.formatTimer(): String {
    return "${this / 60}:${(this % 60).toString().padStart(length = 2, padChar = '0')}"
}

@Composable
fun DraftTeamCard(
    teamNumber: Int,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = "Команда $teamNumber",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun DraftMemberItem(
    member: DraftTeamMember,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = member.fullName,
                style = MaterialTheme.typography.bodyMedium,
            )
            if (member.isCaptain) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                ) {
                    Text(
                        text = "Капитан",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

@Composable
fun DraftPickDialog(
    students: List<DraftStudent>,
    timerSeconds: Int,
    timerKey: Int,
    onDismiss: () -> Unit,
    onSelectStudent: (String) -> Unit,
) {
    val initialSeconds = timerSeconds.coerceAtLeast(0)
    var remainingSeconds by remember(timerKey, initialSeconds) {
        mutableIntStateOf(initialSeconds)
    }

    LaunchedEffect(timerKey, initialSeconds) {
        remainingSeconds = initialSeconds
        while (remainingSeconds > 0) {
            delay(1_000)
            remainingSeconds -= 1
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите студента") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(
                        text = "Автовыбор через ${remainingSeconds.formatTimer()}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                    )
                }

                if (students.isEmpty()) {
                    Text("Свободных студентов больше нет")
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(students, key = DraftStudent::id) { student ->
                            OutlinedCard(
                                onClick = { onSelectStudent(student.id) },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    text = student.fullName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
    )
}
