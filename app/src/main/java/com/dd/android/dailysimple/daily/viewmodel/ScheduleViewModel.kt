package com.dd.android.dailysimple.daily.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.di.getString
import com.dd.android.dailysimple.common.utils.DateUtils
import com.dd.android.dailysimple.common.utils.DateUtils.msFrom
import com.dd.android.dailysimple.daily.DailyConst.EMPTY_ITEM_ID_SCHEDULE
import com.dd.android.dailysimple.daily.DailyConst.SIMPLE_HEADER_ID_SCHEDULE
import com.dd.android.dailysimple.daily.DailyViewType.Companion.SCHEDULE_ITEM
import com.dd.android.dailysimple.daily.viewholders.DailyEmptyItemModel
import com.dd.android.dailysimple.daily.viewholders.DailySimpleHeaderItem
import com.dd.android.dailysimple.db.data.DailySchedule
import com.dd.android.dailysimple.provider.calendar.CalendarProviderHelper

class ScheduleViewModel(app: Application) : AndroidViewModel(app) {
    private val calendarProvider by lazy { CalendarProviderHelper(app) }

    val selectedDate = liveData {
        emit(DateUtils.msDateOnlyFrom())
    } as MutableLiveData<Long>

    val header =
        liveData {
            emit(DailySimpleHeaderItem(SIMPLE_HEADER_ID_SCHEDULE, app.getString(R.string.schedule)))
        }

    val schedule = Transformations.switchMap(selectedDate) { time ->
        Transformations.map(calendarProvider.getEvents(time, msFrom(time, dates = 1))) { events ->
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

    fun insertSchedule(vararg schedule: DailySchedule) {

    }

}