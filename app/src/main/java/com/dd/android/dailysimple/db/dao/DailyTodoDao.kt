package com.dd.android.dailysimple.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dd.android.dailysimple.db.data.DailyTodo

@Dao
interface DailyTodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg todoList: DailyTodo): List<Long>

    @Query("SELECT * FROM daily_todo WHERE :from <= start AND :until > until")
    fun getTodo(from: Long, until: Long): LiveData<List<DailyTodo>>

}