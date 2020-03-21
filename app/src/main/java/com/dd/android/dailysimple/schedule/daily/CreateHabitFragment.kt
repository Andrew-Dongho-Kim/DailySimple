package com.dd.android.dailysimple.schedule.daily

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.databinding.FragmentCreateDailyScheduleBinding
import com.dd.android.dailysimple.schedule.common.BaseFragment
import com.dd.android.dailysimple.schedule.daily.viewmodel.HabitsViewModel
import com.dd.android.dailysimple.schedule.db.DailyHabit
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener

class CreateHabitFragment : BaseFragment<FragmentCreateDailyScheduleBinding>() {

    override val layout: Int = R.layout.fragment_create_daily_schedule

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initToolbar()
        initColorPicker()
    }

    private fun initToolbar() {
        activity.setSupportActionBar(bind.toolbar)
        activity.supportActionBar?.apply {
            setDisplayShowCustomEnabled(false)
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = activity.getString(R.string.make_a_habit)
        }
        setHasOptionsMenu(true)
    }

    private fun initColorPicker() {
        bind.colorPicker.setOnClickListener {
            ColorPickerDialog.newBuilder()
                .create()
                .apply {
                    setColorPickerDialogListener(object : ColorPickerDialogListener {
                        override fun onDialogDismissed(dialogId: Int) {}
                        override fun onColorSelected(dialogId: Int, color: Int) {
                            bind.colorPicker.imageTintList = ColorStateList.valueOf(color)
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
                ViewModelProvider(activity).get(HabitsViewModel::class.java).insert(
                    DailyHabit(
                        id = 0,
                        title = bind.titleEditor.text.toString(),
                        color = bind.colorPicker.imageTintList!!.defaultColor
                    )
                )
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