package com.dd.android.dailysimple.daily.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.recycler.ItemModel
import com.dd.android.dailysimple.provider.calendar.CalendarProviderHelper

class TodoViewModel(app: Application) : AndroidViewModel(app) {
    private val calendarProvider by lazy { CalendarProviderHelper(app) }

    val todayHeader = TodoHeaderItemModel(0, app.getString(R.string.today))

    val todayTodo = calendarProvider.getTodayEvents()
}

data class TodoHeaderItemModel(
    override val id: Long,
    val headerTitle: String
) : ItemModel