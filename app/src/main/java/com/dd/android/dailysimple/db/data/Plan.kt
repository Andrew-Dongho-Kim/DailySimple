package com.dd.android.dailysimple.db.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


const val UNKNOWN_ID = -1L

@Entity(tableName = "plan")
data class Plan(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val type: String,
    @ColumnInfo(name = "start_date") val startDate: String,
    @ColumnInfo(name = "end_date") val endDate: String
)
