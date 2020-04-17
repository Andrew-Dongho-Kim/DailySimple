package com.dd.android.dailysimple.daily

import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.recycler.ItemModel
import com.dd.android.dailysimple.daily.viewmodel.HabitViewModel
import com.dd.android.dailysimple.daily.viewmodel.ScheduleViewModel
import com.dd.android.dailysimple.daily.viewmodel.TodoViewModel

private const val TAG = "DailyItemModels"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class DailyItemModels(activity: FragmentActivity) : ViewModel() {

    private val scheduleVm by activity.viewModels<ScheduleViewModel>()
    private val todoVm by activity.viewModels<TodoViewModel>()
    private val habitsVm by activity.viewModels<HabitViewModel>()

    private val models = arrayOf(
        scheduleVm.header,
        scheduleVm.schedule,
        todoVm.header,
        todoVm.overdueGroup,
        todoVm.todayTodo,
        habitsVm.header,
        habitsVm.allHabits
    )

    val data = MediatorLiveData<List<ItemModel>>().apply {
        models.forEach { liveData ->
            addSource(liveData) {
                if (ensureVm()) value = createModel()
            }
        }
    }

    private fun ensureVm(): Boolean {
        models.forEach { liveData ->
            if (liveData.value == null) return false
        }
        return true
    }

    private fun createModel(): List<ItemModel> =
        mutableListOf<ItemModel>().apply {
            models.forEach { liveData ->
                val model = liveData.value!!

                if (model is List<*>) {
                    model.forEach {
                        if (it is ItemModel) {
                            add(it)
                        }
                    }
                } else if (model is ItemModel) {
                    add(model)
                }
            }
            logD { "Daily - createModel()" }
        }
}

