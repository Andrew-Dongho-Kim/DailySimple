package com.dd.android.dailysimple.db


/**
 * Why use a Repository?
 * A Repository manages queries and allows you to use multiple backends.
 * In the most common example,
 * the Repository implements the logic for deciding whether to fetch data from a network
 * or use results cached in a local database.
 */
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