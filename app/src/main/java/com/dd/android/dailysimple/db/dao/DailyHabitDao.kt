package com.dd.android.dailysimple.db.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.*
import com.dd.android.dailysimple.common.utils.DateUtils.firstDayOfMonth
import com.dd.android.dailysimple.common.utils.DateUtils.firstDayOfWeek
import com.dd.android.dailysimple.common.utils.DateUtils.msFrom
import com.dd.android.dailysimple.db.data.CheckStatus
import com.dd.android.dailysimple.db.data.DailyHabit
import com.dd.android.dailysimple.db.data.DailyHabit.CheckTerm.*
import com.dd.android.dailysimple.db.data.DailyHabitWithCheckStatus

@Dao
interface DailyHabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg habits: DailyHabit): List<Long>

    @Update
    suspend fun update(vararg habits: DailyHabit)

    @Query("DELETE FROM daily_habit WHERE id=:habitId")
    suspend fun delete(habitId: Long)

    @Query("SELECT * FROM daily_habit WHERE start <=:time AND :time < until")
    fun getHabits(time: Long): LiveData<List<DailyHabit>>

    @Query("SELECT * FROM daily_habit WHERE daily_habit.id=:id")
    fun getHabit(id: Long): LiveData<DailyHabit>

    @Query("SELECT * FROM check_status WHERE habit_id=:habitId AND :start <= date AND date < :until ORDER BY date DESC")
    fun getCheckStatus(habitId: Long, start: Long, until: Long): LiveData<List<CheckStatus>>

    fun getHabitsWithCheckStatus(time: Long): LiveData<List<DailyHabitWithCheckStatus>> =
        Transformations.map(getHabits(time)) { habitList ->
            mutableListOf<DailyHabitWithCheckStatus>().apply {

                habitList.forEach { habit ->
                    val start = when (habit.checkTerm) {
                        DAY -> time
                        WEEK -> firstDayOfWeek(time)
                        MONTH -> firstDayOfMonth(time)
                    }
                    add(
                        DailyHabitWithCheckStatus(
                            habit = habit,
                            checkedList = getCheckStatus(
                                habit.id,
                                start,
                                when (habit.checkTerm) {
                                    DAY -> msFrom(start, dates = 1)
                                    WEEK -> msFrom(start, weeks = 1)
                                    MONTH -> msFrom(start, months = 1)
                                }
                            ),
                            selectedTime = time
                        )
                    )
                }
            }
        }
}

