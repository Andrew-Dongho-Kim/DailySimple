package com.dd.android.dailysimple.schedule.daily

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.findNavController
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.schedule.HomeViewPagerFragmentDirections
import com.dd.android.dailysimple.schedule.common.recycler.RecyclerViewAdapter2
import com.dd.android.dailysimple.schedule.common.recycler.ViewHolder2
import com.dd.android.dailysimple.schedule.daily.DailyScheduleViewType.Companion.DAILY_COMMON
import com.dd.android.dailysimple.schedule.daily.DailyScheduleViewType.Companion.DAILY_HEADER
import com.dd.android.dailysimple.schedule.daily.DailyScheduleViewType.Companion.TODO_COMMON
import com.dd.android.dailysimple.schedule.daily.DailyScheduleViewType.Companion.TODO_HEADER
import com.dd.android.dailysimple.schedule.daily.viewmodel.HabitHeaderItemModel
import com.dd.android.dailysimple.schedule.provider.calendar.CalendarModel

@IntDef(DAILY_HEADER, DAILY_COMMON, TODO_HEADER, TODO_COMMON)
private annotation class DailyScheduleViewType {
    companion object {
        const val DAILY_HEADER = -1
        const val DAILY_COMMON = 0
        const val TODO_HEADER = -2
        const val TODO_COMMON = 1
    }
}

class DailyHabitHeaderHolder(parent: ViewGroup) : ViewHolder2(
    parent,
    R.layout.daily_schedule_header_item,
    BR.viewModel
)

class DailyHabitItemHolder(parent: ViewGroup) : ViewHolder2(
    parent,
    R.layout.daily_schedule_card_item,
    BR.viewModel
)

class DailyTodoHeaderHolder(parent: ViewGroup) : ViewHolder2(
    parent,
    R.layout.daily_todo_header
)

class DailyTodoItemHolder(parent: ViewGroup) : ViewHolder2(
    parent,
    R.layout.daily_todo,
    BR.viewModel
)

class DailyScheduleAdapter(lifecycleOwner: LifecycleOwner) : RecyclerViewAdapter2(lifecycleOwner) {

    override fun onCreateViewHolder2(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            TODO_HEADER -> DailyTodoHeaderHolder(parent)
            TODO_COMMON -> DailyTodoItemHolder(parent)
            DAILY_HEADER -> DailyHabitHeaderHolder(parent)
            else -> DailyHabitItemHolder(parent)
        }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TodoHeaderItemModel -> TODO_HEADER
            is CalendarModel -> TODO_COMMON
            is HabitHeaderItemModel -> DAILY_HEADER
            else -> DAILY_COMMON
        }
    }
}

class DailyScheduleCardItemClickListener : View.OnClickListener {
    override fun onClick(view: View) {
        view.findNavController().navigate(
            HomeViewPagerFragmentDirections.homeToCreateDailyScheduleFragment()
        )
    }
}