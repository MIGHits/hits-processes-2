package com.example.hits_processes_2.feature.voting.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.feature.voting.presentation.components.VotingOptionCard
import com.example.hits_processes_2.ui.theme.Hitsprocesses2Theme

@Composable
fun VotingScreen(
    options: List<VotingOption>,
    selectedOptionId: String?,
    onNavigateBack: () -> Unit,
    onSelectOption: (String) -> Unit,
    onDownloadSolution: (String) -> Unit,
    onConfirmVote: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            VotingTopBar(onNavigateBack = onNavigateBack)
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(options, key = VotingOption::id) { option ->
                VotingOptionCard(
                    option = option,
                    isSelected = option.id == selectedOptionId,
                    onDownloadClick = onDownloadSolution,
                    onClick = { onSelectOption(option.id) },
                )
            }

            item {
                Button(
                    onClick = onConfirmVote,
                    enabled = selectedOptionId != null,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Подтвердить выбор")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VotingTopBar(
    onNavigateBack: () -> Unit,
) {
    TopAppBar(
        title = { Text("Голосование") },
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

@Preview(showBackground = true, widthDp = 420, heightDp = 860)
@Composable
private fun VotingScreenPreview() {
    Hitsprocesses2Theme {
        VotingScreen(
            options = previewVotingOptions,
            selectedOptionId = "2",
            onNavigateBack = {},
            onSelectOption = {},
            onDownloadSolution = {},
            onConfirmVote = {},
        )
    }
}
