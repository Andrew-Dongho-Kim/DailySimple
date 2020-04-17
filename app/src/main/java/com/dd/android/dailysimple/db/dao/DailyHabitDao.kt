package com.dd.android.dailysimple.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dd.android.dailysimple.db.data.CheckStatus
import com.dd.android.dailysimple.db.data.DailyHabit

@Dao
interface DailyHabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg habits: DailyHabit): List<Long>

    @Update
    suspend fun update(vararg habits: DailyHabit)

    @Query("DELETE FROM daily_habit WHERE id=:habitId")
    suspend fun delete(habitId: Long)

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
        """
            UPDATE check_status SET checked_count=:checkedCount 
             WHERE habit_id=:habitId AND date=:date"""
    )
    suspend fun update(habitId: Long, date: Long, checkedCount: Int)

    @Delete
    fun delete(vararg checkStatus: CheckStatus): Int

    @Query(
        "SELECT * FROM check_status WHERE habit_id=:habitId ORDER BY date DESC"
    )
    fun getAllCheckStatus(habitId: Long): LiveData<List<CheckStatus>>
}