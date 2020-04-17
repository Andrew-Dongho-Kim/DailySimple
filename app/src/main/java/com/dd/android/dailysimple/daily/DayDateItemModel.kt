package com.dd.android.dailysimple.daily

import com.dd.android.dailysimple.common.recycler.ItemModel

data class DayDateItemModel(
    override val id: Long,
    val year: String,
    val month: String,
    val date: String,
    val day: String,
    val holiday: Boolean
) : ItemModel