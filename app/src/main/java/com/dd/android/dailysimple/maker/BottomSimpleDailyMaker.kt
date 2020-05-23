package com.dd.android.dailysimple.maker

import android.content.Context
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.dd.android.dailysimple.HomeFragmentDirections.Companion.homeToMakeAndEdit
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.daily.DailyConst.NO_ID
import com.dd.android.dailysimple.daily.edit.EditType
import com.dd.android.dailysimple.databinding.FabLayoutCommonBinding

class BottomSimpleDailyMaker(
    private val context: Context,
    private val fabBind: FabLayoutCommonBinding,
    private val fabVm: FabViewModel,
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

    private fun blockTouch(view: View) {}

    companion object {
        private val NAV_MAKE_AND_EDIT_HABIT = homeToMakeAndEdit(NO_ID, EditType.HABIT)
        private val NAV_MAKE_AND_EDIT_TODO = homeToMakeAndEdit(NO_ID, EditType.TODO)
        private val NAV_MAKE_AND_EDIT_SCHEDULE = homeToMakeAndEdit(NO_ID, EditType.SCHEDULE)

        private const val FAV_ACTIVE_ROT = 45f
        private const val FAB_INACTIVE_ROT = 0f

        private const val SIMPLE_MAKER_INACTIVE_TRANS_Y = 100f
        private const val SIMPLE_MAKER_ACTIVE_TRANS_Y = 0f
    }
}