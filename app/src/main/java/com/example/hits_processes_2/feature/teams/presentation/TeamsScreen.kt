package com.example.hits_processes_2.feature.teams.presentation

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.feature.teams.presentation.components.FileItem
import com.example.hits_processes_2.feature.teams.presentation.components.FilledActionIconButton
import com.example.hits_processes_2.feature.teams.presentation.components.LabeledText
import com.example.hits_processes_2.feature.teams.presentation.components.SectionDivider
import com.example.hits_processes_2.feature.teams.presentation.components.SubmissionStatusBadge
import com.example.hits_processes_2.feature.teams.presentation.components.TeamCard
import com.example.hits_processes_2.feature.teams.presentation.components.TeamMemberItem
import com.example.hits_processes_2.feature.teams.presentation.components.TeamSectionHeader
import com.example.hits_processes_2.feature.teams.presentation.components.TeamsSummaryCard
import com.example.hits_processes_2.feature.teams.presentation.components.TeamsTopBar
import com.example.hits_processes_2.ui.theme.Hitsprocesses2Theme

@Composable
fun TeamsScreen(
    teams: List<Team>,
    userRole: UserRole,
    teamFormation: TeamFormation,
    userTeamId: Int?,
    availableStudents: List<TeamMember>,
    onNavigateBack: () -> Unit,
    onJoinTeam: (Int) -> Unit,
    onLeaveTeam: () -> Unit,
    onAddStudent: (teamId: Int, studentId: Int) -> Unit,
    onRemoveStudent: (teamId: Int, studentId: Int) -> Unit,
    onSetCaptain: (teamId: Int, studentId: Int) -> Unit,
    onUpdateGrade: (teamId: Int, grade: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var selectedTeamId by remember { mutableIntStateOf(0) }
    val gradeInputs = remember { mutableStateMapOf<Int, String>() }

    val isStudent = userRole == UserRole.STUDENT
    val isTeacher = userRole == UserRole.TEACHER || userRole == UserRole.MAIN_TEACHER
    val canStudentJoin = isStudent && teamFormation == TeamFormation.STUDENTS
    val totalMembers = teams.sumOf { it.members.size }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TeamsTopBar(
                title = "Команды",
                onNavigateBack = onNavigateBack,
            )
        },
    ) { paddingValues ->
        if (teams.isEmpty()) {
            EmptyTeamsState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 24.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    TeamsSummaryCard(
                        teamsCount = teams.size,
                        membersCount = totalMembers,
                        userRoleLabel = userRole.title,
                        teamFormationLabel = teamFormation.title,
                    )
                }

                items(teams, key = Team::id) { team ->
                    TeamCard(teamNumber = team.number) {
                        TeamSectionHeader(
                            membersCount = team.members.size,
                            status = if (isTeacher) team.status else null,
                            trailingContent = if (isTeacher) {
                                {
                                    TeacherActionsRow(
                                        onAddStudentClick = {
                                            selectedTeamId = team.id
                                            showAddStudentDialog = true
                                        },
                                    )
                                }
                            } else {
                                null
                            },
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            team.members.forEach { member ->
                                TeamMemberItem(
                                    memberName = member.fullName,
                                    isCaptain = member.isCaptain,
                                ) {
                                    if (isTeacher) {
                                        if (!member.isCaptain) {
                                            TextButton(
                                                onClick = { onSetCaptain(team.id, member.id) },
                                            ) {
                                                Text("Капитан")
                                            }
                                        }

                                        IconButton(
                                            onClick = { onRemoveStudent(team.id, member.id) },
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Удалить",
                                                tint = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (canStudentJoin) {
                            Spacer(modifier = Modifier.height(12.dp))
                            StudentTeamAction(
                                isCurrentUserTeam = userTeamId == team.id,
                                onJoinTeam = { onJoinTeam(team.id) },
                                onLeaveTeam = onLeaveTeam,
                            )
                        }

                        if (isTeacher) {
                            TeacherSubmissionSection(
                                team = team,
                                gradeInput = gradeInputs[team.id] ?: team.grade?.toString().orEmpty(),
                                onGradeInputChange = { gradeInputs[team.id] = it },
                                onSaveGradeClick = {
                                    gradeInputs[team.id]?.toIntOrNull()?.let { grade ->
                                        onUpdateGrade(team.id, grade)
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddStudentDialog) {
        AddStudentDialog(
            students = availableStudents,
            onDismiss = { showAddStudentDialog = false },
            onAddStudent = { studentId ->
                onAddStudent(selectedTeamId, studentId)
                showAddStudentDialog = false
            },
        )
    }
}

@Composable
private fun TeacherActionsRow(
    onAddStudentClick: () -> Unit,
) {
    FilledActionIconButton(onClick = onAddStudentClick) {
        Icon(
            imageVector = Icons.Default.PersonAdd,
            contentDescription = "Добавить студента",
        )
    }
}

@Composable
private fun StudentTeamAction(
    isCurrentUserTeam: Boolean,
    onJoinTeam: () -> Unit,
    onLeaveTeam: () -> Unit,
) {
    if (isCurrentUserTeam) {
        Button(
            onClick = onLeaveTeam,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
            ),
        ) {
            Text("Выйти из команды")
        }
    } else {
        Button(
            onClick = onJoinTeam,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Вступить")
        }
    }
}

@Composable
private fun TeacherSubmissionSection(
    team: Team,
    gradeInput: String,
    onGradeInputChange: (String) -> Unit,
    onSaveGradeClick: () -> Unit,
) {
    Spacer(modifier = Modifier.height(14.dp))
    SectionDivider()
    Spacer(modifier = Modifier.height(14.dp))

    LabeledText(
        label = "Решение команды",
        text = team.submission ?: "Не сдано",
    )

    if (team.submission != null) {
        Spacer(modifier = Modifier.height(8.dp))
        FileItem(fileName = team.submission)
    }

    if (team.submittedAt != null) {
        Spacer(modifier = Modifier.height(12.dp))
        LabeledText(
            label = "Момент сдачи",
            text = team.submittedAt,
        )
    }

    Spacer(modifier = Modifier.height(12.dp))
    LabeledText(
        label = "Статус",
        text = team.status.title,
    )
    Spacer(modifier = Modifier.height(8.dp))
    SubmissionStatusBadge(status = team.status)

    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Оценка",
        style = MaterialTheme.typography.titleSmall,
    )
    Spacer(modifier = Modifier.height(8.dp))

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = gradeInput,
            onValueChange = onGradeInputChange,
            label = { Text("Введите оценку") },
            modifier = Modifier.weight(1f),
            singleLine = true,
        )
        Button(onClick = onSaveGradeClick) {
            Text("Сохранить")
        }
    }
}

@Composable
private fun AddStudentDialog(
    students: List<TeamMember>,
    onDismiss: () -> Unit,
    onAddStudent: (Int) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить студента") },
        text = {
            if (students.isEmpty()) {
                Text("Нет доступных студентов")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(students, key = TeamMember::id) { student ->
                        OutlinedCard(
                            onClick = { onAddStudent(student.id) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                ) {
                                    Text(
                                        text = student.fullName,
                                        style = MaterialTheme.typography.titleSmall,
                                    )
                                    Text(
                                        text = "Свободный участник",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                                Spacer(modifier = Modifier.weight(0.05f))
                                Button(onClick = { onAddStudent(student.id) }) {
                                    Text("Добавить")
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
    )
}

@Composable
private fun EmptyTeamsState(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Group,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Команды пока не созданы",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Как только преподаватель сформирует команды, они появятся здесь.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 960)
@Composable
private fun TeamsScreenTeacherPreview() {
    Hitsprocesses2Theme {
        TeamsScreen(
            teams = previewTeams,
            userRole = UserRole.TEACHER,
            teamFormation = TeamFormation.CUSTOM,
            userTeamId = null,
            availableStudents = previewAvailableStudents,
            onNavigateBack = {},
            onJoinTeam = {},
            onLeaveTeam = {},
            onAddStudent = { _, _ -> },
            onRemoveStudent = { _, _ -> },
            onSetCaptain = { _, _ -> },
            onUpdateGrade = { _, _ -> },
        )
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 960)
@Composable
private fun TeamsScreenStudentPreview() {
    Hitsprocesses2Theme {
        TeamsScreen(
            teams = previewTeams,
            userRole = UserRole.STUDENT,
            teamFormation = TeamFormation.STUDENTS,
            userTeamId = 2,
            availableStudents = emptyList(),
            onNavigateBack = {},
            onJoinTeam = {},
            onLeaveTeam = {},
            onAddStudent = { _, _ -> },
            onRemoveStudent = { _, _ -> },
            onSetCaptain = { _, _ -> },
            onUpdateGrade = { _, _ -> },
        )
    }
}
