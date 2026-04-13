package com.example.hits_processes_2.feature.task_detail.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.R

@Composable
fun TeacherTaskActionsSection(
    onTeamsClicked: () -> Unit,
    onEvaluateClicked: () -> Unit,
    onEditClicked: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(
            onClick = onTeamsClicked,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.task_detail_teams_button))
        }

        Button(
            onClick = onEvaluateClicked,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.task_detail_evaluate_button))
        }

        OutlinedButton(
            onClick = onEditClicked,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(R.string.task_detail_edit_button))
        }
    }
}
