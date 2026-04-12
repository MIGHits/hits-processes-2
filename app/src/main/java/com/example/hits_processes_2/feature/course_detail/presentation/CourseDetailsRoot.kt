package com.example.hits_processes_2.feature.course_detail.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.common.ui.component.MainFloatingActionButton
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseDetailsRole
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseParticipant
import com.example.hits_processes_2.feature.course_detail.domain.model.CourseTask
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CourseDetailsRoot(
    courseId: String,
    onNavigateBack: () -> Unit,
    onCreateTask: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: CourseDetailsViewModel = koinViewModel(parameters = { parametersOf(courseId) })
    val state by viewModel.state.collectAsState()
    val editDialogState by viewModel.editDialogState.collectAsState()

    CourseDetailsContent(
        state = state,
        editDialogState = editDialogState,
        onNavigateBack = onNavigateBack,
        onRetry = viewModel::refresh,
        onEditCourseClick = viewModel::openEditCourseDialog,
        onDismissEditDialog = viewModel::dismissEditCourseDialog,
        onEditCourseNameChanged = viewModel::onEditCourseNameChanged,
        onEditCourseDescriptionChanged = viewModel::onEditCourseDescriptionChanged,
        onSubmitEditCourse = viewModel::submitCourseEdit,
        onTaskClick = {},
        onCreateTask = onCreateTask,
        onPromoteParticipant = viewModel::promoteParticipant,
        onDemoteParticipant = viewModel::demoteParticipant,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailsContent(
    state: CourseDetailsUiState,
    editDialogState: CourseEditDialogState?,
    onNavigateBack: () -> Unit,
    onRetry: () -> Unit,
    onEditCourseClick: () -> Unit,
    onDismissEditDialog: () -> Unit,
    onEditCourseNameChanged: (String) -> Unit,
    onEditCourseDescriptionChanged: (String) -> Unit,
    onSubmitEditCourse: () -> Unit,
    onTaskClick: (String) -> Unit,
    onCreateTask: () -> Unit,
    onPromoteParticipant: (CourseParticipant) -> Unit,
    onDemoteParticipant: (CourseParticipant) -> Unit,
    modifier: Modifier = Modifier,
) {
    val course = state.course
    var selectedTabIndex by rememberSaveable { androidx.compose.runtime.mutableIntStateOf(0) }
    val isTeacher = course?.currentUserRole == CourseDetailsRole.TEACHER ||
        course?.currentUserRole == CourseDetailsRole.HEAD_TEACHER

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = course?.name ?: "Курс",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад",
                        )
                    }
                },
                actions = {
                    if (course?.currentUserRole == CourseDetailsRole.HEAD_TEACHER) {
                        IconButton(onClick = onEditCourseClick) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Редактировать курс",
                            )
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            if (isTeacher && selectedTabIndex == 0 && !state.isLoading) {
                MainFloatingActionButton(
                    onClick = onCreateTask,
                    icon = Icons.Default.Add,
                )
            }
        },
    ) { paddingValues ->
        when {
            state.isLoading -> CenterLoading(modifier = Modifier.padding(paddingValues))
            course == null -> ErrorState(
                message = state.errorMessage ?: "Курс не найден",
                onRetry = onRetry,
                modifier = Modifier.padding(paddingValues),
            )
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                CourseSummaryCard(course = course)

                val tabs = listOf("Задания", "Участники")
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        if (selectedTabIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    },
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) },
                        )
                    }
                }

                if (state.errorMessage != null && !state.isLoading) {
                    Text(
                        text = state.errorMessage,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                when (selectedTabIndex) {
                    0 -> CourseTasksTab(
                        tasks = state.tasks,
                        onTaskClick = onTaskClick,
                        modifier = Modifier.weight(1f),
                    )
                    else -> CourseParticipantsTab(
                        teachers = state.teachers,
                        students = state.students,
                        currentUserRole = course.currentUserRole,
                        isRefreshingRoles = state.isRefreshingRoles,
                        onPromoteParticipant = onPromoteParticipant,
                        onDemoteParticipant = onDemoteParticipant,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }

    if (editDialogState != null) {
        EditCourseDialog(
            dialogState = editDialogState,
            onDismiss = onDismissEditDialog,
            onNameChanged = onEditCourseNameChanged,
            onDescriptionChanged = onEditCourseDescriptionChanged,
            onConfirm = onSubmitEditCourse,
        )
    }
}

@Composable
private fun CourseSummaryCard(
    course: com.example.hits_processes_2.feature.course_detail.domain.model.CourseDetails,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = course.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            HorizontalDivider()
            SummaryLine(label = "Роль", value = course.currentUserRole.title())
            if (!course.joinCode.isNullOrBlank()) {
                SummaryLine(
                    label = "Код курса",
                    value = course.joinCode,
                    valueFontFamily = FontFamily.Monospace,
                )
            }
        }
    }
}

@Composable
private fun SummaryLine(
    label: String,
    value: String,
    valueFontFamily: FontFamily? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = valueFontFamily,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun CourseTasksTab(
    tasks: List<CourseTask>,
    onTaskClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (tasks.isEmpty()) {
        EmptyBox(
            message = "Пока в этом курсе нет заданий",
            modifier = modifier,
        )
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(tasks, key = { it.id }) { task ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp)
                        .clickable { onTaskClick(task.id) }
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = task.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun CourseParticipantsTab(
    teachers: List<CourseParticipant>,
    students: List<CourseParticipant>,
    currentUserRole: CourseDetailsRole,
    isRefreshingRoles: Boolean,
    onPromoteParticipant: (CourseParticipant) -> Unit,
    onDemoteParticipant: (CourseParticipant) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ParticipantSection(
                title = "Преподаватели",
                participants = teachers,
                currentUserRole = currentUserRole,
                isRefreshingRoles = isRefreshingRoles,
                onPromoteParticipant = onPromoteParticipant,
                onDemoteParticipant = onDemoteParticipant,
            )
        }

        item {
            ParticipantSection(
                title = "Студенты",
                participants = students,
                currentUserRole = currentUserRole,
                isRefreshingRoles = isRefreshingRoles,
                onPromoteParticipant = onPromoteParticipant,
                onDemoteParticipant = onDemoteParticipant,
            )
        }
    }
}

@Composable
private fun ParticipantSection(
    title: String,
    participants: List<CourseParticipant>,
    currentUserRole: CourseDetailsRole,
    isRefreshingRoles: Boolean,
    onPromoteParticipant: (CourseParticipant) -> Unit,
    onDemoteParticipant: (CourseParticipant) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            if (participants.isEmpty()) {
                Text(
                    text = "Список пуст",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                participants.forEach { participant ->
                    ParticipantRow(
                        participant = participant,
                        currentUserRole = currentUserRole,
                        isRefreshingRoles = isRefreshingRoles,
                        onPromoteParticipant = onPromoteParticipant,
                        onDemoteParticipant = onDemoteParticipant,
                    )
                }
            }
        }
    }
}

@Composable
private fun ParticipantRow(
    participant: CourseParticipant,
    currentUserRole: CourseDetailsRole,
    isRefreshingRoles: Boolean,
    onPromoteParticipant: (CourseParticipant) -> Unit,
    onDemoteParticipant: (CourseParticipant) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = participant.fullName,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = participant.email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = participant.role.title(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        if (currentUserRole == CourseDetailsRole.HEAD_TEACHER && !isRefreshingRoles) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (participant.role != CourseDetailsRole.HEAD_TEACHER) {
                    IconButton(onClick = { onPromoteParticipant(participant) }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Повысить",
                        )
                    }
                }
                if (participant.role != CourseDetailsRole.STUDENT) {
                    IconButton(onClick = { onDemoteParticipant(participant) }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Понизить",
                        )
                    }
                }
            }
        } else if (isRefreshingRoles) {
            CircularProgressIndicator(modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
private fun EditCourseDialog(
    dialogState: CourseEditDialogState,
    onDismiss: () -> Unit,
    onNameChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактировать курс") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = dialogState.name,
                    onValueChange = onNameChanged,
                    label = { Text("Название курса") },
                    singleLine = true,
                    enabled = !dialogState.isSubmitting,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = dialogState.description,
                    onValueChange = onDescriptionChanged,
                    label = { Text("Описание курса") },
                    minLines = 2,
                    maxLines = 4,
                    enabled = !dialogState.isSubmitting,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (dialogState.errorMessage != null) {
                    Text(
                        text = dialogState.errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                if (dialogState.isSubmitting) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !dialogState.isSubmitting) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !dialogState.isSubmitting) {
                Text("Отмена")
            }
        },
    )
}

@Composable
private fun CenterLoading(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
            TextButton(onClick = onRetry) {
                Text("Повторить")
            }
        }
    }
}

@Composable
private fun EmptyBox(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

private fun CourseDetailsRole.title(): String = when (this) {
    CourseDetailsRole.STUDENT -> "Студент"
    CourseDetailsRole.TEACHER -> "Преподаватель"
    CourseDetailsRole.HEAD_TEACHER -> "Зав. курсом"
}
