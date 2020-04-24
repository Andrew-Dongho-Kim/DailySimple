package com.dd.android.dailysimple.provider.calendar


import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.di.getString
import com.dd.android.dailysimple.common.recycler.ItemModel
import com.dd.android.dailysimple.common.utils.DateUtils.MS_DAY
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class ScheduleItemModel(
    override val id: Long,
    val title: String,
    val begin: Date,
    val end: Date,
    val description: String? = null,
    val color: Int,
    val locale: Locale
) : ItemModel {

    val isAllDay = (end.time - begin.time) == MS_DAY

    val beginTime: String =
        if (isAllDay) {
            getString(R.string.all_day)
        } else {
            SimpleDateFormat("hh:mm a", locale).format(begin)
        }

}