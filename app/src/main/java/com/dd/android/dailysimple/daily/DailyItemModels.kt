package com.dd.android.dailysimple.daily

import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.recycler.ItemModel
import com.dd.android.dailysimple.daily.viewmodel.HabitViewModel
import com.dd.android.dailysimple.daily.viewmodel.TodoViewModel

private const val TAG = "ItemModel"
private const val HABITS = "Habit"
private const val HABIT_HEADER = "HabitHeader"
private const val TODO = "TODO"

private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class DailyItemModels(activity: FragmentActivity) : ViewModel() {

    private val todayVm by activity.viewModels<TodoViewModel>()
    private val habitsVm by activity.viewModels<HabitViewModel>()

    private val todayTodo get() = todayVm.todayTodo.value!!
    private val allHabits get() = habitsVm.allHabits.value!!
    private val habitHeader get() = habitsVm.header.value!!

    val data = MediatorLiveData<List<ItemModel>>().apply {
        addSource(habitsVm.allHabits) {
            if (ensureVm()) value = createModel(HABITS)
        }
        addSource(habitsVm.header) {
            if (ensureVm()) value = createModel(HABIT_HEADER)
        }
        addSource(todayVm.todayTodo) {
            if (ensureVm()) value = createModel(TODO)
        }
    }

    private fun ensureVm() =
        todayVm.todayTodo.value != null &&
                habitsVm.allHabits.value != null &&
                habitsVm.header.value != null

    private fun createModel(from: String): List<ItemModel> =
        mutableListOf<ItemModel>().apply {
            add(todayVm.todayHeader)
            addAll(todayTodo)
            add(habitHeader)
            addAll(allHabits)
            logD { "Daily model is re-created : $from, size:$size" }
        }
}

