package com.dd.android.dailysimple.daily

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.dd.android.dailysimple.common.CalendarConst.DAYS
import com.dd.android.dailysimple.common.CalendarConst.MONTHS
import com.dd.android.dailysimple.common.di.getString
import com.dd.android.dailysimple.common.utils.DateUtils.calendar
import com.dd.android.dailysimple.common.utils.DateUtils.msDateOnlyFrom
import com.dd.android.dailysimple.common.widget.recycler.ItemModel
import java.util.Calendar.*

data class DayDateItemModel(
    override val id: Long
) : ItemModel {

    val year = calendar(id).get(YEAR).toString()

    val month = getString(MONTHS[calendar(id).get(MONTH)])

    val date = calendar(id).get(DATE).toString()

    val day = getString(DAYS[calendar(id).get(DAY_OF_WEEK) - 1])

    val isToday = (id == msDateOnlyFrom())

    val isSelected = liveData {
        emit(false)
    } as MutableLiveData<Boolean>

}