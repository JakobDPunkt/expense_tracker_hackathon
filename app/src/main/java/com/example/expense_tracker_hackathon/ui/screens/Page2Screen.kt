package com.example.expense_tracker_hackathon.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun Page2Screen() {
    // --- Obtain the same database & view‑model you already use on Page 1 -------------
    val db  = rememberDatabase()
    val vm: ExpenseViewModel = viewModel(factory = ExpenseViewModelFactory(db))
    val expenses by vm.expenses.collectAsState(initial = emptyList())

    // --- Aggregate ------------------------------------------------------------------
    val totals = remember(expenses) {
        expenses
            .groupBy { it.category }
            .mapValues { (_, items) -> items.sumOf { it.price } }
            .toSortedMap()                                 // optional: alphabetical order
    }
    val grandTotal = totals.values.sum()

    // --- UI -------------------------------------------------------------------------
    val moneyFmt = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Expenses by category", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        if (totals.isEmpty()) {
            Text("No expenses yet")
        } else {
            totals.forEach { (cat, amt) ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(cat,      Modifier.weight(1f))
                    Text(moneyFmt.format(amt))
                }
            }

            Spacer(Modifier.height(8.dp))
            Divider()
            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                Text(moneyFmt.format(grandTotal), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
