package com.dd.android.dailysimple.schedule

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GroupSchedule::class, DailySchedule::class], version = 1)
abstract class ScheduleDatabase : RoomDatabase() {

    abstract fun dailyScheduleDao(): DailyScheduleDao

    abstract fun groupDailyScheduleJoinDao(): GroupDailyScheduleJoinDao

    companion object {
        @Volatile
        var Instance: ScheduleDatabase? = null


    }
}