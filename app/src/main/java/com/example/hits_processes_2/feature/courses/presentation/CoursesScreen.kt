package com.example.hits_processes_2.feature.courses.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.models.Course
import com.example.hits_processes_2.models.UserRole
import com.example.hits_processes_2.common.ui.component.ActionSheetItem
import com.example.hits_processes_2.common.ui.component.CourseCard
import com.example.hits_processes_2.common.ui.component.CoursesTopAppBar
import com.example.hits_processes_2.common.ui.component.MainFloatingActionButton

/**
 * Courses list screen with floating action button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(
	courses: List<Course>,
	userName: String,
	onCourseClick: (Int) -> Unit,
	onProfileClick: () -> Unit,
	onJoinCourse: () -> Unit,
	onCreateCourse: () -> Unit,
	onLogout: () -> Unit,
	modifier: Modifier = Modifier
) {
	var showBottomSheet by remember { mutableStateOf(false) }
	val sheetState = rememberModalBottomSheetState()

	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			CoursesTopAppBar(
				userName = userName,
				onProfileClick = onProfileClick
			)
		},
		floatingActionButton = {
			MainFloatingActionButton(
				onClick = { showBottomSheet = true },
				icon = Icons.Default.Add
			)
		}
	) { paddingValues ->
		LazyColumn(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
				.padding(horizontal = 16.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
			contentPadding = PaddingValues(vertical = 16.dp)
		) {
			items(courses) { course ->
				CourseCard(
					courseName = course.name,
					courseDescription = course.description,
					userRole = course.userRole,
					onClick = { onCourseClick(course.id) }
				)
			}
		}
	}

	// Bottom Sheet for Actions
	if (showBottomSheet) {
		ModalBottomSheet(
			onDismissRequest = { showBottomSheet = false },
			sheetState = sheetState
		) {
			Column(
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 32.dp)
			) {
				Text(
					text = "Действия",
					style = MaterialTheme.typography.titleLarge,
					modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
				)

				ActionSheetItem(
					text = "Присоединиться к курсу",
					icon = Icons.Default.PersonAdd,
					onClick = {
						showBottomSheet = false
						onJoinCourse()
					}
				)

				ActionSheetItem(
					text = "Создать курс",
					icon = Icons.Default.BookmarkAdd,
					onClick = {
						showBottomSheet = false
						onCreateCourse()
					}
				)

				ActionSheetItem(
					text = "Выйти из аккаунта",
					icon = Icons.Default.ExitToApp,
					onClick = {
						showBottomSheet = false
						onLogout()
					},
					tint = MaterialTheme.colorScheme.error
				)
			}
		}
	}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CoursesScreenPreview() {
	val mockCourses = listOf(
		Course(
			id = 1,
			name = "Android Development",
			description = "Изучение разработки Android приложений",
			code = "AND123",
			userRole = UserRole.STUDENT
		),
		Course(
			id = 2,
			name = "Kotlin Basics",
			description = "Основы языка Kotlin",
			code = "KOT456",
			userRole = UserRole.TEACHER
		),
		Course(
			id = 3,
			name = "UI/UX Design",
			description = "Принципы дизайна интерфейсов",
			code = "UI789",
			userRole = UserRole.STUDENT
		)
	)

	MaterialTheme {
		CoursesScreen(
			courses = mockCourses,
			userName = "Иван",
			onCourseClick = {},
			onProfileClick = {},
			onJoinCourse = {},
			onCreateCourse = {},
			onLogout = {}
		)
	}
}