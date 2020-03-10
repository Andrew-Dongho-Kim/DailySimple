package com.dd.android.dailysimple.schedule

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

private const val DATABASE_NAME = "schedule-db"

@Database(entities = [GroupSchedule::class, DailySchedule::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dailyScheduleDao(): DailyScheduleDao
    abstract fun groupDailyScheduleJoinDao(): GroupDailyScheduleJoinDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDb(context).also { instance = it }
            }

        private fun buildDb(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                    }
                })
                .build()
    }
}