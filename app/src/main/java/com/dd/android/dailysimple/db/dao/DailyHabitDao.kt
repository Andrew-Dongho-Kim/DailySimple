package com.dd.android.dailysimple.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dd.android.dailysimple.db.data.CheckStatus
import com.dd.android.dailysimple.db.data.DailyHabit

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

    @Update
    suspend fun update(vararg habits: DailyHabit)

    @Query("SELECT * FROM daily_habit")
    fun getAllHabits(): LiveData<List<DailyHabit>>

    @Query("SELECT * FROM daily_habit WHERE daily_habit.id=:id")
    fun getHabit(id: Long): LiveData<DailyHabit>
}

@Dao
interface CheckStatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg checkStatus: CheckStatus): List<Long>

    @Query(
        "UPDATE check_status SET checked_count=:checkedCount WHERE habit_id=:habitId AND date=:date"
    )
    suspend fun update(habitId: Long, date: Long, checkedCount: Int)

    @Delete
    fun delete(vararg checkStatus: CheckStatus): Int

    @Query(
        "SELECT * FROM check_status WHERE habit_id=:habitId AND date >= :from AND date < :under"
    )
    fun getCheckStatus(habitId: Long, from: Long, under: Long): List<CheckStatus>


    @Query(
        "SELECT * FROM check_status WHERE habit_id=:habitId ORDER BY date DESC"
    )
    fun getAllCheckStatus(habitId: Long): LiveData<List<CheckStatus>>
}