package com.dd.android.dailysimple.daily

import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.widget.recycler.ViewHolder2
import com.dd.android.dailysimple.daily.viewmodel.HabitViewModel
import com.dd.android.dailysimple.daily.viewmodel.ScheduleViewModel
import com.dd.android.dailysimple.daily.viewmodel.TodoViewModel
import com.dd.android.dailysimple.databinding.DailyCalendarItemBinding

typealias scrollTo = (Int) -> Unit

class DayDateAdapter2(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val scrollTo: scrollTo
) : PagedListAdapter<DayDateItemModel, DayDateViewHolder2>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DayDateViewHolder2(parent, viewModelStoreOwner, scrollTo).also {
            it.bind.lifecycleOwner = lifecycleOwner
        }

    override fun onBindViewHolder(holder: DayDateViewHolder2, position: Int) {
        holder.bindTo(getItem(position)!!)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position)!!.id
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<DayDateItemModel>() {
            override fun areItemsTheSame(
                oldItem: DayDateItemModel,
                newItem: DayDateItemModel
            ): Boolean =
                oldItem.id == newItem.id


            override fun areContentsTheSame(
                oldItem: DayDateItemModel,
                newItem: DayDateItemModel
            ): Boolean =
                oldItem == newItem
        }
    }
}

class DayDateViewHolder2(
    parent: ViewGroup,
    viewModelStoreOwner: ViewModelStoreOwner,
    private val scrollTo: scrollTo
) :
    ViewHolder2<DailyCalendarItemBinding, DayDateItemModel>(
        parent,
        R.layout.daily_calendar_item,
        BR.itemModel
    ) {

    private val scheduleVm = ViewModelProvider(viewModelStoreOwner).get(
        ScheduleViewModel::class.java
    )

    private val todoVm = ViewModelProvider(viewModelStoreOwner).get(
        TodoViewModel::class.java
    )

    private val habitVm = ViewModelProvider(viewModelStoreOwner).get(
        HabitViewModel::class.java
    )

    init {
        itemClickListener = ::onClick
    }

    fun onClick(view: View) {
        model?.let {
            val date = it.id
            scheduleVm.selectedDate.postValue(date)
            habitVm.selectedDate.postValue(date)
            todoVm.selectedDate.postValue(date)
            scrollTo(adapterPosition)

            it.isSelected.postValue(true)
            if (SELECTED != it) {
                SELECTED?.isSelected?.postValue(false)
                SELECTED = it
            }
        }
    }

    companion object {
        private var SELECTED: DayDateItemModel? = null
    }
}
