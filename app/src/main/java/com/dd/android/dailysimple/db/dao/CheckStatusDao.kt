package com.dd.android.dailysimple.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dd.android.dailysimple.db.data.CheckStatus

@Dao
interface CheckStatusDao {

    @Query("SELECT * FROM check_status WHERE date=:date AND habit_id=:habitId")
    suspend fun getChecked(date: Long, habitId: Long): List<CheckStatus>

    @Query(
        "SELECT * FROM check_status WHERE habit_id=:habitId ORDER BY date DESC"
    )
    fun getAllChecked(habitId: Long): LiveData<List<CheckStatus>>

    @Transaction
    suspend fun toggleIt(date: Long, habitId: Long) {
        if (getChecked(date, habitId).isEmpty()) {
            insert(CheckStatus(date, habitId, 1))
        } else {
            delete(CheckStatus(date, habitId, 0))
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg checkStatus: CheckStatus): List<Long>

    @Query(
        """
            UPDATE check_status SET checked_count=:checkedCount 
             WHERE habit_id=:habitId AND date=:date"""
    )
    suspend fun update(habitId: Long, date: Long, checkedCount: Int)

    @Delete
    suspend fun delete(vararg checkStatus: CheckStatus): Int

}