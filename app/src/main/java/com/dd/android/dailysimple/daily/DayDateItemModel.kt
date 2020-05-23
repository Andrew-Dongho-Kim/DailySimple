package com.dd.android.dailysimple.daily

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.di.getString
import com.dd.android.dailysimple.common.widget.recycler.ItemModel
import com.dd.android.dailysimple.common.utils.DateUtils.calendarFrom
import com.dd.android.dailysimple.common.utils.DateUtils.msDateOnlyFrom
import java.util.Calendar.*

data class DayDateItemModel(
    override val id: Long
) : ItemModel {

    val year = calendarFrom(id).get(YEAR).toString()

    val month = getString(MONTHS[calendarFrom(id).get(MONTH)])

    val date = calendarFrom(id).get(DATE).toString()

    val day = getString(DAYS[calendarFrom(id).get(DAY_OF_WEEK) - 1])

    val isToday = (id == msDateOnlyFrom())

    val isSelected = liveData {
        emit(false)
    } as MutableLiveData<Boolean>

    companion object {

        private val DAYS = listOf(
            R.string.sunday,
            R.string.monday,
            R.string.tuesday,
            R.string.wednesday,
            R.string.thursday,
            R.string.friday,
            R.string.saturday
        )

        private val MONTHS = listOf(
            R.string.january,
            R.string.february,
            R.string.march,
            R.string.april,
            R.string.may,
            R.string.june,
            R.string.july,
            R.string.august,
            R.string.september,
            R.string.september,
            R.string.october,
            R.string.november,
            R.string.december
        )
    }

}