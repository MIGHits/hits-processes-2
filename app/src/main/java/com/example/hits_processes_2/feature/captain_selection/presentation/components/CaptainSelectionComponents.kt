package com.example.hits_processes_2.feature.captain_selection.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.feature.captain_selection.presentation.CaptainCandidate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptainSelectionTopBar(
    onNavigateBack: () -> Unit,
) {
    TopAppBar(
        title = { Text("Выбор капитанов") },
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

@Composable
fun CaptainCandidateItem(
    candidate: CaptainCandidate,
    onAssignCaptain: (String) -> Unit,
    onRemoveCaptain: (CaptainCandidate) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = candidate.fullName,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
            )

            if (candidate.isCaptain) {
                OutlinedButton(
                    onClick = { onRemoveCaptain(candidate) },
                    enabled = enabled,
                ) {
                    Text("Разжаловать")
                }
            } else {
                Button(
                    onClick = { onAssignCaptain(candidate.id) },
                    enabled = enabled,
                ) {
                    Text("Назначить капитаном")
                }
            }
        }
    }
}
