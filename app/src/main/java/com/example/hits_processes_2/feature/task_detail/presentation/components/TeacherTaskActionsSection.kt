package com.example.hits_processes_2.feature.task_detail.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.R
import com.example.hits_processes_2.common.ui.component.FormPrimaryButton

@Composable
fun TeacherTaskActionsSection(
    onTeamsClicked: () -> Unit,
    showCaptainSelectionAction: Boolean,
    onCaptainSelectionClicked: () -> Unit,
    onEditClicked: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FormPrimaryButton(
            text = stringResource(R.string.task_detail_teams_button),
            onClick = onTeamsClicked,
        )

        if (showCaptainSelectionAction) {
            FormPrimaryButton(
                text = "Выбрать капитанов",
                onClick = onCaptainSelectionClicked,
            )
        }

        Button(
            onClick = onEditClicked,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            androidx.compose.material3.Text(text = stringResource(R.string.task_detail_edit_button))
        }
    }
}
