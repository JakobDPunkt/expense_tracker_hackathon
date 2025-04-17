package com.example.expense_tracker_hackathon.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Page2Screen() {
    val db = rememberDatabase()
    val vm: ExpenseViewModel = viewModel(factory = ExpenseViewModelFactory(db))
    val expenses by vm.expenses.collectAsState(initial = emptyList())

    val allCategories = listOf(
        "Food", "Apartment", "Social", "Transport",
        "Entertainment", "Utilities", "Travel", "Other"
    )

    val totals = remember(expenses) {
        expenses.groupBy { it.category }
            .mapValues { (_, items) -> items.sumOf { it.price } }
            .toSortedMap()
    }

    val grandTotal = totals.values.sum()
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    // Euro formatter
    val moneyFmt = remember {
        NumberFormat.getCurrencyInstance(Locale.GERMANY).apply {
            currency = Currency.getInstance("EUR")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Expenses by Category", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (totals.isEmpty()) {
                item {
                    Text("No expenses yet")
                }
            } else {
                // Category totals
                items(allCategories) { cat ->
                    val amt = totals[cat] ?: 0.0
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCategory = cat },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(cat, style = MaterialTheme.typography.titleMedium)
                                if (amt == 0.0) {
                                    Text("No expenses", style = MaterialTheme.typography.bodySmall)
                                } else {
                                    Text(
                                        moneyFmt.format(amt),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Category info",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Grand total
                item {
                    Spacer(Modifier.height(12.dp))
                    Divider()
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Total",
                            Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            moneyFmt.format(grandTotal),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                // Details for selected category
                selectedCategory?.let { category ->
                    val selectedExpenses = expenses.filter { it.category == category }

                    item {
                        Spacer(Modifier.height(16.dp))
                        Text("Details for $category", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                    }

                    items(selectedExpenses) { expense ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                expense.name,
                                Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                moneyFmt.format(expense.price),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}
