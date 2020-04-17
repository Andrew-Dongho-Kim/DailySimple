package com.dd.android.dailysimple.daily.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.di.appDb
import com.dd.android.dailysimple.daily.SimpleHeaderItemModel
import com.dd.android.dailysimple.daily.viewholders.DailyOverdueTodoGroup
import com.dd.android.dailysimple.db.DailyTodoRepository
import com.dd.android.dailysimple.db.data.DailyTodo
import kotlinx.coroutines.launch

/**
 * As your data grows more complex, you might choose to have a separate
 * class just to load the data. The purpose of ViewModel is to encapsulate
 * the data for a UI controller to let the data survive configuration changes.
 */
class TodoViewModel(app: Application) : AndroidViewModel(app) {

    private val repository by lazy {
        val db = appDb()
        DailyTodoRepository(db.dailyTodoDao())
    }

    val header = liveData {
        emit(
            SimpleHeaderItemModel(
                0,
                app.getString(R.string.todo)
            )
        )
    }

    fun getTodo(todoId: Long) = repository.getTodo(todoId)

    val todayTodo = repository.todayTodo

    val overdueTodo = repository.overdueTodo

    val overdueGroup = Transformations.map(overdueTodo) {
        DailyOverdueTodoGroup(0, it)
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

    fun update(vararg todo:DailyTodo) = viewModelScope.launch {
        repository.update(*todo)
    }
}
