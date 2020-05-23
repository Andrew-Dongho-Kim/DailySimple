package com.dd.android.dailysimple.daily

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.dd.android.dailysimple.common.utils.DateUtils.msDateOnlyFrom
import java.util.*

data class SelectedDateInfo(
    val date: LiveData<Long>
) {
    val year = Transformations.map(date) {
        Calendar.getInstance().run {
            timeInMillis = it
            get(Calendar.YEAR)
        }
    }

    val month = Transformations.map(date) {
        Calendar.getInstance().run {
            timeInMillis = it
            get(Calendar.MONTH)
        }
    }

//    val formattedYearMonth =

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
