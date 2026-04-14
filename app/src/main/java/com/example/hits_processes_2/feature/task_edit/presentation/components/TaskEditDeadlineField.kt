package com.example.hits_processes_2.feature.task_edit.presentation.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.hits_processes_2.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun TaskEditDeadlineField(
    deadlineMillis: Long?,
    minimumDeadlineMillis: Long?,
    onDeadlineSelected: (Long) -> Unit,
    onInvalidDeadlineSelected: () -> Unit,
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val displayValue = deadlineMillis?.let {
        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(it)
    }.orEmpty()

    fun showTimePicker(selectedDateMillis: Long) {
        calendar.timeInMillis = selectedDateMillis
        TimePickerDialog(
            context,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                val selectedMillis = calendar.timeInMillis
                if (minimumDeadlineMillis != null && selectedMillis < minimumDeadlineMillis) {
                    onInvalidDeadlineSelected()
                } else {
                    onDeadlineSelected(selectedMillis)
                }
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true,
        ).show()
    }

    fun showDatePicker() {
        val baseDate = deadlineMillis ?: minimumDeadlineMillis ?: System.currentTimeMillis()
        calendar.timeInMillis = baseDate
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                showTimePicker(calendar.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
        ).apply {
            minimumDeadlineMillis?.let { minDate ->
                val minCalendar = Calendar.getInstance().apply { timeInMillis = minDate }
                minCalendar.set(Calendar.HOUR_OF_DAY, 0)
                minCalendar.set(Calendar.MINUTE, 0)
                minCalendar.set(Calendar.SECOND, 0)
                minCalendar.set(Calendar.MILLISECOND, 0)
                datePicker.minDate = minCalendar.timeInMillis
            }
        }.show()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDatePicker() },
    ) {
        TextField(
            value = displayValue,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            enabled = false,
            shape = TaskEditFieldDefaults.FieldShape,
            placeholder = { Text(text = stringResource(R.string.task_edit_deadline_placeholder)) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = stringResource(R.string.task_edit_deadline_pick_content_description),
                )
            },
            colors = taskEditFieldColors(),
        )
    }
}
