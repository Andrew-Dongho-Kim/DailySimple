package com.dd.android.dailysimple.daily

import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.widget.recycler.ItemModel
import com.dd.android.dailysimple.common.widget.recycler.RecyclerViewAdapter2
import com.dd.android.dailysimple.common.widget.recycler.ViewHolder2
import com.dd.android.dailysimple.daily.DailyViewType.Companion.AUTHORITY_ITEM
import com.dd.android.dailysimple.daily.DailyViewType.Companion.EMPTY_ITEM
import com.dd.android.dailysimple.daily.DailyViewType.Companion.HABIT_ITEM
import com.dd.android.dailysimple.daily.DailyViewType.Companion.OVERDUE_TODO_GROUP
import com.dd.android.dailysimple.daily.DailyViewType.Companion.SCHEDULE_ITEM
import com.dd.android.dailysimple.daily.DailyViewType.Companion.SIMPLE_HEADER
import com.dd.android.dailysimple.daily.DailyViewType.Companion.TODO_ITEM
import com.dd.android.dailysimple.daily.viewholders.*
import com.dd.android.dailysimple.db.data.DailyHabitWithCheckStatus
import com.dd.android.dailysimple.db.data.DailySchedule
import com.dd.android.dailysimple.db.data.DailyTodo

private const val TAG = "DailyAdapter"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

private const val RECYCLER_VIEW_CACHE_SIZE = 10

@IntDef(
    SIMPLE_HEADER,
    EMPTY_ITEM,
    AUTHORITY_ITEM,
    SCHEDULE_ITEM,
    TODO_ITEM,
    HABIT_ITEM,
    OVERDUE_TODO_GROUP
)
annotation class DailyViewType {
    companion object {
        const val SIMPLE_HEADER = -1
        const val EMPTY_ITEM = 0
        const val AUTHORITY_ITEM = 1
        const val SCHEDULE_ITEM = 2
        const val TODO_ITEM = 3
        const val HABIT_ITEM = 4
        const val OVERDUE_TODO_GROUP = 5
    }
}

data class IdBase(val viewType: Int, val idBase: Long)

private val ViewTypeIdBaseMap = mapOf(
    Pair(DailySimpleHeaderItem::class.java, IdBase(SIMPLE_HEADER, -10L)),
    Pair(DailyEmptyItem::class.java, IdBase(EMPTY_ITEM, 10L)),
    Pair(DailyAuthorityItem::class.java, IdBase(AUTHORITY_ITEM, 50L)),
    Pair(DailySchedule::class.java, IdBase(SCHEDULE_ITEM, 100L)),
    Pair(DailyTodo::class.java, IdBase(TODO_ITEM, 1000000000L)),
    Pair(DailyHabitWithCheckStatus::class.java, IdBase(HABIT_ITEM, 2000000000L)),
    Pair(DailyTodoGroup::class.java, IdBase(OVERDUE_TODO_GROUP, 3000000000L))
)

fun RecyclerView.setUpCache() {
    setItemViewCacheSize(RECYCLER_VIEW_CACHE_SIZE)

    with(recycledViewPool) {
        setMaxRecycledViews(SIMPLE_HEADER, 3)
        setMaxRecycledViews(TODO_ITEM, 20)
        setMaxRecycledViews(HABIT_ITEM, 20)
    }
}


class DailyAdapter(
    lifecycleOwner: LifecycleOwner,
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val navController: NavController
) :
    RecyclerViewAdapter2(lifecycleOwner) {

    override fun onCreateViewHolder2(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder2<out ViewDataBinding, out ItemModel> {

        logD { "+onCreateViewHolder(#$viewType)" }
        return when (viewType) {
            SIMPLE_HEADER -> DailySimpleHeaderHolder(parent)
            EMPTY_ITEM -> DailyEmptyItemHolder(parent, navController)
            AUTHORITY_ITEM -> DailyAuthorityItemHolder(parent)
            SCHEDULE_ITEM -> DailyScheduleItemHolder(parent, navController)
            TODO_ITEM -> DailyTodoItemHolder(parent, viewModelStoreOwner, navController)
            OVERDUE_TODO_GROUP -> DailyTodoGroupHolder(parent)
            HABIT_ITEM -> DailyHabitItemHolder2(
                parent, viewModelStoreOwner, navController
            )
            else -> throw IllegalArgumentException("Unknown viewType:$viewType")
        }
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

