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
    entities = [Plan::class, DailyHabit::class, CheckStatus::class],
    version = 2,
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
                .addMigrations(MIGRATION_1_2)
                .build()
    }
}

/**
 * update q
set q.QuestionID = a.QuestionID
from QuestionTrackings q
inner join QuestionAnswers a
on q.AnswerID = a.AnswerID
where q.QuestionID is null --
 */

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE daily_habit ADD COLUMN finishTime INTEGER")
        database.execSQL("UPDATE daily_habit SET finishTime=startTime")
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

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
//        val table = "daily_habit"
//        executeSQLMakeTableToLegacy(database, table)
//
//        database.execSQL("""CREATE TABLE daily_habit(
//                    id INTEGER PRIMARY KEY NOT NULL AUTO GENERATE
//                );"""
//        )

        database.execSQL("ALTER TABLE daily_habit ADD COLUMN startTime INTEGER DEFAULT 0")
    }
}

private fun executeSQLMakeTableToLegacy(database:SupportSQLiteDatabase, tableName:String) =
    database.execSQL("ALTER TABLE $tableName RENAME TO ${tableName}_old;")

