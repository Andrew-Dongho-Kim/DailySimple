package com.dd.android.dailysimple.db

import com.dd.android.dailysimple.common.utils.DateUtils.MS_DAY
import com.dd.android.dailysimple.common.utils.DateUtils.msDateOnlyFrom
import com.dd.android.dailysimple.db.dao.DailyTodoDao
import com.dd.android.dailysimple.db.data.DailyTodo

class DailyTodoRepository(
    private val dailyTodoDao: DailyTodoDao
) {

    fun overdueTodo() = dailyTodoDao.getOverdueTodo(msDateOnlyFrom())

    fun upcomingTodo() = dailyTodoDao.getTodo(msDateOnlyFrom(), msDateOnlyFrom(3))

    fun getTodoInDay(time: Long) = dailyTodoDao.getTodo(time, time + MS_DAY)

    fun getTodoById(todoId: Long) = dailyTodoDao.getTodo(todoId)

    fun makeToDone(todoId: Long) = dailyTodoDao.makeToDone(todoId)

    suspend fun insert(vararg todo: DailyTodo) =
        dailyTodoDao.insert(*todo)

    suspend fun update(vararg todo: DailyTodo) =
        dailyTodoDao.update(*todo)

    suspend fun delete(todoId: Long) =
        dailyTodoDao.delete(todoId)
}