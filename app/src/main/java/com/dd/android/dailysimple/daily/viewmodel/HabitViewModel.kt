package com.dd.android.dailysimple.daily.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.di.appDb
import com.dd.android.dailysimple.common.di.getString
import com.dd.android.dailysimple.common.utils.DateUtils
import com.dd.android.dailysimple.daily.AppConst.EMPTY_ITEM_ID_HABIT
import com.dd.android.dailysimple.daily.AppConst.SIMPLE_HEADER_ID_HABIT
import com.dd.android.dailysimple.daily.DailyViewType.Companion.HABIT_ITEM
import com.dd.android.dailysimple.daily.viewholders.DailyEmptyItemModel
import com.dd.android.dailysimple.daily.viewholders.DailySimpleHeaderItem
import com.dd.android.dailysimple.db.DailyHabitRepository
import com.dd.android.dailysimple.db.data.DailyHabit
import kotlinx.coroutines.launch


class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository by lazy {
        val db = appDb()
        DailyHabitRepository(
            db.dailyScheduleDao(),
            db.checkStatusDao()
        )
    }


    val header = liveData {
        emit(
            DailySimpleHeaderItem(
                SIMPLE_HEADER_ID_HABIT,
                application.getString(R.string.habit)
            )
        )
    }

    val selectedDate = liveData {
        emit(DateUtils.msDateFrom())
    } as MutableLiveData<Long>

    val allHabits = Transformations.switchMap(selectedDate) { time ->
        Transformations.map(repository.getHabits(time)) { habits ->
            if (habits.isEmpty()) {
                listOf(
                    DailyEmptyItemModel(
                        EMPTY_ITEM_ID_HABIT,
                        HABIT_ITEM,
                        getString(R.string.no_habit_message)
                    )
                )
            } else {
                habits
            }
        }
    }

    fun getHabit(habitId: Long) = repository.getHabit(habitId)

    fun toggleIt(habitId: Long) = viewModelScope.launch {
        repository.toggleIt(habitId)
    }

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on
     * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called
     * viewModelScope which we can use here.
     */
    fun insert(habit: DailyHabit) = viewModelScope.launch {
        repository.insert(habit)
    }

    fun delete(habitId: Long) = viewModelScope.launch {
        repository.delete(habitId)
    }
}




