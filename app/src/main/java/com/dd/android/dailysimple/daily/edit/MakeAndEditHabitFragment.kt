package com.dd.android.dailysimple.daily.edit

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.dd.android.dailysimple.HomeFragmentDirections
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.BaseFragment
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.utils.DateUtils
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

    override val layout: Int = R.layout.fragment_make_daily_habit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpModel()
        setUpToolbar()
        setUpColorPicker()
    }

    private fun setUpModel() {
        val habitId = arguments?.get(ARG_ID) as Long?

        habitId?.let {
            viewModel.getHabit(it).observe(viewLifecycleOwner, Observer { habitModel ->
                bind.habitModel = habitModel
                habitModel?.alarm?.let { alarm ->
                    alarmObservable.alarm = alarm
                    alarmObservable.isOn = true
                    logD { "Habit(${habitId}) alarm : $alarm" }
                }
            })
        }
        bind.alarmModel = alarmObservable
    }

    private fun setUpToolbar() {
        activity.setSupportActionBar(bind.toolbar)
        activity.supportActionBar?.apply {
            setDisplayShowCustomEnabled(false)
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = activity.getString(R.string.make_a_habit)
        }

        setHasOptionsMenu(true)
    }

    private fun setUpColorPicker() {
        bind.colorPicker.setOnClickListener {
            ColorPickerDialog.newBuilder()
                .create()
                .apply {
                    setColorPickerDialogListener(object : ColorPickerDialogListener {
                        override fun onDialogDismissed(dialogId: Int) {}
                        override fun onColorSelected(dialogId: Int, color: Int) {
                            Log.e("TEST-DH", color.toString())
                            bind.color.imageTintList = ColorStateList.valueOf(color)
                        }
                    })
                }
                .show(activity.supportFragmentManager, "color-picker-dialog")
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.create_daily_schedule_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.done -> {
                viewModel.insert(
                    DailyHabit(
                        id = bind.habitModel?.id ?: 0,
                        title = bind.titleEditor.text.toString(),
                        color = bind.color.imageTintList!!.defaultColor,
                        memo = bind.memoEditor.text.toString(),
                        startTime = DateUtils.today(),
                        finishTime = DateUtils.todayAfter(66),
                        alarm = alarmObservable.alarm
                    )
                )
                logD { "Alarm  : ${alarmObservable.alarm}" }
                popBackStack()
                true
            }
            R.id.cancel -> {
                popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

