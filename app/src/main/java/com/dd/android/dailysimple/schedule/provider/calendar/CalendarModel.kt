package com.dd.android.dailysimple.schedule.provider.calendar

import com.dd.android.dailysimple.schedule.common.recycler.ItemModel
import java.text.SimpleDateFormat
import java.util.*

data class CalendarModel(
    override val id: Long,
    val title: String,
    val begin: Date,
    val end: Date,
    val description: String? = null,
    val color: Int,
    val locale: Locale
) : ItemModel {

    val beginTime: String =
        SimpleDateFormat("hh:mm a", locale).format(begin)
}