package com.dd.android.dailysimple.db

import com.dd.android.dailysimple.common.utils.DateUtils.msDateOnlyFrom
import com.dd.android.dailysimple.db.dao.CheckStatusDao
import com.dd.android.dailysimple.db.dao.DailyHabitDao
import com.dd.android.dailysimple.db.data.CheckStatus
import com.dd.android.dailysimple.db.data.DailyHabit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DailyHabitRepository(
    private val dailyHabitDao: DailyHabitDao,
    private val checkStatusDao: CheckStatusDao
) {

    fun getHabits(time:Long) = dailyHabitDao.getHabitsWithCheckStatus(time)

    fun getHabit(habitId: Long) =
        dailyHabitDao.getHabit(habitId)

    fun toggleIt(habitId: Long) =
        GlobalScope.launch(Dispatchers.IO) {
            checkStatusDao.toggleIt(msDateOnlyFrom(), habitId)
        }

    suspend fun insert(checkStatus: CheckStatus) =
        checkStatusDao.insert(checkStatus)

    suspend fun insert(habit: DailyHabit) =
        dailyHabitDao.insert(habit)

    suspend fun delete(checkStatus: CheckStatus) =
        checkStatusDao.delete(checkStatus)

    suspend fun delete(habitId: Long) =
        dailyHabitDao.delete(habitId)

    suspend fun update(habit: DailyHabit) =
        dailyHabitDao.update(habit)
}