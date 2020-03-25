package com.dd.android.dailysimple.schedule.daily

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dd.android.dailysimple.schedule.common.DateUtils
import com.dd.android.dailysimple.schedule.daily.viewmodel.MONTHS
import java.util.*

class DailyHabitHeader(parent: ViewGroup) : DailyHabitHeaderHolder(parent) {

    init {

    }

}

class DayDateAdapter : PagedListAdapter<DateDayItemModel, DayDateViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayDateViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: DayDateViewHolder, position: Int) {
        TODO("Not yet implemented")
    }


    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<DateDayItemModel>() {
            override fun areItemsTheSame(
                oldItem: DateDayItemModel,
                newItem: DateDayItemModel
            ): Boolean =
                oldItem.id == newItem.id


            override fun areContentsTheSame(
                oldItem: DateDayItemModel,
                newItem: DateDayItemModel
            ): Boolean =
                oldItem == newItem
        }
    }
}

class DayDateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}

private const val DAY_DATE_PAGE_SIZE = 7

class DayDateDataSource(
    private val context: Context
) : PageKeyedDataSource<Long, DateDayItemModel>() {

    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<Long, DateDayItemModel>
    ) {
        val cal = DateUtils.todayCalendar()
        callback.onResult(
            loadFrom(cal), null, cal.timeInMillis
        )
    }

    override fun loadAfter(
        params: LoadParams<Long>,
        callback: LoadCallback<Long, DateDayItemModel>
    ) {
        val cal = Calendar.getInstance()
        cal.time = Date(params.key)

        callback.onResult(loadFrom(cal), cal.timeInMillis)
    }

    override fun loadBefore(
        params: LoadParams<Long>,
        callback: LoadCallback<Long, DateDayItemModel>
    ) {
    }

    private fun loadFrom(cal: Calendar) =
        mutableListOf<DateDayItemModel>().apply {
            for (i in 0 until DAY_DATE_PAGE_SIZE) {
                add(
                    DateDayItemModel(
                        cal.timeInMillis,
                        context.getString(MONTHS[cal.get(Calendar.MONTH)]),
                        cal.get(Calendar.DATE),
                        context.getString(cal.get(Calendar.DAY_OF_WEEK))
                    )
                )
                cal.set(Calendar.DATE, -1)
            }
        }

}