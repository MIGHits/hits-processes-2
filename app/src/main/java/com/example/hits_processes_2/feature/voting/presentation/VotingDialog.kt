package com.example.hits_processes_2.feature.voting.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.feature.voting.presentation.components.VotingOptionCard
import com.example.hits_processes_2.ui.theme.Hitsprocesses2Theme

@Composable
fun VotingDialog(
    options: List<VotingOption>,
    selectedOptionId: String?,
    isLoading: Boolean = false,
    title: String,
    description: String,
    loadingText: String,
    showVotesCount: Boolean = true,
    onDismiss: () -> Unit,
    onSelectOption: (String) -> Unit,
    onDownloadSolution: (String) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(title)
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "\u0417\u0430\u043a\u0440\u044b\u0442\u044c",
                    )
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (isLoading) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = loadingText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

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
                            showVotesCount = showVotesCount,
                            onDownloadClick = onDownloadSolution,
                            onClick = {
                                if (!isLoading) {
                                    onSelectOption(option.id)
                                }
                            },
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {},
    )
}

@Preview(showBackground = true, widthDp = 440, heightDp = 900)
@Composable
private fun VotingDialogPreview() {
    Hitsprocesses2Theme {
        VotingDialog(
            options = previewVotingOptions,
            selectedOptionId = "2",
            title = "\u0413\u043e\u043b\u043e\u0441\u043e\u0432\u0430\u043d\u0438\u0435",
            description = "\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u043d\u0430 \u0440\u0435\u0448\u0435\u043d\u0438\u0435, \u0447\u0442\u043e\u0431\u044b \u043f\u0440\u043e\u0433\u043e\u043b\u043e\u0441\u043e\u0432\u0430\u0442\u044c",
            loadingText = "\u041e\u0442\u043f\u0440\u0430\u0432\u043b\u044f\u0435\u043c \u0433\u043e\u043b\u043e\u0441...",
            onDismiss = {},
            onSelectOption = {},
            onDownloadSolution = {},
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
            title = "\u0413\u043e\u043b\u043e\u0441\u043e\u0432\u0430\u043d\u0438\u0435",
            description = "\u041d\u0430\u0436\u043c\u0438\u0442\u0435 \u043d\u0430 \u0440\u0435\u0448\u0435\u043d\u0438\u0435, \u0447\u0442\u043e\u0431\u044b \u043f\u0440\u043e\u0433\u043e\u043b\u043e\u0441\u043e\u0432\u0430\u0442\u044c",
            loadingText = "\u041e\u0442\u043f\u0440\u0430\u0432\u043b\u044f\u0435\u043c \u0433\u043e\u043b\u043e\u0441...",
            onDismiss = {},
            onSelectOption = {},
            onDownloadSolution = {},
        )
    }
}
