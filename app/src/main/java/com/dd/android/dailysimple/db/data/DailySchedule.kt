package com.dd.android.dailysimple.db.data

import android.content.Context
import androidx.core.content.ContextCompat.getColor
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.di.getString
import com.dd.android.dailysimple.common.di.systemLocale
import com.dd.android.dailysimple.common.utils.DateUtils.msDateFrom
import com.dd.android.dailysimple.common.utils.DateUtils.toYMD
import com.dd.android.dailysimple.common.widget.recycler.ItemModel
import java.text.SimpleDateFormat

data class DailySchedule(
    override val id: Long,
    var title: String,
    val start: Long,
    val isAllDay: Boolean,
    val end: Long,
    val memo: String? = null,
    val color: Int
) : ItemModel {

    private val locale by lazy { systemLocale() }
    private val hourMinuteFormat by lazy { SimpleDateFormat("hh:mm a", locale) }

    val formattedStart = toYMD(start, locale)
    val formattedEnd = toYMD(end, locale)

    val beginTime: String =
        if (isAllDay) {
            getString(R.string.all_day)
        } else {
            hourMinuteFormat.format(start)
        }

    val endTime: String = hourMinuteFormat.format(end)

    companion object {
        fun create(context: Context? = null) = DailySchedule(
            id = NO_ID,
            title = "",
            memo = "",
            start = msDateFrom(hours = DEFAULT_START),
            end = msDateFrom(hours = DEFAULT_END),
            isAllDay = true,
            color = context?.let { getColor(it, R.color.appPrimary) } ?: 0
        )

        val EMPTY = create()
        private const val NO_ID = 0L

        private const val DEFAULT_START = 1
        private const val DEFAULT_END = 2
    }
}