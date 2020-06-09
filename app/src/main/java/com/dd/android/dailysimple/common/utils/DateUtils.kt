package com.dd.android.dailysimple.common.utils

import android.content.Context
import android.text.format.Time
import android.util.Log
import com.dd.android.dailysimple.R
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    const val MS_SECOND = 1000L
    const val MS_MINUTE = MS_SECOND * 60
    const val MS_HOUR = MS_MINUTE * 60
    const val MS_DAY = MS_HOUR * 24

    val TIME_ZONE_UTC: TimeZone by lazy { TimeZone.getTimeZone("UTC") }


    fun localToUTC(time: Long): Long {
        try {
            val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss").apply {
                timeZone = TIME_ZONE_UTC
            }

            val strDate = dateFormat.format(Date(time))
            val dateFormatLocal = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
            val utcDate = dateFormatLocal.parse(strDate)

            Log.d("TEST-DH", "$utcDate")
            return utcDate?.time ?: time
        } catch (e: Exception) {
        }
        return time
    }

    fun utcToLocal(utcTime: Long): Long {
        try {
            val timeFormat = Time()
            timeFormat.set(utcTime + TimeZone.getDefault().getOffset(utcTime))
            return timeFormat.toMillis(true)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return utcTime
    }

    fun calendar(time: Long): Calendar = Calendar.getInstance().apply {
        timeInMillis = time
    }

    fun year(time: Long) = calendar(time).get(Calendar.YEAR)

    fun month(time: Long) = calendar(time).get(Calendar.MONTH)


    fun calendarDateOnly(timezone: TimeZone? = null): Calendar =
        Calendar.getInstance(timezone ?: TimeZone.getDefault()).apply {
            time = Date()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

    fun calendarWeekOnly(timezone: TimeZone? = null): Calendar =
        calendarDateOnly().apply {
            set(Calendar.DAY_OF_WEEK, 0)
        }

    fun calendarMonthOnly(timeZone: TimeZone? = null): Calendar =
        calendarDateOnly().apply {
            set(Calendar.DAY_OF_MONTH, 0)
        }


    fun msYmd(year: Int, month: Int, date: Int) =
        calendarDateOnly().run {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DATE, date)
            timeInMillis
        }


    fun msDateFrom(
        date: Int = 0,
        hours: Int = 0,
        minutes: Int = 0,
        seconds: Int = 0,
        timezone: TimeZone? = null
    ) =
        calendarDateOnly(timezone).run {
            add(Calendar.DATE, date)
            add(Calendar.HOUR_OF_DAY, hours)
            add(Calendar.MINUTE, minutes)
            add(Calendar.SECOND, seconds)
            set(Calendar.MILLISECOND, 0)
            timeInMillis
        }

    fun msWeekFrom(timezone: TimeZone? = null) =
        calendarWeekOnly(timezone).run {
            timeInMillis
        }

    fun msMonthFrom(timezone: TimeZone? = null) =
        calendarMonthOnly(timezone).run {
            timeInMillis
        }

    fun msFrom(
        msTime: Long = System.currentTimeMillis(),
        months: Int = 0,
        weeks: Int = 0,
        dates: Int = 0,
        hours: Int = 0,
        minutes: Int = 0,
        seconds: Int = 0
    ) =
        Calendar.getInstance().run {
            timeInMillis = msTime
            add(Calendar.MONTH, months)
            add(Calendar.WEEK_OF_YEAR, weeks)
            add(Calendar.DATE, dates)
            add(Calendar.HOUR_OF_DAY, hours)
            add(Calendar.MINUTE, minutes)
            add(Calendar.SECOND, seconds)
            timeInMillis
        }

    fun firstDayOfWeek(msTime: Long) =
        Calendar.getInstance().run {
            timeInMillis = msTime
            set(Calendar.DAY_OF_WEEK, 0)
            timeInMillis
        }

    fun firstDayOfMonth(msTime: Long) =
        Calendar.getInstance().run {
            timeInMillis = msTime
            set(Calendar.DAY_OF_MONTH, 0)
            timeInMillis
        }


    fun getDate(date: Date) = Calendar.getInstance().run {
        time = date
        get(Calendar.DATE)
    }

    fun calendarTimeOnly(): Calendar =
        Calendar.getInstance().apply {
            time = Date()
            set(Calendar.YEAR, 0)
            set(Calendar.MONTH, 0)
            set(Calendar.DATE, 0)
        }


    fun msTimeNow() = System.currentTimeMillis()

    fun toTime(date: Long, locale: Locale): String =
        SimpleDateFormat("a hh : mm", locale).format(Date(date))

    fun toYMD(date: Long, locale: Locale): String =
        SimpleDateFormat("yyyy. MM. dd", locale).format(Date(date))

    fun strYmdToLong(text: String, locale: Locale) =
        SimpleDateFormat("yyyy. MM. dd", locale).parse(text)?.time ?: 0L

    fun todayYMD(locale: Locale): String =
        toYMD(
            msDateFrom(),
            locale
        )

    fun todayAfterYMD(date: Int, locale: Locale): String =
        toYMD(
            msDateFrom(date),
            locale
        )

}