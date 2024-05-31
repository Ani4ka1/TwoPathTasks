package com.example.twopathtask

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ArchivedTaskDao {
    @Query("SELECT * FROM archived_tasks WHERE accountId = :accountId ORDER BY date ASC")
    fun getArchivedTasks(accountId: Int): Flow<List<ArchivedTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: ArchivedTask)

    @Delete
    suspend fun delete(task: ArchivedTask)
}
