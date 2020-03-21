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
//        .toLiveData(
//        Config(
//            pageSize = 60,
//            enablePlaceholders = true,
//            maxSize = 200
//        )
//    )


    fun getHabit(id: Long) = dailyHabitDao.getHabit(id)

    suspend fun insert(schedule: DailyHabit) =
        dailyHabitDao.insert(schedule)

}