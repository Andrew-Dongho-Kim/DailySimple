package com.dd.android.dailysimple.db

import com.dd.android.dailysimple.db.dao.CheckStatusDao
import com.dd.android.dailysimple.db.dao.DailyHabitDao
import com.dd.android.dailysimple.db.data.CheckStatus
import com.dd.android.dailysimple.db.data.DailyHabit


class DailyHabitRepository(
    private val dailyHabitDao: DailyHabitDao,
    private val checkStatusDao: CheckStatusDao
) {

    val allHabits = dailyHabitDao.getAllHabits()

    fun getHabit(habitId: Long) = dailyHabitDao.getHabit(habitId)

    fun getCheckStatus(habitId: Long) = checkStatusDao.getAllCheckStatus(habitId)

    suspend fun insert(checkStatus: CheckStatus) =
        checkStatusDao.insert(checkStatus)

    suspend fun insert(habit: DailyHabit) =
        dailyHabitDao.insert(habit)

    suspend fun update(habit: DailyHabit) =
        dailyHabitDao.update(habit)
}