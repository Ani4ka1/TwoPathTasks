package com.example.twopathtask

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Account::class], version = 2)  // Увеличьте версию с 1 на 2
abstract class AccDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao

    companion object {
        @Volatile
        private var INSTANCE: AccDatabase? = null

        fun getDatabase(context: Context): AccDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AccDatabase::class.java,
                    "acc_database"
                ).fallbackToDestructiveMigration()  // Добавьте это для простой миграции
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
