package com.dd.android.dailysimple.daily.calendar

import android.app.Application
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.CalendarConst
import com.dd.android.dailysimple.common.utils.DateUtils.msDateFrom
import java.util.*
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR

class DailyCalendarViewModel(private val app: Application) : AndroidViewModel(app) {

    val selectedDate = liveData {
        emit(msDateFrom())
    } as MutableLiveData<Long>

    val year = Transformations.map(selectedDate) { time ->
        Calendar.getInstance().run {
            timeInMillis = time
            app.getString(R.string.year, get(YEAR))
        }
    }

    val month = Transformations.map(selectedDate) { time ->
        Calendar.getInstance().run {
            timeInMillis = time
            app.getString(CalendarConst.MONTHS[get(MONTH)])
        }
    }
}

@BindingAdapter("calendar:fillRows")
fun setRows(view: View, rows: Int) {
    val parent = view.parent as? ViewGroup

    Log.d("TEST-DH", "Rows :$rows, Parent:$parent, Height:${parent?.measuredHeight}")
    if (parent == null) {
        view.post { setRows(view, rows) }
    } else if (parent.measuredHeight == 0) {
        parent.post { setRows(view, rows) }
        Log.d("TEST-DH", "OMG : ${parent.measuredHeight}")
    } else {
        view.layoutParams.height = parent.measuredHeight / rows
        view.minimumHeight = parent.measuredHeight / rows
        Log.d("TEST-DH", "View minHeight : ${view.minimumHeight}")
    }
}