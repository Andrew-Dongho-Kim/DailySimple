package com.dd.android.dailysimple.daily.viewholders

import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.navigation.NavController
import com.dd.android.dailysimple.HomeFragmentDirections
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.widget.recycler.ViewHolder2
import com.dd.android.dailysimple.daily.edit.EditType
import com.dd.android.dailysimple.databinding.DailyScheduleItemBinding
import com.dd.android.dailysimple.db.data.DailySchedule

class DailyScheduleItemHolder(
    parent: ViewGroup,
    private val navController: NavController
) :
    ViewHolder2<DailyScheduleItemBinding, DailySchedule>(
        parent,
        R.layout.daily_schedule_item,
        BR.itemModel
    ) {

    init {
        itemClickListener = ::onItemClick
    }

    private fun onItemClick(view: View) {
        navController.navigate(
            HomeFragmentDirections.homeToMakeAndEdit(
                model!!.id,
                EditType.SCHEDULE
            )
        )
    }
}