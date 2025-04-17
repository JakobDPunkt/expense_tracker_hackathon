package com.example.expense_tracker_hackathon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.example.expense_tracker_hackathon.ui.navigation.ExpenseTrackerApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 1) App‑wide theme state
            var darkTheme by remember { mutableStateOf(false) }

            // 2) Choose color scheme
            val colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()

            MaterialTheme(
                colorScheme = colorScheme
            ) {
                ExpenseTrackerApp(
                    isDarkTheme   = darkTheme,
                    onToggleTheme = { darkTheme = !darkTheme }
                )
            }
        }
    }
}
