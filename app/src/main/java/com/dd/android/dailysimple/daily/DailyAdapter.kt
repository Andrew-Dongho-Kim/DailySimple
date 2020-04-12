package com.dd.android.dailysimple.daily

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.annotation.LayoutRes
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dd.android.dailysimple.HomeFragmentDirections
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.databinding.DailyHabitHeaderItemBinding
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.di.appDb
import com.dd.android.dailysimple.common.recycler.ItemModel
import com.dd.android.dailysimple.common.recycler.RecyclerViewAdapter2
import com.dd.android.dailysimple.common.recycler.ViewHolder2
import com.dd.android.dailysimple.daily.DailyScheduleViewType.Companion.DAILY_COMMON
import com.dd.android.dailysimple.daily.DailyScheduleViewType.Companion.DAILY_HEADER
import com.dd.android.dailysimple.daily.DailyScheduleViewType.Companion.SCHEDULE_COMMON
import com.dd.android.dailysimple.daily.DailyScheduleViewType.Companion.SIMPLE_HEADER
import com.dd.android.dailysimple.daily.viewmodel.HabitHeaderItemModel
import com.dd.android.dailysimple.daily.viewmodel.SimpleHeaderItemModel
import com.dd.android.dailysimple.databinding.DailyHabitItemBinding
import com.dd.android.dailysimple.db.data.CheckStatus
import com.dd.android.dailysimple.provider.calendar.TodoItemModel
import java.util.*

private const val TAG = "DailyAdapter"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

private const val RECYCLER_VIEW_CACHE_SIZE = 10

@IntDef(DAILY_HEADER, DAILY_COMMON, SIMPLE_HEADER, SCHEDULE_COMMON)
private annotation class DailyScheduleViewType {
    companion object {
        const val SIMPLE_HEADER = -1
        const val DAILY_HEADER = -2

        const val DAILY_COMMON = 0
        const val SCHEDULE_COMMON = 1
    }
}

fun RecyclerView.setUpCache() {
    setItemViewCacheSize(RECYCLER_VIEW_CACHE_SIZE)

    with(recycledViewPool) {
        setMaxRecycledViews(DAILY_HEADER, 1)
        setMaxRecycledViews(SIMPLE_HEADER, 2)
    }
}

class SimpleHeaderItemHolder(parent: ViewGroup) : ViewHolder2(
    parent,
    R.layout.simple_header_item,
    BR.model
)

abstract class SlaveRecyclerHolder(
    parent: ViewGroup,
    @LayoutRes layoutResId: Int,
    variableId: Int = 0
) : ViewHolder2(parent, layoutResId, variableId) {

    var recyclerView: RecyclerView? = null
        set(value) {
            field?.removeOnItemTouchListener(onBlockTouchWhenScroll)
            field = value
            field?.addOnItemTouchListener(onBlockTouchWhenScroll)
        }

    var isMasterScrolling: Boolean = false

    private val onBlockTouchWhenScroll = object : RecyclerView.OnItemTouchListener {
        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            logD { "onInterceptTouchEvent  master scroll: $isMasterScrolling" }
            return isMasterScrolling
        }
    }
}

class DailyHabitHeaderHolder(
    parent: ViewGroup,
    private val lifecycleOwner: LifecycleOwner
) :
    ViewHolder2(
        parent,
        R.layout.daily_habit_header_item,
        BR.viewModel
    ) {

    private val adapter = DayDateAdapter(lifecycleOwner)
    private val layoutManager = LinearLayoutManager(parent.context).apply {
        orientation = RecyclerView.HORIZONTAL
        reverseLayout = true
    }
    private val yearAndMonthDetector = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            model?.refresh(
                layoutManager.findFirstCompletelyVisibleItemPosition()
            )
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {}
    }
    val recyclerView = bindAs<DailyHabitHeaderItemBinding>().checkedRecyclerView.apply {
        adapter = this@DailyHabitHeaderHolder.adapter
        layoutManager = this@DailyHabitHeaderHolder.layoutManager
        removeOnScrollListener(yearAndMonthDetector)
        addOnScrollListener(yearAndMonthDetector)
    }

    private val observer = Observer<PagedList<DayDateItemModel>> { adapter.submitList(it) }
    private var model: HabitHeaderItemModel? = null

    override fun bindTo(itemModel: ItemModel) {
        super.bindTo(itemModel)

        model = (itemModel as? HabitHeaderItemModel)?.also {
            it.dayDatePagedList.observe(lifecycleOwner, observer)
        }
    }
}

class DailyHabitItemHolder(
    parent: ViewGroup,
    private val lifecycleOwner: LifecycleOwner
) : SlaveRecyclerHolder(
    parent,
    R.layout.daily_habit_item,
    BR.viewModel
) {
    private val binding = bindAs<DailyHabitItemBinding>()
    private val adapter = CheckStatusAdapter(lifecycleOwner)

    init {
        recyclerView = binding.checkedRecyclerView.apply {
            adapter = this@DailyHabitItemHolder.adapter
            layoutManager = object : LinearLayoutManager(parent.context) {
                override fun canScrollHorizontally(): Boolean {
                    return isMasterScrolling
                }

            }.apply {
                orientation = RecyclerView.HORIZONTAL
                reverseLayout = true
            }
        }
    }

    private var liveDataAdapter: LiveData<PagedList<CheckStatus>>? = null
    private val observerAdapter = Observer<PagedList<CheckStatus>> { adapter.submitList(it) }

    private var liveDataRoom: LiveData<List<CheckStatus>>? = null
    private val observerRoom = Observer<List<CheckStatus>> {
        liveDataAdapter?.removeObserver(observerAdapter)
        liveDataAdapter = LivePagedListBuilder(
            object : DataSource.Factory<Key, CheckStatus>() {
                override fun create() = CheckStatusDataSource(itemModel!!.id, it)
            }, PagedList.Config.Builder()
                .setPageSize(LOAD_SIZE)
                .setInitialLoadSizeHint(LOAD_SIZE)
                .build()
        ).build()

        liveDataAdapter!!.observe(lifecycleOwner, observerAdapter)
    }

    override fun bindTo(itemModel: ItemModel) {
        super.bindTo(itemModel)

        binding.habitTitle.setTag(ID_TAG, itemModel.id)
        binding.habitTitle.setOnClickListener(onClick)

        liveDataRoom?.removeObserver(observerRoom)
        liveDataRoom = appDb()
            .checkStatusDao()
            .getAllCheckStatus(itemModel.id).also {
                it.observe(lifecycleOwner, observerRoom)
            }
    }

    companion object {
        private const val ID_TAG = R.id.daily_habit_id
        private const val LOAD_SIZE = 14
        private val onClick = View.OnClickListener {
            it.findNavController().navigate(
                HomeFragmentDirections.homeToDailyDetailFragment(it.getTag(ID_TAG) as Long)
            )
        }
    }
}


class DailyScheduleItemHolder(parent: ViewGroup) : ViewHolder2(
    parent,
    R.layout.daily_schedule_item,
    BR.viewModel
)

class DailyAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val layoutManager: LinearLayoutManager
) : RecyclerViewAdapter2(lifecycleOwner) {

    private var slaveList = LinkedList<SlaveRecyclerHolder>()
    private val slaveScroll = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            slaveList.forEach { it.recyclerView?.scrollBy(dx, dy) }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            slaveList.forEach {
                it.isMasterScrolling = newState != RecyclerView.SCROLL_STATE_IDLE
            }
        }
    }

    override fun onCreateViewHolder2(parent: ViewGroup, viewType: Int): ViewHolder2 {
        logD { "+onCreateViewHolder(#$viewType)" }
        return when (viewType) {
            SIMPLE_HEADER -> SimpleHeaderItemHolder(parent)
            SCHEDULE_COMMON -> DailyScheduleItemHolder(parent)
            DAILY_HEADER -> DailyHabitHeaderHolder(parent, lifecycleOwner).also {
                it.recyclerView.addOnScrollListener(slaveScroll)
            }
            else -> DailyHabitItemHolder(parent, lifecycleOwner).also {
                slaveList.add(it)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder2, position: Int) {
        super.onBindViewHolder(holder, position)
//        logD { "+onBindViewHolder(#${holder.itemViewType}): $position" }
    }

    private fun verifyColleague(vh: SlaveRecyclerHolder) {
        val first = layoutManager.findFirstVisibleItemPosition()
        val last = layoutManager.findLastVisibleItemPosition()

        val it = slaveList.listIterator()

        while (it.hasNext()) {
            val vh2 = it.next()
            if (vh == vh2 ||
                vh2.adapterPosition < first ||
                vh2.adapterPosition > last
            ) {
                it.remove()
            }
        }
        slaveList.add(vh)
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is SimpleHeaderItemModel -> SIMPLE_HEADER
            is TodoItemModel -> SCHEDULE_COMMON
            is HabitHeaderItemModel -> DAILY_HEADER
            else -> DAILY_COMMON
        }
    }

}
