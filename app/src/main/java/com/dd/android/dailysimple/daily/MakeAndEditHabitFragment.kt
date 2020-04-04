package com.dd.android.dailysimple.daily

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.databinding.FragmentMakeDailyScheduleBinding
import com.dd.android.dailysimple.common.BaseFragment
import com.dd.android.dailysimple.daily.viewmodel.HabitViewModel
import com.dd.android.dailysimple.db.DailyHabit
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener

private const val ARG_ID = "dailyScheduleId"

class MakeAndEditHabitFragment : BaseFragment<FragmentMakeDailyScheduleBinding>() {

    private val viewModel by viewModels<HabitViewModel>()

    override val layout: Int = R.layout.fragment_make_daily_schedule

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpModel()
        setUpToolbar()
        setUpColorPicker()
    }

    private fun setUpModel() {
        (arguments?.get(ARG_ID) as? Long)?.let { id ->
            if (id == -1L) return

            viewModel.getHabit(id).observe(viewLifecycleOwner, Observer {
                bind.model = it
            })
        }
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
                viewModel.insert(
                    DailyHabit(
                        id = bind.model?.id ?: 0,
                        title = bind.titleEditor.text.toString(),
                        color = bind.colorPicker.imageTintList!!.defaultColor,
                        memo = bind.memoEditor.text.toString()
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