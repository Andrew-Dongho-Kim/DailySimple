package com.dd.android.dailysimple.schedule.daily.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.navigation.findNavController
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.schedule.HomeViewPagerFragmentDirections
import com.dd.android.dailysimple.schedule.common.recycler.ItemModel
import java.util.*

class HabitHeaderViewModel(application: Application) : AndroidViewModel(application) {

    val header = liveData {
        emit(HabitHeaderItemModel(application))
    }

    fun refresh() = header.value?.refresh()
}

data class HabitHeaderItemModel(private val app: Application, private val length: Int = 5) :
    ItemModel {
    override val id: Long = -1L
    private val _days = MutableLiveData<List<String>>()
    val days = liveData {
        emit(daysHeader())
        emitSource(_days)
    }

    private val _dates = MutableLiveData<List<String>>()
    val dates = liveData {
        emit(dateHeader())
        emitSource(_dates)
    }

    private val _month = MutableLiveData<String>()
    val month = liveData {
        emit(monthOfToday())
        emitSource(_month)
    }

    fun refresh() {
        _days.value = daysHeader()
        _dates.value = dateHeader()
        _month.value = monthOfToday()
    }

    private fun daysHeader() = mutableListOf<String>().apply {
        val calendar = Calendar.getInstance()
        var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        for (i in 0 until length) {
            dayOfWeek = (dayOfWeek - 1 + 7) % 7
            add(app.getString(DAYS[dayOfWeek]))
        }
    }

    private fun dateHeader() = mutableListOf<String>().apply {
        val calendar = Calendar.getInstance()
        for (i in 0 until length) {
            add("${calendar.get(Calendar.DATE)}")
            calendar.add(Calendar.DATE, -1)
        }
    }

    private fun monthOfToday(): String {
        val calendar = Calendar.getInstance()
        return app.getString(MONTHS[calendar.get(Calendar.MONTH)])
    }

    fun addDailySchedule(view: View) =
        view.findNavController().navigate(
            HomeViewPagerFragmentDirections.homeToCreateDailyScheduleFragment()
        )

}

val DAYS = listOf(
    R.string.sunday,
    R.string.monday,
    R.string.tuesday,
    R.string.wednesday,
    R.string.thursday,
    R.string.friday,
    R.string.saturday
)

val MONTHS = listOf(
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