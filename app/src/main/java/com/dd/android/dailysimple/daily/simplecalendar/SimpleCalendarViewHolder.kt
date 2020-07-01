package com.dd.android.dailysimple.daily.simplecalendar

import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.widget.recycler.ViewHolder2
import com.dd.android.dailysimple.daily.DayDateItemModel
import com.dd.android.dailysimple.databinding.DailyCalendarItemBinding

class SimpleCalendarViewHolder(
    parent: ViewGroup,
    viewModelStoreOwner: ViewModelStoreOwner
) :
    ViewHolder2<DailyCalendarItemBinding, DayDateItemModel>(
        parent,
        R.layout.daily_calendar_item,
        BR.itemModel
    ) {

    private val simpleCalendarVm =
        ViewModelProvider(viewModelStoreOwner).get(SimpleCalendarViewModel::class.java)

    init {
        itemClickListener = { onClick() }
    }

    fun onClick() {
        model?.let { simpleCalendarVm.selectedDate.postValue(it.id) }
    }
}
