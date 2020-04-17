package com.dd.android.dailysimple.daily

import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.recycler.ItemModel
import com.dd.android.dailysimple.common.recycler.RecyclerViewAdapter2
import com.dd.android.dailysimple.common.recycler.ViewHolder2
import com.dd.android.dailysimple.daily.DailyScheduleViewType.Companion.HABIT_HEADER
import com.dd.android.dailysimple.daily.DailyScheduleViewType.Companion.HABIT_ITEM
import com.dd.android.dailysimple.daily.DailyScheduleViewType.Companion.OVERDUE_TODO_GROUP
import com.dd.android.dailysimple.daily.DailyScheduleViewType.Companion.SCHEDULE_ITEM
import com.dd.android.dailysimple.daily.DailyScheduleViewType.Companion.SIMPLE_HEADER
import com.dd.android.dailysimple.daily.DailyScheduleViewType.Companion.TODO_ITEM
import com.dd.android.dailysimple.daily.viewholders.*
import com.dd.android.dailysimple.daily.viewmodel.HabitHeaderItemModel
import com.dd.android.dailysimple.db.data.DailyHabit
import com.dd.android.dailysimple.db.data.DailyTodo
import com.dd.android.dailysimple.provider.calendar.ScheduleItemModel
import java.util.*

private const val TAG = "DailyAdapter"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

private const val RECYCLER_VIEW_CACHE_SIZE = 10

@IntDef(HABIT_HEADER, SIMPLE_HEADER, SCHEDULE_ITEM, TODO_ITEM, HABIT_ITEM)
private annotation class DailyScheduleViewType {
    companion object {
        const val SIMPLE_HEADER = -1
        const val HABIT_HEADER = -2
        const val SCHEDULE_ITEM = 0
        const val TODO_ITEM = 1
        const val HABIT_ITEM = 2
        const val OVERDUE_TODO_GROUP = 3
    }
}

fun RecyclerView.setUpCache() {
    setItemViewCacheSize(RECYCLER_VIEW_CACHE_SIZE)

    with(recycledViewPool) {
        setMaxRecycledViews(HABIT_HEADER, 1)
        setMaxRecycledViews(SIMPLE_HEADER, 2)
    }
}

class SimpleHeaderItemHolder(parent: ViewGroup) :
    ViewHolder2<ViewDataBinding, SimpleHeaderItemModel>(
        parent,
        R.layout.simple_header_item,
        BR.model
    )


class DailyScheduleItemHolder(parent: ViewGroup) :
    ViewHolder2<ViewDataBinding, ScheduleItemModel>(
        parent,
        R.layout.daily_schedule_item,
        BR.viewModel
    )

class DailyAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val navController: NavController
) :
    RecyclerViewAdapter2(lifecycleOwner) {

    private val slaveScrollHolders =
        LinkedList<SlaveScrollViewHolder<out ViewDataBinding, out ItemModel>>()

    private val sharedScrollStatus = SharedScrollStatus()


    private val slaveScroll = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            with(sharedScrollStatus) {
                absoluteScrollDx += dx
                absoluteScrollDy += dy
            }

            slaveScrollHolders.forEach { holder ->
                holder.recycler.scrollBy(dx, dy)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            slaveScrollHolders.forEach {
                it.isMasterScrolling = newState != RecyclerView.SCROLL_STATE_IDLE
            }
        }
    }

    override fun onCreateViewHolder2(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder2<out ViewDataBinding, out ItemModel> {
        logD { "+onCreateViewHolder(#$viewType)" }
        return when (viewType) {
            SIMPLE_HEADER -> SimpleHeaderItemHolder(parent)
            HABIT_HEADER -> DailyHabitHeaderHolder(parent, lifecycleOwner).also {
                it.recyclerView.addOnScrollListener(slaveScroll)
            }
            SCHEDULE_ITEM -> DailyScheduleItemHolder(parent)
            TODO_ITEM -> DailyTodoItemHolder(parent, viewModelStoreOwner)
            OVERDUE_TODO_GROUP -> DailyOverdueTodoGroupHolder(parent)
            HABIT_ITEM -> DailyHabitItemHolder(
                parent,
                lifecycleOwner,
                viewModelStoreOwner,
                navController,
                sharedScrollStatus
            ).also {
//                slaveScrollHolders.add(it)
            }
            else -> throw IllegalArgumentException("Unknown viewType:$viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is SimpleHeaderItemModel -> SIMPLE_HEADER
            is HabitHeaderItemModel -> HABIT_HEADER
            is ScheduleItemModel -> SCHEDULE_ITEM
            is DailyTodo -> TODO_ITEM
            is DailyOverdueTodoGroup -> OVERDUE_TODO_GROUP
            is DailyHabit -> HABIT_ITEM
            else -> throw IllegalArgumentException("Unknown types :${items[position]}")
        }
    }

}
