package com.example.expense_tracker_hackathon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.expense_tracker_hackathon.ui.ExpenseTrackerApp     // ← new root

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ExpenseTrackerApp() }                 // ← was ExpenseApp()
    }
}
