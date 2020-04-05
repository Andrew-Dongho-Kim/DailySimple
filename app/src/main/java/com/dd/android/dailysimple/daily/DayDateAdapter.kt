package com.dd.android.dailysimple.daily

import android.content.Context
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.utils.DateUtils
import com.dd.android.dailysimple.common.recycler.ViewHolder2
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
    ViewHolder2(parent, R.layout.daily_habit_day_date_item, BR.itemModel)


class DayDateDataSource(
    private val context: Context
) : PageKeyedDataSource<Long, DayDateItemModel>() {

    private val defaultPageSize = 7

    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<Long, DayDateItemModel>
    ) {
        val cal = DateUtils.todayCalendar()
        val list = loadFrom(cal)
        callback.onResult(list, null, cal.timeInMillis)
    }

    override fun loadAfter(
        params: LoadParams<Long>,
        callback: LoadCallback<Long, DayDateItemModel>
    ) {
        val cal = Calendar.getInstance()
        cal.time = Date(params.key)
        val list = loadFrom(cal)

        callback.onResult(list, cal.timeInMillis)
    }

    override fun loadBefore(
        params: LoadParams<Long>,
        callback: LoadCallback<Long, DayDateItemModel>
    ) {
    }

    private fun loadFrom(cal: Calendar) =
        mutableListOf<DayDateItemModel>().apply {
            for (i in 0 until defaultPageSize) {
                val monthIndex = cal.get(Calendar.MONTH)
                val dayIndex = cal.get(Calendar.DAY_OF_WEEK)
                add(
                    DayDateItemModel(
                        cal.timeInMillis,
                        "${cal.get(Calendar.YEAR)}",
                        context.getString(MONTHS[monthIndex]),
                        "${cal.get(Calendar.DATE)}",
                        context.getString(DAYS[dayIndex - 1]),
                        dayIndex == Calendar.SUNDAY
                    )
                )
                cal.add(Calendar.DATE, -1)
            }
        }
}

val DAYS = listOf(
    R.string.sunday,
    R.string.monday,
    R.string.tuesday,
    R.string.wednesday,
    R.string.thursday,
    R.string.friday,
    R.string.saturday
)

val MONTHS = listOf(
    R.string.january,
    R.string.february,
    R.string.march,
    R.string.april,
    R.string.may,
    R.string.june,
    R.string.july,
    R.string.august,
    R.string.september,
    R.string.september,
    R.string.october,
    R.string.november,
    R.string.december
)