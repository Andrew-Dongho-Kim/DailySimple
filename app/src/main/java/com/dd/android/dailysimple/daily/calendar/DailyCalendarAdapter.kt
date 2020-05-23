package com.dd.android.dailysimple.daily.calendar

import android.content.Context
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.dd.android.dailysimple.common.widget.recycler.ItemModel
import com.dd.android.dailysimple.common.widget.recycler.RecyclerViewAdapter2
import com.dd.android.dailysimple.common.widget.recycler.ViewHolder2
import com.dd.android.dailysimple.common.utils.DateUtils.calendarMonthOnly
import java.util.Calendar.*


fun build(context: Context): List<DailyCalendarItem> {
    val cal = calendarMonthOnly()
    var prev = cal.get(DAY_OF_WEEK) - 1
    cal.add(DAY_OF_WEEK, -prev)

    var total = 0
    val items = mutableListOf<DailyCalendarItem>()
    while (prev >= 0) {
        items.add(DailyCalendarItem(context, cal.timeInMillis, false))
        cal.add(DATE, 1)
        --prev
        ++total
    }
    val month = cal.get(MONTH)
    while (month == cal.get(MONTH)) {
        items.add(DailyCalendarItem(context, cal.timeInMillis, true))
        cal.add(DATE, 1)
        ++total
    }
    var next = (7 - (total % 7)) % 7
    while (next > 0) {
        items.add(DailyCalendarItem(context, cal.timeInMillis, false))
        cal.add(DATE, 1)
        --next
        ++total
    }
    return items;
}


class DailyCalendarAdapter(
    lifecycleOwner: LifecycleOwner
) : RecyclerViewAdapter2(lifecycleOwner) {

    override fun onCreateViewHolder2(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder2<out ViewDataBinding, out ItemModel> {
        return DailyCalendarItemViewHolder(parent).apply {
            bind.rows = items.size / 7
        }
    }
}




