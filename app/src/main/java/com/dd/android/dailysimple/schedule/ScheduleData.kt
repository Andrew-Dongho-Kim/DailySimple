package com.dd.android.dailysimple.schedule

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

const val UNKNOWN_ID = -1

@Entity(tableName = "group_schedule")
data class GroupSchedule(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val type: String,
    @ColumnInfo(name = "start_date") val startDate: String,
    @ColumnInfo(name = "end_date") val endDate: String
)

@Fts4
@Entity(tableName ="daily_schedule")
data class DailySchedule(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "rowid") val rowid: Int,
    @ForeignKey(
        entity = GroupSchedule::class,
        parentColumns = ["id"],
        childColumns = ["pid"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    ) val pid: Int = UNKNOWN_ID,
    val title: String,
    val description: String
)

@Entity(tableName = "group_daily_schedule_join", primaryKeys = ["groupId", "sid"])
data class GroupDailyScheduleJoin(
    @ForeignKey(entity = GroupSchedule::class, parentColumns = ["id"], childColumns = ["groupId"])
    val groupId: Int,

    @ForeignKey(entity = DailySchedule::class, parentColumns = ["rowid"], childColumns = ["sId"])
    val sId: Int
)

