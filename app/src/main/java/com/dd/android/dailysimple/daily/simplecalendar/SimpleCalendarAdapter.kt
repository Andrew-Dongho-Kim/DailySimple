package com.dd.android.dailysimple.daily.simplecalendar

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.dd.android.dailysimple.daily.DayDateItemModel

class SimpleCalendarAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModelStoreOwner: ViewModelStoreOwner
) : PagedListAdapter<DayDateItemModel, SimpleCalendarViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        SimpleCalendarViewHolder(parent, viewModelStoreOwner)
            .also {
                it.bind.lifecycleOwner = lifecycleOwner
            }

    override fun onBindViewHolder(holder: SimpleCalendarViewHolder, position: Int) {
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

