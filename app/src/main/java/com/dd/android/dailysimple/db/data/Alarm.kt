package com.dd.android.dailysimple.db.data

import androidx.annotation.IntDef
import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.dd.android.dailysimple.common.di.systemLocale
import com.dd.android.dailysimple.common.utils.DateUtils.toTime
import com.dd.android.dailysimple.db.data.Alarm.Days.Companion.FRI
import com.dd.android.dailysimple.db.data.Alarm.Days.Companion.MON
import com.dd.android.dailysimple.db.data.Alarm.Days.Companion.NONE
import com.dd.android.dailysimple.db.data.Alarm.Days.Companion.SAT
import com.dd.android.dailysimple.db.data.Alarm.Days.Companion.SUN
import com.dd.android.dailysimple.db.data.Alarm.Days.Companion.THU
import com.dd.android.dailysimple.db.data.Alarm.Days.Companion.TUE
import com.dd.android.dailysimple.db.data.Alarm.Days.Companion.WEN
import com.dd.android.dailysimple.db.data.Alarm.Power.Companion.NORMAL
import com.dd.android.dailysimple.db.data.Alarm.Power.Companion.STRONG
import com.dd.android.dailysimple.db.data.Alarm.Power.Companion.WEAK

data class Alarm(
    var repeat: Boolean = false,
    @ColumnInfo(name = "alarm_days") var alarmDays: Int = NONE,
    @ColumnInfo(name = "alarm_time") var alarmTime: Long = 0,
    @ColumnInfo(name = "alarm_power") var alarmPower: Int = NORMAL
) {

    @Ignore
    fun formattedAlarmTime() = toTime(alarmTime, systemLocale())

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