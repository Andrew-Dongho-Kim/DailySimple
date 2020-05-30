package com.dd.android.dailysimple.daily.simplecalendar

import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.widget.recycler.ViewHolder2
import com.dd.android.dailysimple.daily.DayDateItemModel
import com.dd.android.dailysimple.databinding.DailyCalendarItemBinding

class SimpleCalendarViewHolder(
    parent: ViewGroup,
    private val scrollTo: scrollTo
) :
    ViewHolder2<DailyCalendarItemBinding, DayDateItemModel>(
        parent,
        R.layout.daily_calendar_item,
        BR.itemModel
    ) {

    init {
        itemClickListener = ::onClick
    }

    fun onClick(view: View) {
        model?.let { scrollTo(adapterPosition) }
    }
}
