package com.dd.android.dailysimple.schedule.common

import java.util.*

object DateUtils {

    fun today() = todayCalendar().run {
        timeInMillis
    }

    fun todayAfter(date: Int) = todayCalendar().run {
        add(Calendar.DATE, date)
        timeInMillis
    }

    fun todayCalendar() =
        Calendar.getInstance().apply {
            time = Date()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
}