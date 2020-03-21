package com.dd.android.dailysimple.schedule.common

import java.util.*

object DateUtils {

    fun today(): Long {
        val date = Date()
        return Date(date.year, date.month, date.date).time
    }

    fun todayAfter(day: Int): Long {
        val date = Date()
        return Date(date.year, date.month, date.date+day).time
    }

}