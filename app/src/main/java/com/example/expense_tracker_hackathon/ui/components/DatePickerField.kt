package com.example.expense_tracker_hackathon.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String = "Date",
    dateMillis: Long?,                 // current selection (can be null)
    onDateChange: (Long) -> Unit,      // callback with new millis
    modifier: Modifier = Modifier
) {
    val formatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val dateString = remember(dateMillis) {
        dateMillis?.let {
            Instant.ofEpochMilli(it)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(formatter)
        } ?: ""
    }

    var showDialog by remember { mutableStateOf(false) }
    val pickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)

    OutlinedButton(onClick = { showDialog = true }, modifier = modifier) {
        Text(if (dateString.isNotBlank()) dateString else label)
    }

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let(onDateChange)
                        showDialog = false
                    }
                ) { Text("OK") }
            },
            dismissButton = { TextButton({ showDialog = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = pickerState)
        }
    }
}
