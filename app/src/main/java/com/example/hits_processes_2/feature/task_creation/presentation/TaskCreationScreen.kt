package com.example.hits_processes_2.feature.task_creation.presentation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskCreationScreen(
    onNavigateBack: () -> Unit,
) {
    val viewModel: TaskCreationViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                TaskCreationUiEffect.NavigateBack -> onNavigateBack()
                TaskCreationUiEffect.TaskCreated -> onNavigateBack()
                is TaskCreationUiEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Создание задания") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(TaskCreationUiEvent.BackClicked) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        TaskCreationContent(
            state = state,
            onEvent = viewModel::onEvent,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
private fun TaskCreationContent(
    state: TaskCreationUiState,
    onEvent: (TaskCreationUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        TaskTextField(
            value = state.taskText,
            onValueChange = { onEvent(TaskCreationUiEvent.TaskTextChanged(it)) },
        )

        DeadlineField(
            deadlineMillis = state.deadlineMillis,
            onDeadlineSelected = { onEvent(TaskCreationUiEvent.DeadlineSelected(it)) },
        )

        AttachedFilesSection(
            files = state.attachedFiles,
            onFilesSelected = { onEvent(TaskCreationUiEvent.FilesSelected(it)) },
            onFileRemoved = { onEvent(TaskCreationUiEvent.FileRemoved(it)) },
        )

        TeamFormationDropdown(
            selected = state.teamFormationRule,
            expanded = state.isTeamFormationDropdownExpanded,
            onToggle = { onEvent(TaskCreationUiEvent.TeamFormationDropdownToggled) },
            onSelect = { onEvent(TaskCreationUiEvent.TeamFormationRuleSelected(it)) },
        )

        TeamCountField(
            count = state.teamCount,
            onCountChange = { onEvent(TaskCreationUiEvent.TeamCountChanged(it)) },
        )

        SubmissionStrategyDropdown(
            selected = state.submissionStrategy,
            expanded = state.isSubmissionStrategyDropdownExpanded,
            onToggle = { onEvent(TaskCreationUiEvent.SubmissionStrategyDropdownToggled) },
            onSelect = { onEvent(TaskCreationUiEvent.SubmissionStrategySelected(it)) },
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onEvent(TaskCreationUiEvent.CreateTaskClicked) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = !state.isCreating,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
        ) {
            if (state.isCreating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(text = "Создать задание")
            }
        }
    }
}

@Composable
private fun fieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color(0xFFF2F2F2),
    unfocusedContainerColor = Color(0xFFF2F2F2),
    disabledContainerColor = Color(0xFFF2F2F2),
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    disabledTextColor = MaterialTheme.colorScheme.onSurface,
    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
)

@Composable
private fun TaskTextField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    Column {
        Text(
            text = "Текст задания",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = { Text(text = "Опишите задание...") },
            maxLines = 5,
            shape = RoundedCornerShape(8.dp),
            colors = fieldColors(),
        )
    }
}

@Composable
private fun DeadlineField(
    deadlineMillis: Long?,
    onDeadlineSelected: (Long) -> Unit,
) {
    val context = LocalContext.current
    val displayText = deadlineMillis?.let {
        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(it)
    } ?: ""
    val calendar = remember { Calendar.getInstance() }

    fun showTimePicker(dateMillis: Long) {
        calendar.timeInMillis = dateMillis
        val now = Calendar.getInstance()
        val isToday = calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
            calendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)

        val minHour = if (isToday) now.get(Calendar.HOUR_OF_DAY) else 0
        val minMinute = if (isToday) now.get(Calendar.MINUTE) else 0

        TimePickerDialog(
            context,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                onDeadlineSelected(calendar.timeInMillis)
            },
            minHour,
            minMinute,
            true,
        ).show()
    }

    fun showDatePicker() {
        val now = Calendar.getInstance()
        val picker = DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                showTimePicker(calendar.timeInMillis)
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH),
        )
        picker.datePicker.minDate = System.currentTimeMillis()
        picker.show()
    }

    Column {
        Text(
            text = "Дедлайн",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = displayText,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "ДД.ММ.ГГГГ --:--") },
                readOnly = true,
                enabled = false,
                singleLine = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Выбрать дату",
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = fieldColors(),
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker() },
            )
        }
    }
}

@Composable
private fun AttachedFilesSection(
    files: List<AttachedFile>,
    onFilesSelected: (List<AttachedFile>) -> Unit,
    onFileRemoved: (Int) -> Unit,
) {
    val context = LocalContext.current
    val dashedBorderColor = MaterialTheme.colorScheme.outline

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
    ) { uris: List<Uri> ->
        val attachedFiles = uris.map { uri ->
            AttachedFile(name = resolveFileName(context, uri), uriString = uri.toString())
        }
        if (attachedFiles.isNotEmpty()) onFilesSelected(attachedFiles)
    }

    Column {
        Text(
            text = "Прикреплённые файлы",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp),
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRoundRect(
                        color = dashedBorderColor,
                        style = Stroke(
                            width = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f),
                        ),
                        cornerRadius = CornerRadius(8.dp.toPx()),
                    )
                }
                .padding(16.dp),
        ) {
            if (files.isEmpty()) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Загрузите файлы",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
                )
            } else {
                files.forEachIndexed { index, file ->
                    InputChip(
                        selected = false,
                        onClick = {},
                        label = { Text(text = file.name, maxLines = 1) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Удалить",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onFileRemoved(index) },
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedButton(
                onClick = { launcher.launch("*/*") },
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(text = "Выбрать файлы")
            }
        }
    }
}

@Composable
private fun TeamFormationDropdown(
    selected: TeamFormationRule?,
    expanded: Boolean,
    onToggle: () -> Unit,
    onSelect: (TeamFormationRule) -> Unit,
) {
    val density = LocalDensity.current
    var anchorWidthDp by remember { mutableIntStateOf(0) }

    Column {
        Text(
            text = "Правила формирования команд",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { anchorWidthDp = it.width },
        ) {
            TextField(
                value = selected?.title ?: "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Выберите правило") },
                readOnly = true,
                enabled = false,
                singleLine = true,
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = fieldColors(),
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(onClick = onToggle),
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onToggle,
                modifier = Modifier.width(with(density) { anchorWidthDp.toDp() }),
            ) {
                TeamFormationRule.entries.forEach { rule ->
                    DropdownMenuItem(
                        text = { Text(text = rule.title) },
                        onClick = { onSelect(rule) },
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamCountField(
    count: Int,
    onCountChange: (Int) -> Unit,
) {
    Column {
        Text(
            text = "Количество команд",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextField(
                value = count.toString(),
                onValueChange = { input -> input.toIntOrNull()?.let { onCountChange(it) } },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(8.dp),
                colors = fieldColors(),
            )
        }
    }
}

@Composable
private fun SubmissionStrategyDropdown(
    selected: SubmissionStrategy?,
    expanded: Boolean,
    onToggle: () -> Unit,
    onSelect: (SubmissionStrategy) -> Unit,
) {
    val density = LocalDensity.current
    var anchorWidthDp by remember { mutableIntStateOf(0) }

    Column {
        Text(
            text = "Стратегия сдачи",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { anchorWidthDp = it.width },
        ) {
            TextField(
                value = selected?.title ?: "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Выберите стратегию") },
                readOnly = true,
                enabled = false,
                singleLine = true,
                trailingIcon = {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = fieldColors(),
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(onClick = onToggle),
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onToggle,
                modifier = Modifier.width(with(density) { anchorWidthDp.toDp() }),
            ) {
                SubmissionStrategy.entries.forEach { strategy ->
                    DropdownMenuItem(
                        text = { Text(text = strategy.title) },
                        onClick = { onSelect(strategy) },
                    )
                }
            }
        }
    }
}

private fun resolveFileName(context: android.content.Context, uri: Uri): String {
    var name = uri.lastPathSegment ?: "файл"
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && cursor.moveToFirst()) {
            name = cursor.getString(nameIndex)
        }
    }
    return name
}
