package com.dd.android.dailysimple.daily

import android.content.Context
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.widget.recycler.ViewHolder2
import com.dd.android.dailysimple.common.utils.DateUtils
import java.util.*

class DayDateAdapter(
    private val lifecycleOwner: LifecycleOwner
) : PagedListAdapter<DayDateItemModel, DayDateViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DayDateViewHolder(parent).also {
            it.bind.lifecycleOwner = lifecycleOwner
        }

    override fun onBindViewHolder(holder: DayDateViewHolder, position: Int) {
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

class DayDateViewHolder(parent: ViewGroup) :
    ViewHolder2<ViewDataBinding, DayDateItemModel>(
        parent,
        R.layout.day_date_item,
        BR.itemModel
    ) {

}


private const val PAGE_SIZE = 7

class DayDateDataSource(
    private val context: Context
) : PageKeyedDataSource<Long, DayDateItemModel>() {


    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<Long, DayDateItemModel>
    ) {
        val cal = DateUtils.calendarDateOnly().apply {
            add(Calendar.DATE, 3)
        }
        val prevKey = cal.timeInMillis
        val list = fetch(cal, FetchDir.LEFT)
        callback.onResult(list, prevKey, cal.timeInMillis)
    }

    override fun loadAfter(
        params: LoadParams<Long>,
        callback: LoadCallback<Long, DayDateItemModel>
    ) =
        start(params.key).run {
            callback.onResult(fetch(this, FetchDir.LEFT), timeInMillis)
        }

    override fun loadBefore(
        params: LoadParams<Long>,
        callback: LoadCallback<Long, DayDateItemModel>
    ) =
        start(params.key).run {
            callback.onResult(fetch(this, FetchDir.RIGHT), timeInMillis)
        }

    private fun start(time: Long) =
        Calendar.getInstance().apply {
            timeInMillis = time
        }

    private fun fetch(cal: Calendar, fetchDir: FetchDir) =
        mutableListOf<DayDateItemModel>()
            .apply {
                var dir = if (fetchDir == FetchDir.LEFT) -1 else 1
                if (dir > 0) {
                    cal.add(Calendar.DATE, PAGE_SIZE)
                    dir = -dir
                }
                for (i in 0 until PAGE_SIZE) {
                    add(DayDateItemModel(cal.timeInMillis))
                    cal.add(Calendar.DATE, dir)
                }
                if (fetchDir == FetchDir.RIGHT) {
                    cal.add(Calendar.DATE, PAGE_SIZE)
                }
            }

    private enum class FetchDir {
        LEFT, RIGHT
    }
}

