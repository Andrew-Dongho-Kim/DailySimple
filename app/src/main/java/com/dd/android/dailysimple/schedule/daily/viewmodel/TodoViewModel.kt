package com.dd.android.dailysimple.schedule.daily.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dd.android.dailysimple.schedule.provider.calendar.CalendarProviderHelper

class TodoViewModel(app: Application) : AndroidViewModel(app) {
    private val calendarProvider by lazy { CalendarProviderHelper(app) }

    val todayTodo = calendarProvider.getTodayEvents()
}