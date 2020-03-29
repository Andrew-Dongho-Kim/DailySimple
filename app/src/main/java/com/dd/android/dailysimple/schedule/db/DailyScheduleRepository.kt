package com.dd.android.dailysimple.schedule.db


/**
 * Why use a Repository?
 * A Repository manages queries and allows you to use multiple backends.
 * In the most common example,
 * the Repository implements the logic for deciding whether to fetch data from a network
 * or use results cached in a local database.
 */
class DailyScheduleRepository(
    private val dailyHabitDao: DailyHabitDao
) {

    val allHabits = dailyHabitDao.getAllHabits()

    fun getHabit(id: Long) = dailyHabitDao.getHabit(id)

    suspend fun insert(habit: DailyHabit) =
        dailyHabitDao.insert(habit)

    suspend fun update(habit: DailyHabit)
        = dailyHabitDao.update(habit)
}