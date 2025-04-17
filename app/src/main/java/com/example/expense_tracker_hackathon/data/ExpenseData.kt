package com.example.expense_tracker_hackathon.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
data class ExpenseItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val category: String,
    val date: String       // yyyy‑MM‑dd
)

@Dao
interface ExpenseDao {
    @Insert suspend fun insert(item: ExpenseItem)
    @Query("SELECT * FROM ExpenseItem ORDER BY id DESC")
    fun getAll(): Flow<List<ExpenseItem>>
    @Update suspend fun update(item: ExpenseItem)
    @Delete suspend fun delete(item: ExpenseItem)

    // ← new: delete every row
    @Query("DELETE FROM ExpenseItem")
    suspend fun clearAll()
}

@Database(entities = [ExpenseItem::class], version = 5)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}
