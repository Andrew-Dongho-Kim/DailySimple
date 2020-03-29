package com.dd.android.dailysimple.schedule.daily

data class DayDateItemModel(
    val id: Long,
    val year: String,
    val month: String,
    val date: String,
    val day: String,
    val holiday: Boolean
)