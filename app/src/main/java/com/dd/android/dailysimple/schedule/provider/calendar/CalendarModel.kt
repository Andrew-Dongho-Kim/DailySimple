package com.dd.android.dailysimple.schedule.provider.calendar

import com.dd.android.dailysimple.schedule.common.recycler.ItemModel

data class CalendarModel(
    override val id: Long,
    val title: String,
    val begin: Long,
    val end: Long,
    val description: String? = null,
    val color: Int
) : ItemModel