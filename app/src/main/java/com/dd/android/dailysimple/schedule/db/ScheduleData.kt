package com.dd.android.dailysimple.schedule.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.dd.android.dailysimple.schedule.common.recycler.ItemModel

const val UNKNOWN_ID = -1L

@Entity(tableName = "group_schedule")
data class GroupSchedule(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val type: String,
    @ColumnInfo(name = "start_date") val startDate: String,
    @ColumnInfo(name = "end_date") val endDate: String
)

@Entity(tableName = "daily_habit")
data class DailyHabit(
    @PrimaryKey(autoGenerate = true) override val id: Long,
    @ForeignKey(
        entity = GroupSchedule::class,
        parentColumns = ["id"],
        childColumns = ["groupId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )
    val groupId: Long = UNKNOWN_ID,
    val title: String,
    val memo: String? = null,
    val color: Int
) : ItemModel

@Entity(tableName = "daily_checked")
data class DailyChecked(
    @PrimaryKey val date: Long,
    @ForeignKey(
        entity = DailyHabit::class,
        parentColumns = ["id"],
        childColumns = ["daily_id"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )
    @ColumnInfo(name = "daily_id") val id: Long,
    @ColumnInfo(name = "checked_count") val checkedCount: Int
)