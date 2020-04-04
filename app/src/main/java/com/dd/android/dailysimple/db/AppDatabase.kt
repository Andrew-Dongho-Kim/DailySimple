package com.dd.android.dailysimple.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

private const val DATABASE_NAME = "schedule-db"

// Migration strategy
//  https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
@Database(
    entities = [GroupSchedule::class, DailyHabit::class, CheckStatus::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dailyScheduleDao(): DailyHabitDao

    abstract fun checkStatusDao(): CheckStatusDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDb(context).also { instance = it }
            }

        private fun buildDb(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                .build()
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE check_status(
                'date' INTEGER NOT NULL PRIMARY KEY, 
                'checked_count' INTEGER NOT NULL,
                'habit_id' INTEGER NOT NULL
            )
            """
        )
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
                DROP TABLE check_status
            """
        )
        database.execSQL(
            """
                CREATE TABLE check_status(
                date INTEGER NOT NULL, 
                checked_count INTEGER NOT NULL,
                habit_id INTEGER NOT NULL,
                PRIMARY KEY (date, habit_id)
            );
            """
        )
    }
}