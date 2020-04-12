package com.dd.android.dailysimple.daily.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.provider.calendar.CalendarProviderHelper

class ScheduleViewModel(app: Application) : AndroidViewModel(app) {
    private val calendarProvider by lazy { CalendarProviderHelper(app) }

    val header = SimpleHeaderItemModel(0, app.getString(R.string.schedule))

    val schedule = calendarProvider.getTodayEvents()
}

