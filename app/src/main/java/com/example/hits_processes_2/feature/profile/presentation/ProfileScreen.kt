package com.example.hits_processes_2.feature.profile.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.models.User
import com.example.hits_processes_2.common.ui.component.ClassroomTopAppBar
import com.example.hits_processes_2.common.ui.component.InfoCard
import com.example.hits_processes_2.common.ui.component.LabeledText
import com.example.hits_processes_2.common.ui.component.UserAvatar

/**
 * User profile screen
 */
@Composable
fun ProfileScreen(
	user: User,
	onNavigateBack: () -> Unit,
	onLogout: () -> Unit,
	modifier: Modifier = Modifier
) {
	Scaffold(
		modifier = modifier.fillMaxSize(),
		topBar = {
			ClassroomTopAppBar(
				title = "Профиль",
				onNavigateBack = onNavigateBack
			)
		}
	) { paddingValues ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
				.padding(16.dp)
		) {
			InfoCard {
				// User Avatar
				Column(
					modifier = Modifier.fillMaxWidth(),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					UserAvatar(
						initials = user.initials,
						size = 96.dp
					)
				}

				Spacer(modifier = Modifier.height(24.dp))

				// User Information
				LabeledText(
					label = "Фамилия и имя",
					text = user.fullName
				)

				Spacer(modifier = Modifier.height(16.dp))

				LabeledText(
					label = "Город",
					text = user.city ?: "Не указан"
				)

				Spacer(modifier = Modifier.height(16.dp))

				LabeledText(
					label = "Почта",
					text = user.email
				)

				Spacer(modifier = Modifier.height(16.dp))

				LabeledText(
					label = "Дата рождения",
					text = user.birthDate ?: "Не указана"
				)
			}

			Spacer(modifier = Modifier.height(24.dp))

			Button(
				onClick = onLogout,
				modifier = Modifier.fillMaxWidth(),
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.error
				)
			) {
				Icon(
					imageVector = Icons.Default.ExitToApp,
					contentDescription = null,
					modifier = Modifier.size(20.dp)
				)
				Spacer(modifier = Modifier.width(8.dp))
				Text("Выйти из аккаунта")
			}
		}
	}
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
	val mockUser = User(
		id = 1,
		firstName = "Иван",
		lastName = "Иванов",
		email = "ivan@example.com",
		city = "Москва",
		birthDate = "01.01.2000"
	)

	MaterialTheme {
		ProfileScreen(
			user = mockUser,
			onNavigateBack = {},
			onLogout = {}
		)
	}
}
