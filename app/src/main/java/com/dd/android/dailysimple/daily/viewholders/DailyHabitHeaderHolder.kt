package com.dd.android.dailysimple.daily.viewholders

import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.recycler.ItemModel
import com.dd.android.dailysimple.common.recycler.ViewHolder2
import com.dd.android.dailysimple.daily.DayDateAdapter
import com.dd.android.dailysimple.daily.DayDateItemModel
import com.dd.android.dailysimple.daily.viewmodel.HabitHeaderItemModel
import com.dd.android.dailysimple.databinding.DailyHabitHeaderItemBinding

class DailyHabitHeaderHolder(
    parent: ViewGroup, private val lifecycleOwner: LifecycleOwner
) :
    ViewHolder2<DailyHabitHeaderItemBinding, HabitHeaderItemModel>(
        parent,
        R.layout.daily_habit_header_item,
        BR.itemModel
    ) {

    private val adapter = DayDateAdapter(lifecycleOwner)

    private val layoutManager = LinearLayoutManager(parent.context)
        .apply {
            orientation = RecyclerView.HORIZONTAL
            reverseLayout = true
        }

    private val yearAndMonthDetector = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            model?.refresh(
                layoutManager.findFirstCompletelyVisibleItemPosition()
            )
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {}
    }

    val recyclerView = bind.checkedRecyclerView.apply {
        adapter = this@DailyHabitHeaderHolder.adapter
        layoutManager = this@DailyHabitHeaderHolder.layoutManager
        removeOnScrollListener(yearAndMonthDetector)
        addOnScrollListener(yearAndMonthDetector)
    }

    private val observer = Observer<PagedList<DayDateItemModel>> { adapter.submitList(it) }


    override fun bindTo(itemModel: ItemModel) {
        super.bindTo(itemModel)
       model?.also {
            it.dayDatePagedList.observe(lifecycleOwner, observer)
        }
    }
}