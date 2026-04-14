package com.example.hits_processes_2.feature.voting.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.feature.voting.presentation.components.VotingOptionCard
import com.example.hits_processes_2.ui.theme.Hitsprocesses2Theme

@Composable
fun VotingDialog(
    options: List<VotingOption>,
    selectedOptionId: Int?,
    onDismiss: () -> Unit,
    onSelectOption: (Int) -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Голосование") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Выберите одно решение",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                ) {
                    items(options, key = VotingOption::id) { option ->
                        VotingOptionCard(
                            option = option,
                            isSelected = option.id == selectedOptionId,
                            onClick = { onSelectOption(option.id) },
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = selectedOptionId != null,
            ) {
                Text("Подтвердить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
    )
}

@Preview(showBackground = true, widthDp = 440, heightDp = 900)
@Composable
private fun VotingDialogPreview() {
    Hitsprocesses2Theme {
        VotingDialog(
            options = previewVotingOptions,
            selectedOptionId = 2,
            onDismiss = {},
            onSelectOption = {},
            onConfirm = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 440, heightDp = 900)
@Composable
private fun VotingDialogNoSelectionPreview() {
    Hitsprocesses2Theme {
        VotingDialog(
            options = previewVotingOptions,
            selectedOptionId = null,
            onDismiss = {},
            onSelectOption = {},
            onConfirm = {},
        )
    }
}
