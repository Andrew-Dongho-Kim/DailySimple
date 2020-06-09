package com.dd.android.dailysimple.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dd.android.dailysimple.db.dao.CheckStatusDao
import com.dd.android.dailysimple.db.dao.DailyHabitDao
import com.dd.android.dailysimple.db.dao.DailyTodoDao
import com.dd.android.dailysimple.db.data.*

private const val DATABASE_NAME = "schedule-db"

// Migration strategy
//  https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
@Database(
    entities = [
        Plan::class,
        DailyHabit::class, CheckStatus::class,
        DailyTodo::class, TodoSubTask::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(*[DailyHabitTypeConverters::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun dailyScheduleDao(): DailyHabitDao

    abstract fun checkStatusDao(): CheckStatusDao

    abstract fun dailyTodoDao(): DailyTodoDao

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
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                .build()
    }
}

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
            CREATE TABLE daily_todo(
                'id' INTEGER NOT NULL PRIMARY KEY, 
                'title' TEXT NOT NULL,
                'memo' TEXT NOT NULL,
                'start' INTEGER NOT NULL,
                'until' INTEGER NOT NULL,
                'done' INTEGER NOT NULL DEFAULT ${DoneState.ONGOING},
                'alarm_days' INTEGER NULL,
                'alarm_time' INTEGER NULL,
                'alarm_power' INTEGER NULL,
                'repeat' INTEGER NULL
            )
            """
        )

        database.execSQL(
            """
            CREATE TABLE daily_todo_sub_job(
                'sub_job_id' INTEGER NOT NULL PRIMARY KEY, 
                'daily_todo_id' INTEGER NOT NULL,
                'title' TEXT NOT NULL
            )
            """
        )
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
                DROP TABLE daily_todo_sub_job
            """
        )
        database.execSQL(
            """
                CREATE TABLE todo_sub_task(
                'id' INTEGER NOT NULL PRIMARY KEY,
                'todo_id' INTEGER NOT NULL,
                'title' TEXT NOT NULL
            );
            """
        )
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE todo_sub_task ADD COLUMN state INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_5_6 = object : Migration(5, 6) {
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

private fun executeSQLMakeTableToLegacy(database: SupportSQLiteDatabase, tableName: String) =
    database.execSQL("ALTER TABLE $tableName RENAME TO ${tableName}_old;")

