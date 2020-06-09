package com.dd.android.dailysimple.daily.calendar

import android.content.Context
import androidx.core.content.ContextCompat
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.widget.recycler.ItemModel
import com.dd.android.dailysimple.common.utils.DateUtils.msDateFrom
import java.util.*
import java.util.Calendar.*

data class DailyCalendarItem(
    val context: Context,
    val msTime: Long,
    val isCurrMonth: Boolean
) : ItemModel {
    override val id = msTime

    private val dayOfWeek = Calendar.getInstance().run {
        timeInMillis = msTime
        get(DAY_OF_WEEK)
    }

    private val isToday = msTime == msDateFrom()

    val date = Calendar.getInstance().run {
        timeInMillis = msTime
        get(DATE).toString()
    }

    val alpha = if (isCurrMonth) NORMAL_ALPHA else DIM_ALPHA

    val textColor = ContextCompat.getColor(
        context, if (isToday) TODAY_COLOR_RES else when (dayOfWeek) {
            SUNDAY -> SUNDAY_COLOR_RES
            SATURDAY -> SATURDAY_COLOR_RES
            else -> WEEKDAY_COLOR_RES
        }
    )

    val textBackgroundColor = ContextCompat.getColor(
        context,
        if (isToday) TODAY_BACKGROUND_COLOR_RES else NORMAL_BACKGROUND_COLOR_RES
    )

    companion object {
        const val NORMAL_ALPHA = 1.0f
        const val DIM_ALPHA = 0.4f

        const val NORMAL_BACKGROUND_COLOR_RES = android.R.color.transparent
        const val TODAY_BACKGROUND_COLOR_RES = R.color.appPrimary

        const val TODAY_COLOR_RES = R.color.appWhite
        const val WEEKDAY_COLOR_RES = R.color.basic_text1_color
        const val SUNDAY_COLOR_RES = R.color.apple
        const val SATURDAY_COLOR_RES = R.color.blue
    }
}