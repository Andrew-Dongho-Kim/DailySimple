package com.dd.android.dailysimple.daily

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
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
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.databinding.DailyHabitHeaderItemBinding
import com.dd.android.dailysimple.databinding.DailyHabitItem2Binding
import com.dd.android.dailysimple.HomeViewPagerFragmentDirections
import com.dd.android.dailysimple.common.di.appDb
import com.dd.android.dailysimple.common.recycler.ItemModel
import com.dd.android.dailysimple.common.recycler.RecyclerViewAdapter2
import com.dd.android.dailysimple.common.recycler.ViewHolder2
import com.dd.android.dailysimple.daily.DailyScheduleViewType.Companion.DAILY_COMMON
import com.dd.android.dailysimple.daily.DailyScheduleViewType.Companion.DAILY_HEADER
import com.dd.android.dailysimple.daily.DailyScheduleViewType.Companion.TODO_COMMON
import com.dd.android.dailysimple.daily.DailyScheduleViewType.Companion.TODO_HEADER
import com.dd.android.dailysimple.daily.viewmodel.HabitHeaderItemModel
import com.dd.android.dailysimple.daily.viewmodel.TodoHeaderItemModel
import com.dd.android.dailysimple.db.CheckStatus
import com.dd.android.dailysimple.provider.calendar.TodoItemModel

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

    private val adapter = DayDateAdapter(lifecycleOwner)
    private val layoutManager = LinearLayoutManager(parent.context).apply {
        orientation = RecyclerView.HORIZONTAL
        reverseLayout = true
    }
    private val slaveRecyclerViewList = mutableListOf<RecyclerView>()

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            model?.refresh(
                layoutManager.findFirstCompletelyVisibleItemPosition()
            )
            slaveRecyclerViewList.forEach {
                it.scrollBy(dx, dy)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {}
    }

    private val observer = Observer<PagedList<DayDateItemModel>> { adapter.submitList(it) }
    private var model: HabitHeaderItemModel? = null

    init {
        bindAs<DailyHabitHeaderItemBinding>().checkedRecyclerView.apply {
            adapter = this@DailyHabitHeaderHolder.adapter
            layoutManager = this@DailyHabitHeaderHolder.layoutManager
            removeOnScrollListener(scrollListener)
            addOnScrollListener(scrollListener)
        }
    }

    fun addSlaveRecyclerView(recyclerView: RecyclerView) {
        slaveRecyclerViewList.add(recyclerView)
    }

    fun removeSlaveRecyclerView(recyclerView: RecyclerView) {
        slaveRecyclerViewList.remove(recyclerView)
    }

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
) : ViewHolder2(
    parent,
    R.layout.daily_habit_item2,
    BR.viewModel
) {
    private val binding = bindAs<DailyHabitItem2Binding>()
    private val adapter = CheckStatusAdapter(lifecycleOwner)
    private val layoutManager = LinearLayoutManager(parent.context).apply {
        orientation = RecyclerView.HORIZONTAL
        reverseLayout = true
    }

    var checkedListRecycler:RecyclerView? = null
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

    init {
        binding.checkedRecyclerView.apply {
            adapter = this@DailyHabitItemHolder.adapter
            layoutManager = this@DailyHabitItemHolder.layoutManager
            removeOnItemTouchListener(onBlockItemTouch)
            addOnItemTouchListener(onBlockItemTouch)
            checkedListRecycler = this
        }
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
                HomeViewPagerFragmentDirections.homeToDailyHabitDetailFragment(it.getTag(ID_TAG) as Long)
            )
        }
        private val onBlockItemTouch = object : RecyclerView.OnItemTouchListener {
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                return true
            }
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
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

    private var habitHeaderHolder: DailyHabitHeaderHolder? = null

    override fun onCreateViewHolder2(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            TODO_HEADER -> DailyTodoHeaderHolder(parent)
            TODO_COMMON -> DailyTodoItemHolder(parent)
            DAILY_HEADER -> DailyHabitHeaderHolder(parent, lifecycleOwner).also {
                habitHeaderHolder = it
            }
            else -> DailyHabitItemHolder(parent, lifecycleOwner)
        }

    override fun onBindViewHolder(holder: ViewHolder2, position: Int) {
        super.onBindViewHolder(holder, position)

        // How to remove un-visible slave recycler view for checked list ?
        when (holder) {
            is DailyHabitItemHolder -> {
                holder.checkedListRecycler?.let {
                    habitHeaderHolder?.addSlaveRecyclerView(it)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TodoHeaderItemModel -> TODO_HEADER
            is TodoItemModel -> TODO_COMMON
            is HabitHeaderItemModel -> DAILY_HEADER
            else -> DAILY_COMMON
        }
    }
}