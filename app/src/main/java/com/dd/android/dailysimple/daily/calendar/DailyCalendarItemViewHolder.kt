package com.dd.android.dailysimple.daily.calendar

import android.view.ViewGroup
import com.dd.android.dailysimple.BR
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.widget.recycler.ViewHolder2
import com.dd.android.dailysimple.databinding.DailyCalendarGridItemBinding

class DailyCalendarItemViewHolder(
    private val parent: ViewGroup
) : ViewHolder2<DailyCalendarGridItemBinding, DailyCalendarItem>(
    parent,
    R.layout.daily_calendar_grid_item,
    BR.itemModel
) {

}