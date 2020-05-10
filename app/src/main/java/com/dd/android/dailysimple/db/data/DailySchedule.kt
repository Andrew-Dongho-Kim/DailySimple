package com.dd.android.dailysimple.db.data

import android.content.Context
import androidx.core.content.ContextCompat.getColor
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.di.getString
import com.dd.android.dailysimple.common.di.systemLocale
import com.dd.android.dailysimple.common.recycler.ItemModel
import com.dd.android.dailysimple.common.utils.DateUtils.MS_DAY
import com.dd.android.dailysimple.common.utils.DateUtils.msDateOnlyFrom
import com.dd.android.dailysimple.common.utils.DateUtils.toYMD
import java.text.SimpleDateFormat

data class DailySchedule(
    override val id: Long,
    val title: String,
    val start: Long,
    val end: Long,
    val memo: String? = null,
    val color: Int
) : ItemModel {

    private val locale = systemLocale()

    val formattedStart = toYMD(start, locale)

    val formattedEnd = toYMD(end, locale)

    val isAllDay = (end - start) == MS_DAY

    val beginTime: String =
        if (isAllDay) {
            getString(R.string.all_day)
        } else {
            SimpleDateFormat("hh:mm a", locale).format(start)
        }

    companion object {
        fun create(context: Context) = DailySchedule(
            id = NO_ID,
            title = "",
            memo = "",
            start = msDateOnlyFrom(hours = DEFAULT_START),
            end = msDateOnlyFrom(hours = DEFAULT_END),
            color = getColor(context, R.color.appPrimary)
        )

        private const val NO_ID = 0L

        private const val DEFAULT_START = 1
        private const val DEFAULT_END = 2
    }
}