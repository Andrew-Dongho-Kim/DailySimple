package com.dd.android.dailysimple.schedule.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

//@Dao
//interface GroupDailyScheduleJoinDao {
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(field: GroupDailyScheduleJoin)
//
//    @Query(
//        """
//        SELECT * FROM daily_schedule WHERE daily_schedule.groupId=:groupId
//        """
//    )
//    fun getDailySchedulesForGroup(groupId: Int): LiveData<List<DailySchedule>>
//
//    @Query(
//        """
//            SELECT * FROM group_schedule)
//            INNER JOIN group_daily_schedule_join
//            ON group_schedule.id == group_daily_schedule_join.groupId
//            WHERE group_daily_schedule_join.dailyId=:dailyId
//        """
//    )
//    fun getGroupSchedulesForDaily(dailyId: Int): LiveData<List<GroupSchedule>>
//}

@Dao
interface DailyHabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg habits: DailyHabit): List<Long>

    @Query("SELECT * FROM daily_habit")
    fun getAllHabits(): LiveData<List<DailyHabit>>//DataSource.Factory<Int, DailySchedule>

    @Query("SELECT * FROM daily_habit WHERE daily_habit.id=:id")
    fun getHabit(id: Long): LiveData<DailyHabit>
}

interface DailyCheckStatesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg checkedState: DailyChecked)
}