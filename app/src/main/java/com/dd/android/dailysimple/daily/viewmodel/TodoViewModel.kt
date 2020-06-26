package com.dd.android.dailysimple.daily.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.di.appDb
import com.dd.android.dailysimple.common.di.getString
import com.dd.android.dailysimple.common.utils.DateUtils.msDateFrom
import com.dd.android.dailysimple.daily.AppConst.EMPTY_ITEM_ID_TODO
import com.dd.android.dailysimple.daily.AppConst.OVERDUE_TODO_GROUP
import com.dd.android.dailysimple.daily.AppConst.SIMPLE_HEADER_ID_TODO
import com.dd.android.dailysimple.daily.AppConst.UPCOMING_TODO_GROUP
import com.dd.android.dailysimple.daily.DailyMergeItem
import com.dd.android.dailysimple.daily.DailyViewType.Companion.TODO_ITEM
import com.dd.android.dailysimple.daily.viewholders.DailyEmptyItem
import com.dd.android.dailysimple.daily.viewholders.DailySimpleHeaderItem
import com.dd.android.dailysimple.daily.viewholders.DailyTodoGroup
import com.dd.android.dailysimple.db.DailyTodoRepository
import com.dd.android.dailysimple.db.data.DailyTodo
import com.dd.android.dailysimple.db.data.TodoSubTask
import kotlinx.coroutines.*

/**
 * As your data grows more complex, you might choose to have a separate
 * class just to load the data. The purpose of ViewModel is to encapsulate
 * the data for a UI controller to let the data survive configuration changes.
 */
class TodoViewModel(private val app: Application) : AndroidViewModel(app) {

    private val viewModelThreadScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val repository by lazy {
        DailyTodoRepository(appDb().dailyTodoDao())
    }

    val header = liveData {
        emit(DailySimpleHeaderItem(SIMPLE_HEADER_ID_TODO, app.getString(R.string.todo)))
    }

    fun getTodo(todoId: Long) = repository.getTodoById(todoId)

    fun makeToDone(todoId: Long) = GlobalScope.launch {
        repository.makeToDone(todoId)
    }

    val selectedDate = liveData {
        emit(msDateFrom())
    } as MutableLiveData<Long>

    private val distinctSelectedDate = Transformations.distinctUntilChanged(selectedDate)

    private val currTodo = Transformations.switchMap(distinctSelectedDate) { time ->
        repository.getTodoInDay(time)
    }

    private val isOverdueExpanded = liveData {
        emit(false)
    } as MutableLiveData<Boolean>

    private val overdueTodo = Transformations.switchMap(distinctSelectedDate) {
        repository.overdueTodo()
    }

    private val overdueGroup = Transformations.map(overdueTodo) {
        DailyTodoGroup(
            OVERDUE_TODO_GROUP,
            app.resources.getQuantityString(
                R.plurals.plurals_overdue_task_message,
                it.size,
                it.size
            ),
            isOverdueExpanded,
            it
        )
    }

    private val overdueGroupChild = Transformations.switchMap(overdueGroup) {
        it.listLiveData
    }

    private val isUpcomingExpanded = liveData {
        emit(false)
    } as MutableLiveData<Boolean>

    private val upcomingTodo = Transformations.switchMap(distinctSelectedDate) {
        repository.upcomingTodo()
    }

    private val upcomingGroup = Transformations.map(upcomingTodo) {
        DailyTodoGroup(
            UPCOMING_TODO_GROUP, app.resources.getQuantityString(
                R.plurals.plurals_upcoming_task_message,
                it.size,
                it.size
            ), isUpcomingExpanded, it
        )
    }

    private val upcomingGroupChild = Transformations.switchMap(upcomingGroup) {
        it.listLiveData
    }

    val wholeTodo = Transformations.switchMap(distinctSelectedDate) { time ->
        Log.d("TEST-DH", "WholeTodo selected date : $time")
        Transformations.map(
            if (time == msDateFrom()) {
                DailyMergeItem(
                    overdueGroup,
                    overdueGroupChild,
                    currTodo,
                    upcomingGroup,
                    upcomingGroupChild
                )
            } else {
                currTodo
            }
        ) { todoList ->
            if (todoList.isEmpty()) {
                listOf(
                    DailyEmptyItem(
                        EMPTY_ITEM_ID_TODO,
                        TODO_ITEM,
                        getString(R.string.no_todo_message)
                    )
                )
            } else {
                todoList
            }
        }
    }

    fun getSubTasks(todoId: Long) = repository.getSubTasksById(todoId)


    /**
     * A ViewModelScope id defined for each ViewModel in your app
     * Any coroutine launched in this scope is automatically canceled
     * if the ViewModel is cleared.
     * Coroutines are useful here for when you have work that needs to
     * be done only if the ViewModel is active.
     */
    fun insertTodo(vararg todo: DailyTodo): LiveData<List<Long>> {
        val ids = MutableLiveData<List<Long>>()
        viewModelThreadScope.launch {
            ids.postValue(repository.insertTodo(*todo))
        }
        return ids
    }

    fun insertSubTask(vararg subtasks: TodoSubTask) = viewModelThreadScope.launch {
        repository.insertSubTask(*subtasks)
    }

    fun deleteSubTask(subTaskId: Long) = viewModelThreadScope.launch {
        repository.deleteSubTask(subTaskId)
    }

    fun updateTodo(vararg todo: DailyTodo) = viewModelThreadScope.launch {
        repository.update(*todo)
    }

    fun deleteTodo(todoId: Long) = viewModelThreadScope.launch {
        repository.deleteTodo(todoId)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelThreadScope.cancel()
    }
}

