package com.dd.android.dailysimple.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dd.android.dailysimple.db.data.DailyTodo

@Dao
interface DailyTodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg todo: DailyTodo): List<Long>

    @Update
    suspend fun update(vararg todo: DailyTodo)

    @Query("DELETE FROM daily_todo WHERE id = :todoId")
    suspend fun delete(todoId: Long)

    @Query("SELECT * FROM daily_todo WHERE :todoId = id")
    fun getTodo(todoId: Long): LiveData<DailyTodo>

    @Query("UPDATE daily_todo SET done=${DailyTodo.DONE} WHERE :todoId = id")
    fun makeToDone(todoId: Long)

    @Query("SELECT * FROM daily_todo WHERE start<=:start AND :end <= until")
    fun getTodo(start: Long, end: Long): LiveData<List<DailyTodo>>

    @Query("SELECT * FROM daily_todo WHERE until <= :time AND done = ${DailyTodo.ONGOING}")
    fun getOverdueTodo(time: Long): LiveData<List<DailyTodo>>

}