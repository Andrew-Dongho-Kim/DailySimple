package com.dd.android.dailysimple.db.data

import androidx.room.*
import com.dd.android.dailysimple.common.recycler.ItemModel


@Entity(tableName = "daily_todo")
data class DailyTodo(
    @PrimaryKey(autoGenerate = true) override val id: Long,
    var title: String,
    var memo: String,
    var start: Long,
    var until: Long,
    @Embedded var alarm: Alarm? = null
) : ItemModel


@Entity(tableName = "daily_todo_sub_job")
data class DailyTodoSubJob(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sub_job_id")
    var subJobId: Long,
    @ForeignKey(
        entity = DailyTodo::class,
        parentColumns = ["id"],
        childColumns = ["daily_todo_id"],
        onDelete = ForeignKey.CASCADE
    )
    @ColumnInfo(name = "daily_todo_id")
    var dailyTodoId: Long,
    var title: String
)


