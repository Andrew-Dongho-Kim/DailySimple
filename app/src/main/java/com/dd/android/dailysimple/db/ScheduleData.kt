package com.dd.android.dailysimple.db

import androidx.annotation.IntDef
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.dd.android.dailysimple.common.recycler.ItemModel
import com.dd.android.dailysimple.db.Alarm.Days.Companion.FRI
import com.dd.android.dailysimple.db.Alarm.Days.Companion.MON
import com.dd.android.dailysimple.db.Alarm.Days.Companion.NONE
import com.dd.android.dailysimple.db.Alarm.Days.Companion.SAT
import com.dd.android.dailysimple.db.Alarm.Days.Companion.SUN
import com.dd.android.dailysimple.db.Alarm.Days.Companion.THU
import com.dd.android.dailysimple.db.Alarm.Days.Companion.TUE
import com.dd.android.dailysimple.db.Alarm.Days.Companion.WEN
import com.dd.android.dailysimple.db.Alarm.Power.Companion.NORMAL
import com.dd.android.dailysimple.db.Alarm.Power.Companion.STRONG
import com.dd.android.dailysimple.db.Alarm.Power.Companion.WEAK

const val UNKNOWN_ID = -1L

@Entity(tableName = "plan")
data class Plan(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val type: String,
    @ColumnInfo(name = "start_date") val startDate: String,
    @ColumnInfo(name = "end_date") val endDate: String
)

@Entity(tableName = "daily_habit")
data class DailyHabit(
    @PrimaryKey(autoGenerate = true) override val id: Long,
    @ForeignKey(
        entity = Plan::class,
        parentColumns = ["id"],
        childColumns = ["planId"],
        onDelete = CASCADE,
        onUpdate = CASCADE
    )
    val groupId: Long = UNKNOWN_ID,
    var title: String,
    var memo: String? = null,
    var color: Int,
    var startTime: Long,
    var finishTime: Long? = null,
    @Embedded var alarm: Alarm? = null
) : ItemModel {


}

data class Alarm(
    var repeat: Boolean = false,
    @ColumnInfo(name = "alarm_days") var alarmDays: Int = NONE,
    @ColumnInfo(name = "alarm_time") var alarmTime: Int = 0,
    @ColumnInfo(name = "alarm_power") var alarmPower: Int = NORMAL
) {

    @IntDef(WEAK, NORMAL, STRONG)
    annotation class Power {
        companion object {
            const val WEAK = 1
            const val NORMAL = 2
            const val STRONG = 3
        }
    }

    @IntDef(NONE, MON, TUE, WEN, THU, FRI, SAT, SUN)
    annotation class Days {
        companion object {
            const val NONE = 0
            const val MON = 1 shl 0
            const val TUE = 1 shl 1
            const val WEN = 1 shl 2
            const val THU = 1 shl 3
            const val FRI = 1 shl 4
            const val SAT = 1 shl 5
            const val SUN = 1 shl 6
        }
    }

    companion object {
        fun alarmDays(@Days vararg days: Int): Int {
            var alarm = 0
            days.forEach { alarm = alarm or it }
            return alarm
        }
    }
}

@Entity(tableName = "check_status", primaryKeys = ["date", "habit_id"])
data class CheckStatus(
    val date: Long,
    @ForeignKey(
        entity = DailyHabit::class,
        parentColumns = ["id"],
        childColumns = ["habit_id"],
        onDelete = CASCADE
    )
    @ColumnInfo(name = "habit_id") val habitId: Long,
    @ColumnInfo(name = "checked_count") val checkedCount: Int
) : ItemModel {

    @Ignore
    override val id = date
}