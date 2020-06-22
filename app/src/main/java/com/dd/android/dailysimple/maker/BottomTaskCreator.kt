package com.dd.android.dailysimple.maker

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.dd.android.dailysimple.HomeFragmentDirections.Companion.homeToMakeAndEdit
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.daily.AppConst.NO_ID
import com.dd.android.dailysimple.daily.edit.EditType
import com.dd.android.dailysimple.daily.viewmodel.HabitViewModel
import com.dd.android.dailysimple.daily.viewmodel.ScheduleViewModel
import com.dd.android.dailysimple.daily.viewmodel.TodoViewModel
import com.dd.android.dailysimple.databinding.FabLayoutCommonBinding
import com.dd.android.dailysimple.db.data.DailyHabit
import com.dd.android.dailysimple.db.data.DailySchedule
import com.dd.android.dailysimple.db.data.DailyTodo

class BottomTaskCreator(
    private val context: Context,
    private val fabBind: FabLayoutCommonBinding,
    fabVm: FabViewModel,
    private val habitVm: HabitViewModel,
    private val todoVm: TodoViewModel,
    private val scheduleVm: ScheduleViewModel,
    private val viewLifecycleOwner: LifecycleOwner,
    private val navController: NavController
) {

    init {
        fabVm.setUpFab()
        fabVm.setUpEdit()
    }

    private fun FabViewModel.setUpEdit() {
        fabBind.simpleMaker.setOnFocusChangeListener { _, hasFocus ->
            isKeyboardOpened.postValue(hasFocus)
        }
    }

    private fun FabViewModel.setUpFab() {
        text1.postValue(context.getString(R.string.habit))
        text2.postValue(context.getString(R.string.todo))
        text3.postValue(context.getString(R.string.schedule))

        onFab1Click = { navController.navigate(NAV_MAKE_AND_EDIT_HABIT) }
        onFab2Click = { navController.navigate(NAV_MAKE_AND_EDIT_TODO) }
        onFab3Click = { navController.navigate(NAV_MAKE_AND_EDIT_SCHEDULE) }
        onFabAddClick = { makeSimply(selected.value ?: SIMPLY_MAKE_HABIT) }
        isOpen.observe(viewLifecycleOwner, Observer { opened ->
            with(fabBind) {
                fabAdd.animate().rotation(if (opened) FAV_ACTIVE_ROT else FAB_INACTIVE_ROT)

                val transY = if (opened) {
                    SIMPLE_MAKER_INACTIVE_TRANS_Y
                } else {
                    SIMPLE_MAKER_ACTIVE_TRANS_Y
                }
                simpleMaker.animate().translationY(transY)
                simpleMakerBackground.animate().translationY(transY)

                fabRoot.setOnClickListener(if (opened) ::blockTouch else null)
                fabRoot.isClickable = opened
            }
        })
    }

    private fun makeSimply(selected: Int) {
        val jobTitle = fabBind.simpleMaker.text.toString()
        if (jobTitle.isEmpty()) return

        when (selected) {
            SIMPLY_MAKE_HABIT -> {
                habitVm.insert(DailyHabit.create(context).apply { title = jobTitle })
            }
            SIMPLY_MAKE_TODO -> {
                todoVm.insertTodo(DailyTodo.create(context).apply { title = jobTitle })
            }
            SIMPLY_MAKE_SCHEDULE -> {
                scheduleVm.insertSchedule(DailySchedule.create(context).apply { title = jobTitle })
            }
        }
        with(fabBind.simpleMaker) {
            text = null
            hideSoftKey()
            clearFocus()
        }
    }

    private fun blockTouch(view: View) {}

    private fun hideSoftKey() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(fabBind.root.windowToken, 0)
    }

    companion object {
        private val NAV_MAKE_AND_EDIT_HABIT = homeToMakeAndEdit(NO_ID, EditType.HABIT)
        private val NAV_MAKE_AND_EDIT_TODO = homeToMakeAndEdit(NO_ID, EditType.TODO)
        private val NAV_MAKE_AND_EDIT_SCHEDULE = homeToMakeAndEdit(NO_ID, EditType.SCHEDULE)

        private const val SIMPLY_MAKE_HABIT = 0
        private const val SIMPLY_MAKE_TODO = 1
        private const val SIMPLY_MAKE_SCHEDULE = 2

        private const val FAV_ACTIVE_ROT = 45f
        private const val FAB_INACTIVE_ROT = 0f

        private const val SIMPLE_MAKER_INACTIVE_TRANS_Y = 100f
        private const val SIMPLE_MAKER_ACTIVE_TRANS_Y = 0f
    }
}