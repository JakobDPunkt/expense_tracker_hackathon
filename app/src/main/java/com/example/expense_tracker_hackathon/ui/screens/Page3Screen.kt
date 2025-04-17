package com.example.expense_tracker_hackathon.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.expense_tracker_hackathon.ui.screens.rememberDatabase
import com.example.expense_tracker_hackathon.ui.screens.ExpenseViewModel
import com.example.expense_tracker_hackathon.ui.screens.ExpenseViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Page3Screen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    // Get the same VM to invoke clearAllExpenses()
    val db = rememberDatabase()
    val vm: ExpenseViewModel = viewModel(factory = ExpenseViewModelFactory(db))

    // Local state for showing the confirmation dialog
    var showConfirm by remember { mutableStateOf(false) }

    //  ── Confirmation Dialog ────────────────────────────────────
    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete all expenses? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.clearAllExpenses()
                    showConfirm = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    //  ── Settings Content ───────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1) Theme toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dark Theme", style = MaterialTheme.typography.titleMedium)
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { onToggleTheme() }
            )
        }

        // 2) Clear all expenses (opens confirmation)
        Button(
            onClick = { showConfirm = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Clear All Expenses", color = MaterialTheme.colorScheme.onError)
        }
    }
}
