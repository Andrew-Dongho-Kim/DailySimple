package com.dd.android.dailysimple.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dd.android.dailysimple.db.data.KeyValue

@Dao
interface KeyValueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg keyValue: KeyValue): List<Long>

    @Query("DELETE FROM key_value WHERE `key`=:key")
    suspend fun delete(key: String)

    @Query("SELECT * FROM key_value WHERE `key`=:key")
    suspend fun get(key:String) : LiveData<KeyValue>
}