package com.dd.android.dailysimple.daily.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.di.appDb
import com.dd.android.dailysimple.common.di.getString
import com.dd.android.dailysimple.common.utils.DateUtils.msDateOnlyFrom
import com.dd.android.dailysimple.daily.DailyConst.EMPTY_ITEM_ID_TODO
import com.dd.android.dailysimple.daily.DailyConst.OVERDUE_TODO_GROUP
import com.dd.android.dailysimple.daily.DailyConst.SIMPLE_HEADER_ID_TODO
import com.dd.android.dailysimple.daily.DailyConst.UPCOMING_TODO_GROUP
import com.dd.android.dailysimple.daily.DailyMergeItem
import com.dd.android.dailysimple.daily.DailyViewType.Companion.TODO_ITEM
import com.dd.android.dailysimple.daily.viewholders.DailyEmptyItemModel
import com.dd.android.dailysimple.daily.viewholders.DailySimpleHeaderItem
import com.dd.android.dailysimple.daily.viewholders.DailyTodoGroup
import com.dd.android.dailysimple.db.DailyTodoRepository
import com.dd.android.dailysimple.db.data.DailyTodo
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * As your data grows more complex, you might choose to have a separate
 * class just to load the data. The purpose of ViewModel is to encapsulate
 * the data for a UI controller to let the data survive configuration changes.
 */
class TodoViewModel(private val app: Application) : AndroidViewModel(app) {

    private val repository by lazy {
        val db = appDb()
        DailyTodoRepository(db.dailyTodoDao())
    }

    val header = liveData {
        emit(
            DailySimpleHeaderItem(SIMPLE_HEADER_ID_TODO, app.getString(R.string.todo))
        )
    }

    fun getTodo(todoId: Long) = repository.getTodoById(todoId)

    fun makeToDone(todoId: Long) = GlobalScope.launch {
        repository.makeToDone(todoId)
    }

    val selectedDate = liveData {
        emit(msDateOnlyFrom())
    } as MutableLiveData<Long>

    private val currTodo = Transformations.switchMap(selectedDate) { time ->
        repository.getTodoInDay(time)
    }

    private val isOverdueExpanded = liveData {
        emit(false)
    } as MutableLiveData<Boolean>

    private val overdueTodo = repository.overdueTodo()

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

    private val upcomingTodo = repository.upcomingTodo()

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

    val wholeTodo = Transformations.switchMap(selectedDate) { time ->
        Transformations.map(
            if (time == msDateOnlyFrom()) {
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
                    DailyEmptyItemModel(
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


    /**
     * A ViewModelScope id defined for each ViewModel in your app
     * Any coroutine launched in this scope is automatically canceled
     * if the ViewModel is cleared.
     * Coroutines are useful here for when you have work that needs to
     * be done only if the ViewModel is active.
     */
    fun insert(vararg todo: DailyTodo) = viewModelScope.launch {
        repository.insert(*todo)
    }

    fun update(vararg todo: DailyTodo) = viewModelScope.launch {
        repository.update(*todo)
    }

    fun delete(todoId: Long) = viewModelScope.launch {
        repository.delete(todoId)
    }
}
