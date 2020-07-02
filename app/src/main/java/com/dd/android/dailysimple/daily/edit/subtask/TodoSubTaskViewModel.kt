package com.dd.android.dailysimple.daily.edit.subtask

import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.dd.android.dailysimple.BR
import com.dd.android.dailysimple.common.di.appDb
import com.dd.android.dailysimple.db.DailyTodoRepository
import com.dd.android.dailysimple.db.data.DoneState
import com.dd.android.dailysimple.db.data.TodoSubTask

class TodoSubTaskViewModel(private val todoId: Long) : ViewModel() {

    private val repository by lazy {
        DailyTodoRepository(appDb().dailyTodoDao())
    }

    private val inMemorySubTasks = mutableListOf<EditableTodoSubTask>()

    private val _liveDataSubTasks: MutableLiveData<List<EditableTodoSubTask>> =
        Transformations.map(repository.getSubTasksById(todoId)) { subTasks ->

            val editableTasks = subTasks.mapIndexed { idx, subTask ->
                createEditableTask(subTask, idx)
            }

            inMemorySubTasks.removeAll { old -> editableTasks.find { new -> old.id == new.id } != null }
            inMemorySubTasks.addAll(editableTasks)

            subTaskId = (inMemorySubTasks.maxBy { it.id }?.id ?: 0L) + 1
            sortTasks()
            inMemorySubTasks as List<EditableTodoSubTask>

        } as MutableLiveData
    val liveDataSubTasks = _liveDataSubTasks

    // It would be used newly created task for temporary id because of stableId
    private var subTaskId = 0L

    private val onPropertyChanged = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            if (propertyId == BR.done) {
                sortTasks()
                _liveDataSubTasks.postValue(inMemorySubTasks)
            }
        }
    }

    private fun createEditableTask(subTask: TodoSubTask, position: Int, added: Boolean = false) =
        EditableTodoSubTask(subTask, position).apply {
            addOnPropertyChangedCallback(onPropertyChanged)
        }

    fun newTask(): EditableTodoSubTask {
        val insertPos = inMemorySubTasks.indexOfFirst { DoneState.isDone(it.done) }
        val subTask = createEditableTask(TodoSubTask(subTaskId++, todoId), insertPos, added = true)

        inMemorySubTasks.add(if (insertPos < 0) inMemorySubTasks.size else insertPos, subTask)
        _liveDataSubTasks.postValue(inMemorySubTasks)
        return subTask
    }

    suspend fun saveTasks(newTodoId: Long) {
        inMemorySubTasks
            .filter { (it.added || it.edited) && it.title.isNotEmpty() }
            .map {
                it.task.todoId = newTodoId
                if (it.added) it.task.id = 0L
                it.task
            }.forEach {
                repository.insertSubTask(it)
            }
    }

    private fun sortTasks() {
        val sorted = inMemorySubTasks.sortedWith(Comparator { lhs, rhs ->
            val diff = (lhs.done - rhs.done).toInt()
            if (diff == 0) {
                (lhs.id - rhs.id).toInt()
            } else {
                diff
            }
        })
        inMemorySubTasks.clear()
        inMemorySubTasks.addAll(sorted)
    }
}