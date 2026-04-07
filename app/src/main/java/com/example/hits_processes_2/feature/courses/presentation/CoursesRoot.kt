package com.example.hits_processes_2.feature.courses.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.hits_processes_2.feature.courses.domain.model.CourseRole
import com.example.hits_processes_2.feature.courses.domain.model.CourseShort
import org.koin.androidx.compose.koinViewModel

@Composable
fun CoursesRoot(
    onCourseClick: (String) -> Unit,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: CoursesViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val createDialogState by viewModel.createDialogState.collectAsState()
    val joinDialogState by viewModel.joinDialogState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            onLoggedOut()
        }
    }

    CoursesRootContent(
        state = state,
        createDialogState = createDialogState,
        joinDialogState = joinDialogState,
        onCourseClick = onCourseClick,
        onProfileClick = {},
        onRefresh = viewModel::refresh,
        onLogoutAction = viewModel::logout,
        onCreateCourseClick = viewModel::openCreateCourseDialog,
        onDismissCreateDialog = viewModel::dismissCreateCourseDialog,
        onCourseNameChanged = viewModel::onCourseNameChanged,
        onCourseDescriptionChanged = viewModel::onCourseDescriptionChanged,
        onSubmitCreateCourse = viewModel::submitCreateCourse,
        onJoinCourseClick = viewModel::openJoinCourseDialog,
        onDismissJoinDialog = viewModel::dismissJoinCourseDialog,
        onJoinCodeChanged = viewModel::onJoinCodeChanged,
        onSubmitJoinCourse = viewModel::submitJoinCourse,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesRootContent(
    state: CoursesUiState,
    createDialogState: CreateCourseDialogState?,
    joinDialogState: JoinCourseDialogState?,
    onCourseClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onRefresh: () -> Unit,
    onLogoutAction: () -> Unit,
    onCreateCourseClick: () -> Unit,
    onDismissCreateDialog: () -> Unit,
    onCourseNameChanged: (String) -> Unit,
    onCourseDescriptionChanged: (String) -> Unit,
    onSubmitCreateCourse: () -> Unit,
    onJoinCourseClick: () -> Unit,
    onDismissJoinDialog: () -> Unit,
    onJoinCodeChanged: (String) -> Unit,
    onSubmitJoinCourse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showActionSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CoursesHeader(
                userName = state.userName,
                onProfileClick = onProfileClick,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showActionSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Действия с курсами",
                )
            }
        },
    ) { paddingValues ->
        when {
            state.isLoading -> CoursesLoading(modifier = Modifier.padding(paddingValues))
            state.errorMessage != null -> CoursesError(
                message = state.errorMessage,
                onRetry = onRefresh,
                modifier = Modifier.padding(paddingValues),
            )
            state.courses.isEmpty() -> CoursesEmpty(
                modifier = Modifier.padding(paddingValues),
                onCreateCourseClick = onCreateCourseClick,
                onJoinCourseClick = onJoinCourseClick,
            )
            else -> CoursesListContent(
                courses = state.courses,
                onCourseClick = onCourseClick,
                paddingValues = paddingValues,
            )
        }
    }

    if (showActionSheet) {
        ModalBottomSheet(
            onDismissRequest = { showActionSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        ) {
            CoursesActionSheet(
                onJoinCourseClick = {
                    showActionSheet = false
                    onJoinCourseClick()
                },
                onCreateCourseClick = {
                    showActionSheet = false
                    onCreateCourseClick()
                },
                onLogoutClick = {
                    showActionSheet = false
                    onLogoutAction()
                },
            )
        }
    }

    if (createDialogState != null) {
        CreateCourseDialog(
            dialogState = createDialogState,
            onDismiss = onDismissCreateDialog,
            onNameChanged = onCourseNameChanged,
            onDescriptionChanged = onCourseDescriptionChanged,
            onConfirm = onSubmitCreateCourse,
        )
    }

    if (joinDialogState != null) {
        JoinCourseDialog(
            dialogState = joinDialogState,
            onDismiss = onDismissJoinDialog,
            onCodeChanged = onJoinCodeChanged,
            onConfirm = onSubmitJoinCourse,
        )
    }
}

@Composable
private fun CoursesHeader(
    userName: String,
    onProfileClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Курсы",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = userName.ifBlank { "Главный экран" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            UserProfileBadge(
                userName = userName,
                onClick = onProfileClick,
            )
        }
    }
}

@Composable
private fun UserProfileBadge(
    userName: String,
    onClick: () -> Unit,
) {
    val initials = userName.split(" ")
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .take(2)
        .joinToString("")
        .ifBlank { "P" }

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                color = Color(0xFFE8A048),
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (userName.isBlank()) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Профиль",
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        } else {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun CoursesActionSheet(
    onJoinCourseClick: () -> Unit,
    onCreateCourseClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 16.dp),
    ) {
        HorizontalDivider()

        CoursesActionSheetItem(
            icon = Icons.Default.GroupAdd,
            text = "Присоединиться к курсу",
            onClick = onJoinCourseClick,
        )

        CoursesActionSheetItem(
            icon = Icons.Default.PostAdd,
            text = "Создать курс",
            onClick = onCreateCourseClick,
        )

        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

        CoursesActionSheetItem(
            icon = Icons.AutoMirrored.Filled.Logout,
            text = "Выйти из аккаунта",
            onClick = onLogoutClick,
            tint = MaterialTheme.colorScheme.error,
            textColor = MaterialTheme.colorScheme.error,
        )
    }
}

@Composable
private fun CoursesActionSheetItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
        )
    }
}

@Composable
private fun CoursesListContent(
    courses: List<CourseShort>,
    onCourseClick: (String) -> Unit,
    paddingValues: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(courses, key = { it.id }) { course ->
            CourseListCard(
                course = course,
                onClick = { onCourseClick(course.id) },
            )
        }
    }
}

@Composable
private fun CourseListCard(
    course: CourseShort,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
        ) {
            Text(
                text = course.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = course.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = course.currentUserRole.title(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun CreateCourseDialog(
    dialogState: CreateCourseDialogState,
    onDismiss: () -> Unit,
    onNameChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Создать курс",
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = dialogState.name,
                    onValueChange = onNameChanged,
                    label = { Text("Название курса") },
                    singleLine = true,
                    enabled = !dialogState.isSubmitting,
                    isError = dialogState.errorMessage != null && dialogState.name.trim().length < 3,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = dialogState.description,
                    onValueChange = onDescriptionChanged,
                    label = { Text("Описание курса") },
                    minLines = 2,
                    maxLines = 4,
                    enabled = !dialogState.isSubmitting,
                    isError = dialogState.errorMessage != null && dialogState.description.trim().length < 3,
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
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !dialogState.isSubmitting,
            ) {
                Text("Создать")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !dialogState.isSubmitting,
            ) {
                Text("Отмена")
            }
        },
    )
}

@Composable
private fun JoinCourseDialog(
    dialogState: JoinCourseDialogState,
    onDismiss: () -> Unit,
    onCodeChanged: (String) -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Присоединиться к курсу",
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = dialogState.code,
                    onValueChange = onCodeChanged,
                    label = { Text("Код курса") },
                    singleLine = true,
                    enabled = !dialogState.isSubmitting,
                    isError = dialogState.errorMessage != null,
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
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !dialogState.isSubmitting,
            ) {
                Text("Вступить")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !dialogState.isSubmitting,
            ) {
                Text("Отмена")
            }
        },
    )
}

@Composable
private fun CoursesLoading(
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
private fun CoursesError(
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
                text = "Не удалось загрузить курсы",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
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
private fun CoursesEmpty(
    onCreateCourseClick: () -> Unit,
    onJoinCourseClick: () -> Unit,
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
                text = "Пока нет курсов",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Создай новый курс или вступи по коду, чтобы начать работу.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onCreateCourseClick) {
                    Text("Создать курс")
                }
                TextButton(onClick = onJoinCourseClick) {
                    Text("Вступить")
                }
            }
        }
    }
}

private fun CourseRole.title(): String = when (this) {
    CourseRole.STUDENT -> "Студент"
    CourseRole.TEACHER -> "Преподаватель"
    CourseRole.HEAD_TEACHER -> "Зав. курсом"
}

@Preview(showBackground = true)
@Composable
private fun CoursesRootPreview() {
    CoursesRootContent(
        state = CoursesUiState(
            courses = listOf(
                CourseShort(
                    id = "1",
                    name = "Android Development",
                    description = "Изучение разработки Android приложений",
                    currentUserRole = CourseRole.STUDENT,
                ),
                CourseShort(
                    id = "2",
                    name = "Kotlin Basics",
                    description = "Основы языка Kotlin",
                    currentUserRole = CourseRole.HEAD_TEACHER,
                ),
            ),
            userName = "Иван Петров",
        ),
        createDialogState = null,
        joinDialogState = null,
        onCourseClick = {},
        onProfileClick = {},
        onRefresh = {},
        onLogoutAction = {},
        onCreateCourseClick = {},
        onDismissCreateDialog = {},
        onCourseNameChanged = {},
        onCourseDescriptionChanged = {},
        onSubmitCreateCourse = {},
        onJoinCourseClick = {},
        onDismissJoinDialog = {},
        onJoinCodeChanged = {},
        onSubmitJoinCourse = {},
    )
}
