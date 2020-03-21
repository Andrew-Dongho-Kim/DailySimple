package com.dd.android.dailysimple.schedule.daily.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.dd.android.dailysimple.schedule.db.AppDatabase
import com.dd.android.dailysimple.schedule.db.DailyHabit
import com.dd.android.dailysimple.schedule.db.DailyScheduleRepository
import kotlinx.coroutines.launch

class HabitsViewModel(application: Application) : AndroidViewModel(application) {
    // The ViewModel maintains a reference to the repository to get data.
    private val repository by lazy {
        val db = AppDatabase.getInstance(application.applicationContext)
        DailyScheduleRepository(db.dailyScheduleDao())
    }

    val allHabits = repository.allHabits


    fun getSchedule(id: Long) = repository.getHabit(id)

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on
     * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called
     * viewModelScope which we can use here.
     */
    fun insert(schedule: DailyHabit) = viewModelScope.launch {
        Log.d("TEST-DH", "Insert : $schedule, ${repository.insert(schedule)}")

    }
}
