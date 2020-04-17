package com.dd.android.dailysimple.db

import com.dd.android.dailysimple.common.utils.DateUtils.msDateOnlyFrom
import com.dd.android.dailysimple.common.utils.DateUtils.msTimeNow
import com.dd.android.dailysimple.db.dao.DailyTodoDao
import com.dd.android.dailysimple.db.data.DailyTodo

class DailyTodoRepository(
    private val dailyTodoDao: DailyTodoDao
) {

    val todayTodo = dailyTodoDao.getOngoingTodo(msDateOnlyFrom(date = 1))

    val overdueTodo = dailyTodoDao.getOverdueTodo(msTimeNow())

    fun getTodo(todoId: Long) = dailyTodoDao.getTodo(todoId)

    suspend fun insert(vararg todo: DailyTodo) =
        dailyTodoDao.insert(*todo)

    suspend fun update(vararg todo: DailyTodo) =
        dailyTodoDao.update(*todo)

    suspend fun delete(todoId: Long) =
        dailyTodoDao.delete(todoId)
}