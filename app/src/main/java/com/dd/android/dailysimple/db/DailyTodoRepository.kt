package com.dd.android.dailysimple.db

import com.dd.android.dailysimple.common.utils.DateUtils.MS_DAY
import com.dd.android.dailysimple.common.utils.DateUtils.msDateFrom
import com.dd.android.dailysimple.common.utils.DateUtils.msFrom
import com.dd.android.dailysimple.db.dao.DailyTodoDao
import com.dd.android.dailysimple.db.data.DailyTodo
import com.dd.android.dailysimple.db.data.TodoSubTask

class DailyTodoRepository(
    private val dailyTodoDao: DailyTodoDao
) {

    fun overdueTodo() = dailyTodoDao.getOverdueTodo(msDateFrom())

    fun upcomingTodo() = dailyTodoDao.getUpcomingTodo(msDateFrom(1), msDateFrom(3))

    fun getTodoInDay(time: Long) = dailyTodoDao.getTodoRange(time, time + MS_DAY)

    fun getTodoById(todoId: Long) = dailyTodoDao.getTodoById(todoId)

    fun makeToDone(todoId: Long) = dailyTodoDao.makeToDone(todoId, msFrom())

    fun getSubTasksById(todoId: Long) = dailyTodoDao.getSubTasks(todoId)

    suspend fun insertTodo(vararg todo: DailyTodo) =
        dailyTodoDao.insertTodo(*todo)

    suspend fun insertSubTask(vararg subtask: TodoSubTask) =
        dailyTodoDao.insertSubTask(*subtask)

    suspend fun update(vararg todo: DailyTodo) =
        dailyTodoDao.updateTodo(*todo)

    suspend fun deleteTodo(todoId: Long) =
        dailyTodoDao.deleteTodo(todoId)

    suspend fun deleteSubTask(subTaskId: Long) =
        dailyTodoDao.deleteSubTask(subTaskId)
}