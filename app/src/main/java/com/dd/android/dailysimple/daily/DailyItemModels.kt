package com.dd.android.dailysimple.daily

import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.recycler.ItemModel
import com.dd.android.dailysimple.daily.viewmodel.HabitViewModel
import com.dd.android.dailysimple.daily.viewmodel.ScheduleViewModel

private const val TAG = "ItemModel"
private const val HABITS = "Habit"
private const val HABIT_HEADER = "HabitHeader"
private const val TODO = "TODO"

private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class DailyItemModels(activity: FragmentActivity) : ViewModel() {

    private val scheduleVm by activity.viewModels<ScheduleViewModel>()
    private val habitsVm by activity.viewModels<HabitViewModel>()

    private val schedule get() = scheduleVm.schedule.value!!
    private val allHabits get() = habitsVm.allHabits.value!!
    private val habitHeader get() = habitsVm.header.value!!

    val data = MediatorLiveData<List<ItemModel>>().apply {
        addSource(habitsVm.allHabits) {
            if (ensureVm()) value = createModel(HABITS)
        }
        addSource(habitsVm.header) {
            if (ensureVm()) value = createModel(HABIT_HEADER)
        }
        addSource(scheduleVm.schedule) {
            if (ensureVm()) value = createModel(TODO)
        }
    }

    private fun ensureVm() =
        scheduleVm.schedule.value != null &&
                habitsVm.allHabits.value != null &&
                habitsVm.header.value != null

    private fun createModel(from: String): List<ItemModel> =
        mutableListOf<ItemModel>().apply {
            add(scheduleVm.header)
            addAll(schedule)
            add(habitHeader)
            addAll(allHabits)
            logD { "Daily model is re-created : $from, size:$size" }
        }
}

