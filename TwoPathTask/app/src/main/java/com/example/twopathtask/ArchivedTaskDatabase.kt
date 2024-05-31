package com.example.twopathtask

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ArchivedTask::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ArchivedTaskDatabase : RoomDatabase() {

    abstract fun archivedTaskDao(): ArchivedTaskDao

    companion object {
        @Volatile
        private var INSTANCE: ArchivedTaskDatabase? = null

        fun getDatabase(context: Context): ArchivedTaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArchivedTaskDatabase::class.java,
                    "archived_task_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
