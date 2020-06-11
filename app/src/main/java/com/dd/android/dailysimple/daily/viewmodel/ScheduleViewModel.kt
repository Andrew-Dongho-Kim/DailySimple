package com.dd.android.dailysimple.daily.viewmodel

import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.di.getString
import com.dd.android.dailysimple.common.utils.DateUtils.msDateFrom
import com.dd.android.dailysimple.common.utils.DateUtils.msFrom
import com.dd.android.dailysimple.daily.AppConst.AUTHORITY_ITEM_ID_SCHEDULE
import com.dd.android.dailysimple.daily.AppConst.EMPTY_ITEM_ID_SCHEDULE
import com.dd.android.dailysimple.daily.AppConst.SIMPLE_HEADER_ID_SCHEDULE
import com.dd.android.dailysimple.daily.DailyViewType.Companion.SCHEDULE_ITEM
import com.dd.android.dailysimple.daily.viewholders.DailyAuthorityItem
import com.dd.android.dailysimple.daily.viewholders.DailyEmptyItem
import com.dd.android.dailysimple.daily.viewholders.DailySimpleHeaderItem
import com.dd.android.dailysimple.db.data.DailySchedule
import com.dd.android.dailysimple.provider.calendar.CalendarProviderHelper

class ScheduleViewModel(private val app: Application) : AndroidViewModel(app) {
    private val calendarProvider by lazy { CalendarProviderHelper(app) }

    val selectedDate = liveData {
        emit(msDateFrom())
    } as MutableLiveData<Long>

    val header =
        liveData {
            emit(DailySimpleHeaderItem(SIMPLE_HEADER_ID_SCHEDULE, app.getString(R.string.schedule)))
        }

    val schedule = Transformations.switchMap(selectedDate) { time ->
        Transformations.map(calendarProvider.getEvents(time, msFrom(time, dates = 1))) { events ->
            if (events.isEmpty()) {
                listOf(if (hasCalendarPermission()) createEmptyItem() else createAuthorityItem())
            } else {
                events
            }
        }
    }

    fun insertSchedule(vararg schedule: DailySchedule) {

    }

    fun refresh() {
        selectedDate.postValue(selectedDate.value)
    }

    private fun createEmptyItem() = DailyEmptyItem(
        EMPTY_ITEM_ID_SCHEDULE,
        SCHEDULE_ITEM,
        getString(R.string.no_schedule_message)
    )

    private fun createAuthorityItem() = DailyAuthorityItem(
        AUTHORITY_ITEM_ID_SCHEDULE,
        getString(R.string.no_permission_to_read_calendar)
    )

    private fun hasCalendarPermission() =
        ContextCompat.checkSelfPermission(
            app,
            android.Manifest.permission.READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
}