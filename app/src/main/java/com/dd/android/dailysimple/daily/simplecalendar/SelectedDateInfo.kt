package com.dd.android.dailysimple.daily.simplecalendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.CalendarConst
import com.dd.android.dailysimple.common.di.getString
import com.dd.android.dailysimple.common.utils.DateUtils.msDateOnlyFrom
import java.util.*

data class SelectedDateInfo(
    val date: LiveData<Long>
) {
    
    val ymText = Transformations.map(date) {
        Calendar.getInstance().run {
            timeInMillis = it
            "${getString(R.string.year, get(Calendar.YEAR))} ${getString(
                CalendarConst.MONTHS[get(
                    Calendar.MONTH
                )]
            )}"
        }
    }

    val state = Transformations.map(date) {
        val today = msDateOnlyFrom()
        when {
            it == today -> TODAY
            it > today -> FUTURE
            else -> PAST
        }
    }

    companion object {
        const val TODAY = 0
        const val PAST = 1
        const val FUTURE = 2
    }
}