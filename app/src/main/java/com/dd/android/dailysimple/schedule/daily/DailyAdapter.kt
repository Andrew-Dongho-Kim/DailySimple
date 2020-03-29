package com.dd.android.dailysimple.schedule.daily

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.databinding.DailyHabitHeaderItemBinding
import com.dd.android.dailysimple.schedule.HomeViewPagerFragmentDirections
import com.dd.android.dailysimple.schedule.common.recycler.ItemModel
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

open class DailyHabitHeaderHolder(
    parent: ViewGroup,
    private val lifecycleOwner: LifecycleOwner
) :
    ViewHolder2(
        parent,
        R.layout.daily_habit_header_item,
        BR.viewModel
    ) {

    private var model: HabitHeaderItemModel? = null

    private val adapter = DayDateAdapter(lifecycleOwner)

    private val layoutManager = LinearLayoutManager(parent.context).apply {
        orientation = RecyclerView.HORIZONTAL
        reverseLayout = true
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            model?.refresh(
                layoutManager.findFirstCompletelyVisibleItemPosition()
            )
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {}
    }

    private val recyclerView = bindAs<DailyHabitHeaderItemBinding>().checkedRecyclerView.apply {
        adapter = this@DailyHabitHeaderHolder.adapter
        layoutManager = this@DailyHabitHeaderHolder.layoutManager
        addOnScrollListener(scrollListener)
    }

    override fun bindTo(itemModel: ItemModel) {
        super.bindTo(itemModel)
        model = itemModel as HabitHeaderItemModel
        itemModel.dayDatePagedList.observe(
            lifecycleOwner,
            Observer { adapter.submitList(it) }
        )
    }
}

class DailyHabitItemHolder(parent: ViewGroup) : ViewHolder2(
    parent,
    R.layout.daily_habit_item,
    BR.viewModel
) {
    override fun bindTo(itemModel: ItemModel) {
        super.bindTo(itemModel)
        itemView.setTag(R.id.daily_habit_id, itemModel.id)
        itemClickListener = itemClick
    }

    companion object {
        private val itemClick = View.OnClickListener { view ->
            view.findNavController().navigate(
                HomeViewPagerFragmentDirections.homeToCreateDailyScheduleFragment(
                    view.getTag(R.id.daily_habit_id) as Long
                )
            )
        }
    }
}

class DailyTodoHeaderHolder(parent: ViewGroup) : ViewHolder2(
    parent,
    R.layout.daily_todo_header
)

class DailyTodoItemHolder(parent: ViewGroup) : ViewHolder2(
    parent,
    R.layout.daily_todo_item,
    BR.viewModel
)

class DailyAdapter(
    private val lifecycleOwner: LifecycleOwner
) : RecyclerViewAdapter2(lifecycleOwner) {

    override fun onCreateViewHolder2(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            TODO_HEADER -> DailyTodoHeaderHolder(parent)
            TODO_COMMON -> DailyTodoItemHolder(parent)
            DAILY_HEADER -> DailyHabitHeaderHolder(parent, lifecycleOwner)
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