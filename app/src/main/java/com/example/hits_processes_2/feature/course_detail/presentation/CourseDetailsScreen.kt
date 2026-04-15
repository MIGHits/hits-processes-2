package com.example.hits_processes_2.feature.course_detail.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.common.ui.component.AssignmentCard
import com.example.hits_processes_2.common.ui.component.ClassroomTopAppBar
import com.example.hits_processes_2.common.ui.component.InfoCard
import com.example.hits_processes_2.common.ui.component.MainFloatingActionButton
import com.example.hits_processes_2.common.ui.component.ParticipantItem
import com.example.hits_processes_2.common.ui.component.SectionHeader
import com.example.hits_processes_2.common.ui.component.SpacedDivider
import com.example.hits_processes_2.models.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Course details screen with feed and participants tabs
 */
@Composable
fun CourseDetailsScreen(
	course: Course,
	assignments: List<Assignment>,
	teachers: List<Participant>,
	students: List<Participant>,
	userRole: UserRole,
	onNavigateBack: () -> Unit,
	onEditCourse: () -> Unit,
	onLeaveCourse: () -> Unit,
	onAssignmentClick: (Int) -> Unit,
	onCreateAssignment: () -> Unit,
	onPromoteParticipant: (Int) -> Unit,
	onDemoteParticipant: (Int) -> Unit,
	modifier: Modifier = Modifier
) {
	var selectedTabIndex by remember { mutableIntStateOf(0) }
	val isMainTeacher = userRole == UserRole.MAIN_TEACHER
	val isTeacher = userRole == UserRole.TEACHER || isMainTeacher

	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			ClassroomTopAppBar(
				title = course.name,
				onNavigateBack = onNavigateBack,
				actions = {
					if (isMainTeacher) {
						IconButton(onClick = onEditCourse) {
							Icon(
								imageVector = Icons.Default.Edit,
								contentDescription = "Изменить"
							)
						}
					} else {
						IconButton(onClick = onLeaveCourse) {
							Icon(
								imageVector = Icons.Default.ExitToApp,
								contentDescription = "Выйти"
							)
						}
					}
				}
			)
		},
		floatingActionButton = {
			if (isTeacher && selectedTabIndex == 0) {
				MainFloatingActionButton(
					onClick = onCreateAssignment,
					icon = Icons.Default.Add
				)
			}
		}
	) { paddingValues ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
		) {
			// Course Description
			InfoCard(
				modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
			) {
				Text(
					text = course.description,
					style = MaterialTheme.typography.bodyMedium,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)

				if (isTeacher) {
					SpacedDivider()
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.SpaceBetween
					) {
						Text(
							text = "Код курса:",
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
						Text(
							text = course.code,
							style = MaterialTheme.typography.bodySmall,
							fontFamily = FontFamily.Monospace
						)
					}
				}
			}

			// Tabs
			TabRow(selectedTabIndex = selectedTabIndex) {
				Tab(
					selected = selectedTabIndex == 0,
					onClick = { selectedTabIndex = 0 },
					text = { Text("Лента") }
				)
				Tab(
					selected = selectedTabIndex == 1,
					onClick = { selectedTabIndex = 1 },
					text = { Text("Участники") }
				)
			}

			// Tab Content
			when (selectedTabIndex) {
				0 -> FeedTab(
					assignments = assignments,
					onAssignmentClick = onAssignmentClick
				)
				1 -> ParticipantsTab(
					teachers = teachers,
					students = students,
					isMainTeacher = isMainTeacher,
					onPromote = onPromoteParticipant,
					onDemote = onDemoteParticipant
				)
			}
		}
	}
}

@Composable
private fun FeedTab(
	assignments: List<Assignment>,
	onAssignmentClick: (Int) -> Unit
) {
	LazyColumn(
		modifier = Modifier.fillMaxSize(),
		contentPadding = PaddingValues(16.dp),
		verticalArrangement = Arrangement.spacedBy(12.dp)
	) {
		items(assignments) { assignment ->
			AssignmentCard(
				assignmentText = assignment.text,
				deadline = assignment.formattedDeadline,
				author = assignment.author,
				onClick = { onAssignmentClick(assignment.id) }
			)
		}
	}
}

@Composable
private fun ParticipantsTab(
	teachers: List<Participant>,
	students: List<Participant>,
	isMainTeacher: Boolean,
	onPromote: (Int) -> Unit,
	onDemote: (Int) -> Unit
) {
	LazyColumn(
		modifier = Modifier.fillMaxSize(),
		contentPadding = PaddingValues(16.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		// Teachers Section
		item {
			InfoCard {
				SectionHeader(title = "Преподаватели")

				Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
					teachers.forEach { teacher ->
						ParticipantItem(
							firstName = teacher.firstName,
							lastName = teacher.lastName,
							email = teacher.email,
							isMain = teacher.isMain,
							actions = {
								if (isMainTeacher && !teacher.isMain) {
									IconButton(onClick = { onPromote(teacher.id) }) {
										Icon(
											imageVector = Icons.Default.KeyboardArrowUp,
											contentDescription = "Повысить"
										)
									}
									if (false) IconButton(onClick = { onDemote(teacher.id) }) {
										Icon(
											imageVector = Icons.Default.KeyboardArrowDown,
											contentDescription = "Понизить"
										)
									}
								}
							}
						)
					}
				}
			}
		}

		// Students Section
		item {
			InfoCard {
				SectionHeader(title = "Студенты")

				Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
					students.forEach { student ->
						ParticipantItem(
							firstName = student.firstName,
							lastName = student.lastName,
							email = student.email,
							actions = {
								if (isMainTeacher) {
									IconButton(onClick = { onPromote(student.id) }) {
										Icon(
											imageVector = Icons.Default.KeyboardArrowUp,
											contentDescription = "Повысить"
										)
									}
									if (false) IconButton(onClick = { onDemote(student.id) }) {
										Icon(
											imageVector = Icons.Default.KeyboardArrowDown,
											contentDescription = "Понизить"
										)
									}
								}
							}
						)
					}
				}
			}
		}
	}
}

fun formatDeadline(date: LocalDateTime): String {
	val formatter = DateTimeFormatter.ofPattern(
		"d MMMM yyyy, HH:mm",
		Locale("ru")
	)
	return date.format(formatter)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CourseDetailsScreenPreview() {
	val course = Course(
		id = 1,
		name = "Android Development",
		description = "Курс по разработке Android приложений на Kotlin",
		code = "AND123",
		userRole = UserRole.MAIN_TEACHER
	)

	val assignments = listOf(
		Assignment(
			id = 1,
			text = "Сделать Todo приложение",
			deadline = LocalDateTime.now().plusDays(3),
			author = "Иван Иванов",
			teamFormation = TeamFormation.STUDENTS,
			teamCount = 2,
			submissionStrategy = SubmissionStrategy.FIRST
		),
		Assignment(
			id = 2,
			text = "Реализовать экран профиля",
			deadline = LocalDateTime.now().plusDays(7),
			author = "Петр Петров",
			teamFormation = TeamFormation.RANDOM,
			teamCount = 3,
			submissionStrategy = SubmissionStrategy.LAST
		)
	)

	val teachers = listOf(
		Participant(
			id = 1,
			firstName = "Иван",
			lastName = "Иванов",
			email = "ivan@example.com",
			isMain = true
		),
		Participant(
			id = 2,
			firstName = "Петр",
			lastName = "Петров",
			email = "petr@example.com"
		)
	)

	val students = listOf(
		Participant(
			id = 3,
			firstName = "Анна",
			lastName = "Смирнова",
			email = "anna@example.com"
		),
		Participant(
			id = 4,
			firstName = "Дмитрий",
			lastName = "Кузнецов",
			email = "dmitry@example.com"
		)
	)

	MaterialTheme {
		CourseDetailsScreen(
			course = course,
			assignments = assignments,
			teachers = teachers,
			students = students,
			userRole = UserRole.MAIN_TEACHER,
			onNavigateBack = {},
			onEditCourse = {},
			onLeaveCourse = {},
			onAssignmentClick = {},
			onCreateAssignment = {},
			onPromoteParticipant = {},
			onDemoteParticipant = {}
		)
	}
}
