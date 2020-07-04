package com.dd.android.dailysimple.db.data

import android.content.Context
import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.MutableLiveData
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.di.appContext
import com.dd.android.dailysimple.common.di.getString
import com.dd.android.dailysimple.common.di.systemLocale
import com.dd.android.dailysimple.common.utils.DateUtils.msDateFrom
import com.dd.android.dailysimple.common.utils.DateUtils.msFrom
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

    override val selected = MutableLiveData<Boolean>()

    private val locale by lazy { systemLocale() }
    private val hourMinuteFormat by lazy { SimpleDateFormat("hh:mm a", locale) }

    val formattedStart get() = toYMD(start, locale)
    val formattedEnd get() = toYMD(end, locale)

    val beginTime: String =
        if (isAllDay) {
            getString(R.string.all_day)
        } else {
            hourMinuteFormat.format(start)
        }

    val endTime: String = hourMinuteFormat.format(end)

    companion object {
        fun create(
            context: Context,
            title: String = "",
            memo: String = "",
            color: Int = getColor(context, R.color.appPrimary),
            start: Long = msDateFrom(hours = DEFAULT_START),
            end: Long = msFrom(start, hours = DEFAULT_END_FROM_START),
            isAllDay: Boolean = true
        ) = DailySchedule(
            id = NO_ID,
            title = title,
            memo = memo,
            color = color,
            start = start,
            end = end,
            isAllDay = isAllDay
        )

        val EMPTY = create(appContext())
        private const val NO_ID = 0L

        private const val DEFAULT_START = 1
        private const val DEFAULT_END_FROM_START = 1
    }
}