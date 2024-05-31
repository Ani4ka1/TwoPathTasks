package com.example.twopathtask

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE category = :category AND accountId = :accountId ORDER BY date ASC LIMIT 2")
    fun getTwoNearestTasks(category: String, accountId: Int): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE category = :category AND accountId = :accountId")
    fun getTasksByCategory(category: String, accountId: Int): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}
