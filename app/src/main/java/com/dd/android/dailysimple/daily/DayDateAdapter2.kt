package com.dd.android.dailysimple.daily

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.recycler.ViewHolder2

class DayDateAdapter2(
    private val lifecycleOwner: LifecycleOwner
) : PagedListAdapter<DayDateItemModel, DayDateViewHolder2>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DayDateViewHolder2(parent).also {
            it.bind.lifecycleOwner = lifecycleOwner
        }

    override fun onBindViewHolder(holder: DayDateViewHolder2, position: Int) {
        holder.bind.setVariable(holder.variableId, getItem(position))
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

class DayDateViewHolder2(parent: ViewGroup) :
    ViewHolder2<ViewDataBinding, DayDateItemModel>(
        parent,
        R.layout.daily_calendar_item,
        BR.itemModel
    )
