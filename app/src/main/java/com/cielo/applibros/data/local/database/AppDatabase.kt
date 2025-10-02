package com.cielo.applibros.data.local.database
import com.cielo.applibros.data.local.entities.Converters
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters

import com.cielo.applibros.data.local.dao.BookDao
import com.cielo.applibros.data.local.entities.BookEntity

@Database(
    entities = [BookEntity::class],
    version = 4, // La versión actual de tu schema
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "books_database"
                )
                    .fallbackToDestructiveMigration() // Elimina y recrea la base si cambia la versión
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}