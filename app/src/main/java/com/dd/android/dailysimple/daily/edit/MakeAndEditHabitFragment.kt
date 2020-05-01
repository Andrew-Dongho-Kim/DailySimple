package com.dd.android.dailysimple.daily.edit

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.BaseFragment
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.TimePickerDialogFragment
import com.dd.android.dailysimple.common.di.systemLocale
import com.dd.android.dailysimple.common.setUnderlineText
import com.dd.android.dailysimple.common.utils.DateUtils
import com.dd.android.dailysimple.common.utils.DateUtils.msDateOnlyFrom
import com.dd.android.dailysimple.common.utils.DateUtils.strYmdToLong
import com.dd.android.dailysimple.daily.edit.observable.AlarmObservable
import com.dd.android.dailysimple.daily.viewmodel.HabitViewModel
import com.dd.android.dailysimple.databinding.FragmentMakeDailyHabitBinding
import com.dd.android.dailysimple.db.data.Alarm
import com.dd.android.dailysimple.db.data.DailyHabit
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener


private const val TAG = "MakeAndEdit"

private const val ARG_ID = "habitId"

private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class MakeAndEditHabitFragment : BaseFragment<FragmentMakeDailyHabitBinding>() {

    private val alarmObservable = AlarmObservable(Alarm())

    private val viewModel by viewModels<HabitViewModel>()

    private lateinit var habitModel: DailyHabit

    override val layout: Int = R.layout.fragment_make_daily_habit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpDataBinding()
        setStatusBarColor(R.color.basic_view_background)
    }

    private fun setUpDataBinding() {
        val habitId = arguments?.get(ARG_ID) as Long
        viewModel.getHabit(habitId).observe(viewLifecycleOwner, Observer { model ->
            habitModel = (model ?: DailyHabit.create(requireContext())).apply {
                alarm = ensureAlarm(habitId, alarm)
            }
            bind.habitModel = habitModel
            logD { "habitId : $habitId, model:$habitModel" }
        })
        bind.alarmModel = alarmObservable
        bind.ui = this
    }

    private fun ensureAlarm(habitId: Long, alarm: Alarm?) = alarm?.also { it ->
        alarmObservable.alarm = it
        alarmObservable.isOn = true
        logD { "Habit(${habitId}) alarm : $it" }
    } ?: alarmObservable.alarm


    fun onColorPickerClick(view: View) {
        ColorPickerDialog.newBuilder()
            .create()
            .apply {
                setColorPickerDialogListener(object : ColorPickerDialogListener {
                    override fun onDialogDismissed(dialogId: Int) {}
                    override fun onColorSelected(dialogId: Int, color: Int) {
                        Log.e("TEST-DH", Integer.toHexString(color))
                        bind.color.background = ColorDrawable(color)
                        habitModel.color = color
                    }
                })
            }
            .show(activity.supportFragmentManager, "color-picker-dialog")
    }

    fun onDatePickClick(view: View) {
        val tv = view as TextView
        TimePickerDialogFragment.with(
            minDate = msDateOnlyFrom(),
            currDate = strYmdToLong(tv.text.toString(), systemLocale()),
            useTime = false
        ).show(
            parentFragmentManager,
            Observer {
                tv.setUnderlineText(DateUtils.toYMD(it, systemLocale()))
            }
        )
    }

    fun onTimePickClick(view: View) {
        val tv = view as TextView
        TimePickerDialogFragment.with(
            useDate = false
        ).show(
            parentFragmentManager,
            Observer {
                tv.setUnderlineText(DateUtils.toTime(it, systemLocale()))
                alarmObservable.alarmTime = it
            }
        )
    }

    fun onDoneClick() {
        if (bind.titleEditor.text.isNullOrEmpty()) {
            return
        }

        viewModel.insert(
            DailyHabit(
                id = habitModel.id,
                title = bind.titleEditor.text.toString(),
                color = habitModel.color,
                memo = bind.memoEditor.text.toString(),
                start = strYmdToLong(bind.startDate.text.toString(), systemLocale()),
                until = strYmdToLong(bind.endDate.text.toString(), systemLocale()),
                alarm = alarmObservable.alarm
            )
        )
        logD { "Alarm  : ${alarmObservable.alarm}" }
        popBackStack()
    }

    fun onCancelClick() {
        popBackStack()
    }

}

