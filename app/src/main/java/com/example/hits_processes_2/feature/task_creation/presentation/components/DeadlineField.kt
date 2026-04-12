package com.example.hits_processes_2.feature.task_creation.presentation.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hits_processes_2.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DeadlineField(
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
            text = stringResource(R.string.task_creation_deadline_label),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker() },
        ) {
            TextField(
                value = displayText,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = stringResource(R.string.task_creation_deadline_placeholder)) },
                readOnly = true,
                enabled = false,
                singleLine = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = stringResource(R.string.task_creation_deadline_pick_content_description),
                    )
                },
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                colors = taskCreationFieldColors(),
            )
        }
    }
}
