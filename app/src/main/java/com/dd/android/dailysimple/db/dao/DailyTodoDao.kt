package com.dd.android.dailysimple.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dd.android.dailysimple.common.utils.DateUtils.msFrom
import com.dd.android.dailysimple.db.data.DailyTodo
import com.dd.android.dailysimple.db.data.DoneState
import com.dd.android.dailysimple.db.data.TodoSubTask

@Dao
interface DailyTodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(vararg todo: DailyTodo): List<Long>

    @Update
    suspend fun updateTodo(vararg todo: DailyTodo)

    @Query("DELETE FROM daily_todo WHERE id = :todoId")
    suspend fun deleteTodo(todoId: Long)

    @Query("SELECT * FROM daily_todo WHERE :todoId = id")
    fun getTodoById(todoId: Long): LiveData<DailyTodo>

    @Query("SELECT * FROM daily_todo WHERE start<=:start AND :end <= until ORDER BY done ASC")
    fun getTodoRange(start: Long, end: Long): LiveData<List<DailyTodo>>

    @Query("SELECT * FROM daily_todo WHERE until <= :start AND done < :end ")
    fun getOverdueTodo(start: Long, end: Long = msFrom(start, dates = 1)): LiveData<List<DailyTodo>>

    @Query("SELECT * FROM daily_todo WHERE :start <= start AND start < :until")
    fun getUpcomingTodo(start: Long, until: Long): LiveData<List<DailyTodo>>

    @Query("UPDATE daily_todo SET done=:doneTime WHERE :todoId = id")
    fun makeToDone(todoId: Long, doneTime: Long)


    // Sub tasks function
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTask(vararg subtasks: TodoSubTask): List<Long>

    @Query("DELETE FROM todo_sub_task WHERE id=:subTaskId")
    suspend fun deleteSubTask(subTaskId: Long)

    @Query("SELECT * FROM todo_sub_task WHERE todo_id=:todoId ORDER BY done ASC")
    fun getSubTasks(todoId: Long): LiveData<List<TodoSubTask>>

}