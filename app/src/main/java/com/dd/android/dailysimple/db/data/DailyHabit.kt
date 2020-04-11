package com.dd.android.dailysimple.db.data

import androidx.room.*
import com.dd.android.dailysimple.common.recycler.ItemModel


@Entity(tableName = "daily_habit")
data class DailyHabit(
    @PrimaryKey(autoGenerate = true) override val id: Long,
    @ForeignKey(
        entity = Plan::class,
        parentColumns = ["id"],
        childColumns = ["planId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )
    val groupId: Long = UNKNOWN_ID,
    var title: String,
    var memo: String? = null,
    var color: Int,
    var startTime: Long,
    var finishTime: Long? = null,
    @Embedded var alarm: Alarm? = null
) : ItemModel


@Entity(tableName = "check_status", primaryKeys = ["date", "habit_id"])
data class CheckStatus(
    val date: Long,
    @ForeignKey(
        entity = DailyHabit::class,
        parentColumns = ["id"],
        childColumns = ["habit_id"],
        onDelete = ForeignKey.CASCADE
    )
    @ColumnInfo(name = "habit_id") val habitId: Long,
    @ColumnInfo(name = "checked_count") val checkedCount: Int
) : ItemModel {

    @Ignore
    override val id = date
}