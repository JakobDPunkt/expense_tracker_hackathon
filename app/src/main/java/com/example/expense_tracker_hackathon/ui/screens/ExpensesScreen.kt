package com.example.expense_tracker_hackathon.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.expense_tracker_hackathon.data.ExpenseDatabase
import com.example.expense_tracker_hackathon.data.ExpenseItem
import com.example.expense_tracker_hackathon.ui.components.CategoryDropdown
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/* ──────────────────── View‑model & helpers (unchanged) ───────────────────── */
@OptIn(ExperimentalMaterial3Api::class)
class ExpenseViewModel(private val db: ExpenseDatabase) : ViewModel() {
    private val dao = db.expenseDao()
    val expenses: Flow<List<ExpenseItem>> = dao.getAll()

    fun addExpense(desc: String, amt: Double, cat: String, date: String) =
        viewModelScope.launch { dao.insert(ExpenseItem(name = desc, price = amt, category = cat, date = date)) }

    fun updateExpense(item: ExpenseItem) = viewModelScope.launch { dao.update(item) }
    fun deleteExpense(item: ExpenseItem) = viewModelScope.launch { dao.delete(item) }
}
class ExpenseViewModelFactory(private val db: ExpenseDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = ExpenseViewModel(db) as T
}
@Composable fun rememberDatabase(): ExpenseDatabase {
    val ctx = LocalContext.current
    return remember {
        Room.databaseBuilder(ctx, ExpenseDatabase::class.java, "expense-db")
            .fallbackToDestructiveMigration()
            .build()
    }
}

/* ───────────────────────────── Screen UI ─────────────────────────────────── */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen() {
    val db = rememberDatabase()
    val vm: ExpenseViewModel = viewModel(factory = ExpenseViewModelFactory(db))
    val expenses by vm.expenses.collectAsState(initial = emptyList())

    var showSheet by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showSheet = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Add expense")
            }
        }
    ) { padding ->

        val grouped = remember(expenses) {
            expenses.sortedByDescending { it.date }
                .groupBy { it.date }
        }

        if (grouped.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("No expenses yet") }
        } else {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                grouped.forEach { (date, list) ->
                    item(key = "header-$date") {
                        Surface(
                            Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Text(
                                text = date,
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    items(list, key = { it.id }) { exp ->
                        ExpenseRow(
                            exp = exp,
                            onUpdate = vm::updateExpense,
                            onDelete = { vm.deleteExpense(exp) }
                        )
                    }
                }
            }
        }
    }

    if (showSheet) {
        AddExpenseSheet(
            onDismiss = { showSheet = false },
            onAdd = { desc, amt, cat, date ->
                vm.addExpense(desc, amt, cat, date)
                showSheet = false
            }
        )
    }
}

/* ───────────────────── bottom‑sheet composable ───────────────────────────── */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExpenseSheet(
    onDismiss: () -> Unit,
    onAdd: (String, Double, String, String) -> Unit
) {
    var desc by remember { mutableStateOf("") }
    var amt  by remember { mutableStateOf("") }
    var cat  by remember { mutableStateOf("") }

    val categories = listOf("Food", "Transport", "Entertainment", "Utilities", "Other")

    val todayMillis = remember {
        LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = todayMillis)
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val dateString = remember(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(dateFormatter)
        } ?: ""
    }
    var showDatePicker by remember { mutableStateOf(false) }
    val ctx = LocalContext.current

    /* ── no WindowInsets parameter (works on older Compose) ── */
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("New expense", style = MaterialTheme.typography.titleMedium)

            TextField(desc, { desc = it }, Modifier.fillMaxWidth(), label = { Text("Name") })
            TextField(amt,  { amt  = it }, Modifier.fillMaxWidth(), label = { Text("Amount") })

            CategoryDropdown(
                options = categories,
                selectedOption = cat,
                onOptionSelected = { cat = it },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (dateString.isNotBlank()) dateString else "Select date") }

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = { TextButton({ showDatePicker = false }) { Text("OK") } },
                    dismissButton = { TextButton({ showDatePicker = false }) { Text("Cancel") } }
                ) { DatePicker(state = datePickerState) }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    val amount = amt.toDoubleOrNull()
                    if (desc.isNotBlank() && amount != null && cat.isNotBlank() && dateString.isNotBlank()) {
                        onAdd(desc, amount, cat, dateString)
                    } else {
                        Toast.makeText(ctx, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Save") }
        }
    }
}

/* ───────────────────────── Row with card layout ─────────────────────────── */
@Composable
fun ExpenseRow(
    exp: ExpenseItem,
    onUpdate: (ExpenseItem) -> Unit,
    onDelete: () -> Unit
) {
    var editing by remember { mutableStateOf(false) }
    var desc by remember { mutableStateOf(exp.name) }
    var amt  by remember { mutableStateOf(exp.price.toString()) }
    var cat  by remember { mutableStateOf(exp.category) }

    Card(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        if (editing) {
            Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TextField(desc, { desc = it }, label = { Text("Name") })
                TextField(amt,  { amt  = it }, label = { Text("Amount") })
                TextField(cat,  { cat  = it }, label = { Text("Category") })
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton({ editing = false }) { Text("Cancel") }
                    TextButton({
                        onUpdate(exp.copy(name = desc, price = amt.toDoubleOrNull() ?: 0.0, category = cat))
                        editing = false
                    }) { Text("Save") }
                    TextButton(onDelete) { Text("Delete") }
                }
            }
        } else {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(desc, style = MaterialTheme.typography.bodyLarge)
                    Text(cat, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
                Text(
                    text = exp.price.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                TextButton({ editing = true }) { Text("Edit") }
            }
        }
    }
}
