package com.example.spelltester.data.db

import android.content.*
import androidx.room.*
import com.example.spelltester.data.db.word.*
import com.example.spelltester.data.db.attempt.*
import com.example.spelltester.data.db.quiz.*
import com.example.spelltester.data.storage.*

const val databaseName = "SpellTestDatabase.db"
const val dbVersion = 10
@Database(
    entities = [ Word::class, Attempt::class, Quiz::class],
    version = dbVersion,
)
@TypeConverters(IntArrayConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun attemptDao(): AttemptDao
    abstract fun quizDao(): QuizDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance
            ?: synchronized(LOCK) {
                instance
                    ?: createDatabase(
                        context
                    ).also { instance = it }
            }

        fun getInstance() = instance!!
        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, databaseName
            ).allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .createFromAsset("database/$databaseName")
                .build().also {
                    LocalStorage.getInstance().log("Database created")
                        .log("Database version: $dbVersion")
                }
    }
}