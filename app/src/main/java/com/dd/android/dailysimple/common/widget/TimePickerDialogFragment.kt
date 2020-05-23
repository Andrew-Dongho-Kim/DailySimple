package com.dd.android.dailysimple.common.widget

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.widget.CalendarView
import android.widget.TimePicker
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.BaseDialogFragment
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.databinding.FragmentTimePickerBinding
import java.util.*


private const val TAG = "TimePickerDialogFragment"

private const val ARGS_CURR_DATE = "args_curr_date"
private const val ARGS_MIN_DATE = "args_min_date"
private const val ARGS_USE_TIME = "args_use_time"

private const val ARGS_USE_DATE = "args_use_date"

private const val UNUSED_LONG = -1L

private inline fun logD(crossinline message: () -> String) =
    Logger.d(
        TAG,
        message
    )

class TimePickerDialogFragment : BaseDialogFragment<FragmentTimePickerBinding>() {

    override val layout = R.layout.fragment_time_picker

    private var year = 0
    private var month = 0
    private var dayOfMonth = 0
    private var hourOfDay = 0
    private var minutes = 0

    private val selectedTime = MutableLiveData<Long>()
    private var observer: Observer<Long>? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.ui = this

        setUpData()
        setUpTime()
        observer?.let { selectedTime.observe(viewLifecycleOwner, it) }
    }

    private fun setUpData() {
        arguments?.let { args ->
            args.letLongIfNotDefault(ARGS_CURR_DATE) { bind.calendar.date = it }
            args.letLongIfNotDefault(ARGS_MIN_DATE) { bind.calendar.minDate = it }
            args.letIfFalse(ARGS_USE_TIME) { bind.time.visibility = GONE }
            args.letIfFalse(ARGS_USE_DATE) { bind.calendar.visibility = GONE }
        }
    }

    private fun Bundle.letLongIfNotDefault(key: String, block: (Long) -> Unit) {
        val value = getLong(key,
            UNUSED_LONG
        )
        if (value != UNUSED_LONG) {
            block(value)
        }
    }

    private fun Bundle.letIfFalse(key: String, block: (Boolean) -> Unit) {
        val value = getBoolean(key, true)
        if (!value) {
            block(value)
        }
    }

    private fun setUpTime() {
        val cal = Calendar.getInstance().apply {
            timeInMillis = bind.calendar.date
        }
        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH)
        dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
        hourOfDay = bind.time.hour
        minutes = bind.time.minute
        logD {
            "TimePicked : $year, $month, $dayOfMonth, $hourOfDay, $minutes"
        }
    }

    fun show(
        fragmentManager: FragmentManager,
        observer: Observer<Long>
    ) {
        this.observer = observer
        show(fragmentManager, TAG)
    }

    fun onSelectedDateChanged(calendar: CalendarView, year: Int, month: Int, dayOfMonth: Int) {
        this.year = year
        this.month = month
        this.dayOfMonth = dayOfMonth
        logD { "Date Changed : $year, $month, $dayOfMonth" }
    }

    fun onTimeChanged(timePicker: TimePicker, hourOfDay: Int, minutes: Int) {
        this.hourOfDay = hourOfDay
        this.minutes = minutes
        logD { "Time Changed : $hourOfDay, $minutes" }
    }


    fun onDoneClicked() {
        selectedTime.postValue(
            Calendar.getInstance().run {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minutes)
                timeInMillis
            }
        )
        dismiss()
    }

    fun onCancelClicked() {
        dismiss()
    }

    companion object {

        fun with(
            minDate: Long? = null,
            currDate: Long? = null,
            useDate: Boolean? = null,
            useTime: Boolean? = null
        ) =
            TimePickerDialogFragment().apply {
                with(Bundle()) {
                    minDate?.let { putLong(ARGS_MIN_DATE, it) }
                    currDate?.let { putLong(ARGS_CURR_DATE, it) }
                    useDate?.let { putBoolean(ARGS_USE_DATE, it) }
                    useTime?.let { putBoolean(ARGS_USE_TIME, it) }
                    arguments = this
                }
            }
    }
}