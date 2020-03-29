package com.dd.android.dailysimple.schedule.common

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun today() = todayCalendar().run {
        timeInMillis
    }

    fun todayAfter(date: Int) = todayCalendar().run {
        add(Calendar.DATE, date)
        timeInMillis
    }

    fun getDate(date: Date) = Calendar.getInstance().run {
        time = date
        get(Calendar.DATE)
    }

    fun todayCalendar(): Calendar =
        Calendar.getInstance().apply {
            time = Date()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

    fun toYearMonthDate(date: Date, locale: Locale) =
        SimpleDateFormat("yyyy. MM. DD", locale).format(date)
}