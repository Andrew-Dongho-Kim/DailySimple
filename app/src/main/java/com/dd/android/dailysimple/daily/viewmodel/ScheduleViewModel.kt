package com.dd.android.dailysimple.daily.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.di.getString
import com.dd.android.dailysimple.daily.DailyConst.EMPTY_ITEM_ID_SCHEDULE
import com.dd.android.dailysimple.daily.DailyConst.SIMPLE_HEADER_ID_SCHEDULE
import com.dd.android.dailysimple.daily.DailyViewType.Companion.SCHEDULE_ITEM
import com.dd.android.dailysimple.daily.viewholders.DailyEmptyItemModel
import com.dd.android.dailysimple.daily.viewholders.DailySimpleHeaderItem
import com.dd.android.dailysimple.provider.calendar.CalendarProviderHelper

class ScheduleViewModel(app: Application) : AndroidViewModel(app) {
    private val calendarProvider by lazy { CalendarProviderHelper(app) }

    val header =
        liveData {
            emit(DailySimpleHeaderItem(SIMPLE_HEADER_ID_SCHEDULE, app.getString(R.string.schedule)))
        }

    val schedule =
        Transformations.map(calendarProvider.getTodayEvents()) { events ->
            if (events.isEmpty()) {
                listOf(
                    DailyEmptyItemModel(
                        EMPTY_ITEM_ID_SCHEDULE,
                        SCHEDULE_ITEM,
                        getString(R.string.no_schedule_message)
                    )
                )
            } else {
                events
            }
        }
}