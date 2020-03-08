package com.dd.android.dailysimple.schedule

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GroupDailyScheduleJoinDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(field: GroupDailyScheduleJoin)

    @Query(
        """
        SELECT * FROM daily_schedule
        INNER JOIN group_daily_schedule_join
        ON daily_schedule.rowid == group_daily_schedule_join.sId
        WHERE group_daily_schedule_join.groupId=:groupId
        """
    )
    fun getDailySchedulesForGroup(groupId: Int): LiveData<List<DailySchedule>>

    @Query(
        """
            SELECT * FROM group_schedule
            INNER JOIN group_daily_schedule_join
            ON group_schedule.id == group_daily_schedule_join.groupId
            WHERE group_daily_schedule_join.sId=:sId
        """
    )
    fun getGroupSchedulesForDaily(sId: Int): LiveData<List<GroupSchedule>>
}

@Dao
interface DailyScheduleDao {
    @Query("SELECT * FROM daily_schedule")
    fun getDailySchedules(): LiveData<List<DailySchedule>>
}