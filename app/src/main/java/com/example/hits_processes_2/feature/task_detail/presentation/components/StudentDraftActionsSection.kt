package com.example.hits_processes_2.feature.task_detail.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.hits_processes_2.R

@Composable
fun StudentDraftActionsSection(
    onTeamsClicked: () -> Unit,
) {
    Button(
        onClick = onTeamsClicked,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = stringResource(R.string.task_detail_teams_button))
    }
}
