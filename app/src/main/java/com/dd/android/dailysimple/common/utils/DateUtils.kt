package com.dd.android.dailysimple.common.utils

import android.content.Context
import com.dd.android.dailysimple.R
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private const val MS_SECOND = 1000L

    private const val MS_MINUTE = MS_SECOND * 60

    private const val MS_HOUR = MS_MINUTE * 60

    const val MS_DAY = MS_HOUR * 24

    fun calendarFrom(time:Long) = Calendar.getInstance().apply {
        timeInMillis = time
    }

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


    fun msDateOnlyFrom(
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
            timeInMillis
        }

    fun msWeekOnlyFrom(timezone: TimeZone? = null) =
        calendarWeekOnly(timezone).run {
            timeInMillis
        }

    fun msMonthOnlyFrom(timezone: TimeZone? = null) =
        calendarMonthOnly(timezone).run {
            timeInMillis
        }

    fun msFrom(
        msTime: Long,
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

    @JvmOverloads
    fun msTimeOnlyFrom(hour: Int = 0, minute: Int = 0, second: Int = 0): Long =
        calendarTimeOnly().run {
            add(Calendar.HOUR_OF_DAY, hour)
            add(Calendar.MINUTE, minute)
            add(Calendar.SECOND, second)
            timeInMillis
        }

    fun msTimeNow() = System.currentTimeMillis()

    fun toRemain(context: Context, remain: Long): String {
        val left = remain > 0
        var time = (if (left) remain else -remain)
        val resources = context.resources
        val days = (time / MS_DAY).toInt()
        if (days > 0) {
            return "${resources.getQuantityString(
                R.plurals.plurals_day,
                days,
                days
            )} ${resources.getString(
                if (left) R.string.left else R.string.past
            )}"
        }

        time = (time % MS_DAY)
        val hours = time / MS_HOUR

        time %= MS_HOUR
        val minutes = time / MS_MINUTE
        if (hours > 0 || minutes >= 10) {
            return if (hours > 0) {
                String.format(
                    "%d${resources.getString(R.string.abb_hour)} %02d${resources.getString(
                        R.string.abb_minute
                    )} ${resources.getString(
                        if (left) R.string.left else R.string.past
                    )}", hours, minutes
                )
            } else {
                String.format(
                    "%02d${resources.getString(
                        R.string.abb_minute
                    )} ${resources.getString(
                        if (left) R.string.left else R.string.past
                    )}", minutes
                )
            }
        }

        time %= MS_MINUTE
        val seconds = time / MS_SECOND
        return String.format(
            "%d${resources.getString(R.string.abb_minute)} %02d${resources.getString(
                R.string.abb_second
            )} ${resources.getString(
                if (left) R.string.left else R.string.past
            )
            }", minutes, seconds
        )
    }

    suspend fun delayRemain(remain: Long) {
        delay(1000)
    }


    fun toTime(date: Long, locale: Locale): String =
        SimpleDateFormat("a hh : mm", locale).format(Date(date))

    fun toYMD(date: Long, locale: Locale): String =
        SimpleDateFormat("yyyy. MM. dd", locale).format(Date(date))

    fun strYmdToLong(text: String, locale: Locale) =
        SimpleDateFormat("yyyy. MM. dd", locale).parse(text)?.time ?: 0L

    fun todayYMD(locale: Locale): String =
        toYMD(
            msDateOnlyFrom(),
            locale
        )

    fun todayAfterYMD(date: Int, locale: Locale): String =
        toYMD(
            msDateOnlyFrom(date),
            locale
        )

}