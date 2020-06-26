package com.dd.android.dailysimple.daily

import androidx.lifecycle.*
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.widget.recycler.ItemModel
import com.dd.android.dailysimple.daily.viewmodel.HabitViewModel
import com.dd.android.dailysimple.daily.viewmodel.ScheduleViewModel
import com.dd.android.dailysimple.daily.viewmodel.TodoViewModel

private const val TAG = "DailyItemModels"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class DailyItemModels(viewModelStoreOwner: ViewModelStoreOwner) : ViewModel() {

    private val scheduleVm by lazy {
        ViewModelProvider(viewModelStoreOwner).get(ScheduleViewModel::class.java)
    }
    private val todoVm by lazy {
        ViewModelProvider(viewModelStoreOwner).get(TodoViewModel::class.java)
    }
    private val habitsVm by lazy {
        ViewModelProvider(viewModelStoreOwner).get(HabitViewModel::class.java)
    }

    private val models = arrayOf(
        scheduleVm.header,
        scheduleVm.schedule,
        todoVm.header,
        todoVm.wholeTodo,
        habitsVm.header,
        habitsVm.allHabits
    )

    private var waitUpdates = mutableListOf<LiveData<*>>(*models)

    val data = MediatorLiveData<List<ItemModel>>().apply {
        models.forEach { liveData ->
            addSource(liveData) {
                dumpWho(it)
                if (needUpdates(liveData)) {
                    value = createModel()
                }
            }
        }
    }

    private fun dumpWho(obj: Any) {
        val clazz = if (obj is List<*>) {
            if (obj.size > 0) obj[0]?.javaClass?.simpleName else obj.javaClass.simpleName
        } else {
            obj.javaClass.simpleName
        }
        logD { "updated : $clazz" }
    }

    fun postDate(date: Long) {
        scheduleVm.selectedDate.postValue(date)
        todoVm.selectedDate.postValue(date)
        habitsVm.selectedDate.postValue(date)
        waitUpdates = mutableListOf(scheduleVm.schedule, todoVm.wholeTodo, habitsVm.allHabits)
    }

    private fun needUpdates(source: LiveData<*>): Boolean {
        waitUpdates.remove(source)
        return waitUpdates.size == 0
    }

    private fun createModel(): List<ItemModel> =
        mutableListOf<ItemModel>().apply {
            models.forEach { liveData ->
                val model = liveData.value!!

                if (model is List<*>) {
                    model.forEach {
                        addItemModel(this, it)
                    }
                } else {
                    addItemModel(this, model)
                }
            }
            logD { "Daily - createModel()" }
        }

    private fun addItemModel(itemList: MutableList<ItemModel>, item: Any?) {
        if (item is DailyExpandableItem) {
            if (item.children.isNotEmpty())
                itemList.add(item)
        } else if (item is ItemModel) {
            itemList.add(item)
        }
    }
}
