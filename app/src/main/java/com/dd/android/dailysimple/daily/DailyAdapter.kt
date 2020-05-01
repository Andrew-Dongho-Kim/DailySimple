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
import com.dd.android.dailysimple.daily.DailyViewType.Companion.EMPTY_ITEM
import com.dd.android.dailysimple.daily.DailyViewType.Companion.HABIT_HEADER
import com.dd.android.dailysimple.daily.DailyViewType.Companion.HABIT_ITEM
import com.dd.android.dailysimple.daily.DailyViewType.Companion.OVERDUE_TODO_GROUP
import com.dd.android.dailysimple.daily.DailyViewType.Companion.SCHEDULE_ITEM
import com.dd.android.dailysimple.daily.DailyViewType.Companion.SIMPLE_HEADER
import com.dd.android.dailysimple.daily.DailyViewType.Companion.TODO_ITEM
import com.dd.android.dailysimple.daily.viewholders.*
import com.dd.android.dailysimple.daily.viewmodel.DailyCalendarModel
import com.dd.android.dailysimple.db.data.DailyHabitWithCheckStatus
import com.dd.android.dailysimple.db.data.DailyTodo
import com.dd.android.dailysimple.provider.calendar.ScheduleItemModel
import java.util.*

private const val TAG = "DailyAdapter"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

private const val RECYCLER_VIEW_CACHE_SIZE = 10

@IntDef(
    SIMPLE_HEADER,
    HABIT_HEADER,
    EMPTY_ITEM,
    SCHEDULE_ITEM,
    TODO_ITEM,
    HABIT_ITEM,
    OVERDUE_TODO_GROUP
)
annotation class DailyViewType {
    companion object {
        const val SIMPLE_HEADER = -1
        const val HABIT_HEADER = -2
        const val EMPTY_ITEM = 0
        const val SCHEDULE_ITEM = 1
        const val TODO_ITEM = 2
        const val HABIT_ITEM = 3
        const val OVERDUE_TODO_GROUP = 4
    }
}

enum class IdBase(val viewType: Int, val idBase: Long) {
    SIMPLE_HEADER(-1, -10L),
    HABIT_HEADER(-2, -50L),
    EMPTY_ITEM(0, 10L),
    SCHEDULE_ITEM(1, 50L),
    TODO_ITEM(2, 1000000L),
    HABIT_ITEM(3, 2000000L),
    OVERDUE_TODO_GROUP(4, 3000000L)
}


private val ViewTypeIdBaseMap = mapOf(
    Pair(DailySimpleHeaderItem::class.java, IdBase.SIMPLE_HEADER),
    Pair(DailyCalendarModel::class.java, IdBase.HABIT_HEADER),
    Pair(DailyEmptyItemModel::class.java, IdBase.EMPTY_ITEM),
    Pair(ScheduleItemModel::class.java, IdBase.SCHEDULE_ITEM),
    Pair(DailyTodo::class.java, IdBase.TODO_ITEM),
    Pair(DailyHabitWithCheckStatus::class.java, IdBase.HABIT_ITEM),
    Pair(DailyTodoGroup::class.java, IdBase.OVERDUE_TODO_GROUP)
)

fun RecyclerView.setUpCache() {
    setItemViewCacheSize(RECYCLER_VIEW_CACHE_SIZE)

    with(recycledViewPool) {
        setMaxRecycledViews(HABIT_HEADER, 1)
        setMaxRecycledViews(SIMPLE_HEADER, 2)
        setMaxRecycledViews(HABIT_ITEM, 20)
    }
}

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
            SIMPLE_HEADER -> DailySimpleHeaderHolder(parent)
            HABIT_HEADER -> DailyHabitHeaderHolder(parent, lifecycleOwner).also {
                it.recyclerView.addOnScrollListener(slaveScroll)
            }
            EMPTY_ITEM -> DailyEmptyItemHolder(parent, navController)
            SCHEDULE_ITEM -> DailyScheduleItemHolder(parent)
            TODO_ITEM -> DailyTodoItemHolder(parent, viewModelStoreOwner)
            OVERDUE_TODO_GROUP -> DailyTodoGroupHolder(parent)
            HABIT_ITEM -> DailyHabitItemHolder2(
                parent, viewModelStoreOwner, navController
            )
            else -> throw IllegalArgumentException("Unknown viewType:$viewType")
        }
    }

    override fun onBindViewHolder(
        holder: ViewHolder2<out ViewDataBinding, out ItemModel>,
        position: Int
    ) {
        super.onBindViewHolder(holder, position)
        if (holder is SlaveScrollViewHolder) slaveScrollHolders.add(holder)
    }

    override fun onViewRecycled(holder: ViewHolder2<out ViewDataBinding, out ItemModel>) {
        slaveScrollHolders.remove(holder)
    }

    override fun getItemId(position: Int) =
        items[position].run {
            (ViewTypeIdBaseMap[javaClass] ?: error("unknown : $this")).idBase + id
        }

    override fun getItemViewType(position: Int) =
        items[position].run {
            (ViewTypeIdBaseMap[javaClass] ?: error("unknown : $this")).viewType
        }
}

