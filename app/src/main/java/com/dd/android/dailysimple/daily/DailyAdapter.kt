package com.dd.android.dailysimple.daily

import android.view.ViewGroup
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

private const val TAG = "DailyAdapter"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

private const val RECYCLER_VIEW_CACHE_SIZE = 10


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
            (IdMap[javaClass] ?: error("unknown : $this")).idBase + id
        }

    override fun getItemViewType(position: Int) =
        items[position].run {
            (IdMap[javaClass] ?: error("unknown : $this")).viewType
        }
}

