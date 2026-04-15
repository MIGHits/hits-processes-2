package com.example.hits_processes_2.feature.task_detail.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.hits_processes_2.R
import com.example.hits_processes_2.common.ui.component.FormPrimaryButton

@Composable
fun StudentDraftActionsSection(
    onTeamsClicked: () -> Unit,
) {
    FormPrimaryButton(
        text = stringResource(R.string.task_detail_teams_button),
        onClick = onTeamsClicked,
    )
}
