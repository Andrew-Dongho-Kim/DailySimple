package com.dd.android.dailysimple.common.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun today() = todayCalendar()
        .run {
        timeInMillis
    }

    fun todayAfter(date: Int) = todayCalendar()
        .run {
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
            set(Calendar.MILLISECOND, 0)
        }


    fun toYMD(date: Long, locale: Locale): String =
        SimpleDateFormat("yyyy. MM. dd", locale).format(Date(date))

    fun todayYMD(locale: Locale): String =
        toYMD(
            today(),
            locale
        )

    fun todayAfterYMD(date:Int, locale: Locale): String =
        toYMD(
            todayAfter(date),
            locale
        )
}