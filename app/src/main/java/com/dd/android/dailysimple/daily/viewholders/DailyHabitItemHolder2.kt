package com.dd.android.dailysimple.daily.viewholders

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import com.dd.android.dailysimple.BR
import com.dd.android.dailysimple.HomeFragmentDirections.Companion.homeToMakeAndEdit
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.widget.recycler.ViewHolder2
import com.dd.android.dailysimple.daily.simplecalendar.SelectedDateInfo
import com.dd.android.dailysimple.daily.edit.EditType
import com.dd.android.dailysimple.daily.viewmodel.HabitViewModel
import com.dd.android.dailysimple.databinding.DailyHabitItem2Binding
import com.dd.android.dailysimple.db.data.DailyHabitWithCheckStatus

class DailyHabitItemHolder2(
    parent: ViewGroup,
    viewModelStoreOwner: ViewModelStoreOwner,
    private val navController: NavController
) :
    ViewHolder2<DailyHabitItem2Binding, DailyHabitWithCheckStatus>(
        parent,
        R.layout.daily_habit_item_2,
        BR.itemModel
    ) {

    private val habitVm = ViewModelProvider(viewModelStoreOwner).get(
        HabitViewModel::class.java
    )

    init {
        itemClickListener = ::onItemClick
        bind.checked.setOnClickListener(::onCheckClick)
        bind.dateInfo =
            SelectedDateInfo(
                habitVm.selectedDate
            )
    }

    private fun onItemClick(view: View) {
        navController.navigate(homeToMakeAndEdit(model!!.id, EditType.HABIT))
    }

    private fun onCheckClick(view: View) {
        model?.let {
            habitVm.toggleIt(it.id)
        }
    }

}