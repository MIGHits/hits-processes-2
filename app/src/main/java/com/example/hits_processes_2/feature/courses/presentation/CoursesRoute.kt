package com.example.hits_processes_2.feature.courses.presentation

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.feature.courses.domain.model.CourseRole
import com.example.hits_processes_2.feature.courses.domain.model.CourseShort
import org.koin.androidx.compose.koinViewModel

@Composable
fun CoursesRoute(
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: CoursesViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            onLoggedOut()
        }
    }

    CoursesContent(
        state = state,
        onCourseClick = {},
        onRetry = viewModel::loadCourses,
        onLogout = viewModel::logout,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CoursesContent(
    state: CoursesUiState,
    onCourseClick: (String) -> Unit,
    onRetry: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Курсы",
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            text = "Список ваших курсов",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Выйти",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        when {
            state.isLoading -> LoadingState(modifier = Modifier.padding(paddingValues))
            state.errorMessage != null -> ErrorState(
                message = state.errorMessage,
                onRetry = onRetry,
                modifier = Modifier.padding(paddingValues),
            )
            state.courses.isEmpty() -> EmptyState(modifier = Modifier.padding(paddingValues))
            else -> CoursesList(
                courses = state.courses,
                onCourseClick = onCourseClick,
                paddingValues = paddingValues,
            )
        }
    }
}

@Composable
private fun CoursesList(
    courses: List<CourseShort>,
    onCourseClick: (String) -> Unit,
    paddingValues: PaddingValues,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(courses, key = { it.id }) { course ->
            CourseListItem(
                course = course,
                onClick = { onCourseClick(course.id) },
            )
        }
    }
}

@Composable
private fun CourseListItem(
    course: CourseShort,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = course.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.width(8.dp))
                CourseRoleChip(role = course.currentUserRole)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = course.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun CourseRoleChip(
    role: CourseRole,
) {
    val label = when (role) {
        CourseRole.STUDENT -> "Студент"
        CourseRole.TEACHER -> "Преподаватель"
        CourseRole.HEAD_TEACHER -> "Зав. курсом"
    }

    val colors = when (role) {
        CourseRole.STUDENT -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
        )
        CourseRole.TEACHER, CourseRole.HEAD_TEACHER -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }

    AssistChip(
        onClick = {},
        label = { Text(label) },
        colors = colors,
        enabled = false,
    )
}

@Composable
private fun LoadingState(
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
                text = "Не удалось загрузить курсы",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TextButton(onClick = onRetry) {
                Text("Повторить")
            }
        }
    }
}

@Composable
private fun EmptyState(
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
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Пока нет курсов",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Когда курсы появятся, они будут показаны на этом экране.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CoursesContentPreview() {
    CoursesContent(
        state = CoursesUiState(
            courses = listOf(
                CourseShort(
                    id = "course-1",
                    name = "Android Development",
                    description = "Изучение разработки Android приложений",
                    currentUserRole = CourseRole.STUDENT,
                ),
                CourseShort(
                    id = "course-2",
                    name = "Kotlin Basics",
                    description = "Основы языка Kotlin",
                    currentUserRole = CourseRole.HEAD_TEACHER,
                ),
            ),
        ),
        onCourseClick = {},
        onRetry = {},
        onLogout = {},
    )
}
