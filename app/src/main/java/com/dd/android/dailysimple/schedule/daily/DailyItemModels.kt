package com.dd.android.dailysimple.schedule.daily

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MediatorLiveData
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.schedule.common.recycler.ItemModel
import com.dd.android.dailysimple.schedule.daily.viewmodel.HabitHeaderViewModel
import com.dd.android.dailysimple.schedule.daily.viewmodel.HabitsViewModel
import com.dd.android.dailysimple.schedule.daily.viewmodel.TodoViewModel


class DailyItemModels(fragment: Fragment) {

    private val todayTodoHeader = TodoHeaderItemModel(0, fragment.getString(R.string.today))
    private val todayVm by fragment.viewModels<TodoViewModel>()

    private val habitsVm by fragment.viewModels<HabitsViewModel>()
    private val habitHeaderVm by fragment.viewModels<HabitHeaderViewModel>()

    private val todayTodo get() = todayVm.todayTodo.value!!
    private val allHabits get() = habitsVm.allHabits.value!!
    private val habitHeader get() = habitHeaderVm.header.value!!

    val data = MediatorLiveData<List<ItemModel>>().apply {
        addSource(habitsVm.allHabits) {
            if (ensureVm()) value = createModel()
        }
        addSource(habitHeaderVm.header) {
            if (ensureVm()) value = createModel()
        }
        addSource(todayVm.todayTodo) {
            if (ensureVm()) value = createModel()
        }
    }

    private fun ensureVm() =
        todayVm.todayTodo.value != null &&
                habitsVm.allHabits.value != null &&
                habitHeaderVm.header.value != null

    private fun createModel(): List<ItemModel> =
        mutableListOf<ItemModel>().apply {
            add(todayTodoHeader)
            addAll(todayTodo)
            add(habitHeader)
            addAll(allHabits)
        }
}

data class TodoHeaderItemModel(
    override val id: Long,
    val headerTitle: String
) : ItemModel