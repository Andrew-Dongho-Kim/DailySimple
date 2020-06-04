package com.dd.android.dailysimple.daily.simplecalendar

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.widget.CenterScrollLinearLayoutManager
import com.dd.android.dailysimple.common.widget.recycler.findChildViewUnder2
import com.dd.android.dailysimple.daily.DayDateItemModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


private const val TAG = "SimpleCalendar"
private const val UNKNOWN_POSITION = -1
private const val LIST_PREPARE_DELAY = 500L

private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)
private inline fun logE(crossinline message: () -> String) = Logger.e(TAG, message)

class SimpleCalendarController(
    private val recyclerView: RecyclerView,
    private val lifecycleOwner: LifecycleOwner,
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val scrollWith: ((selectedDate: Long) -> Unit)? = null
) : RecyclerView.OnScrollListener() {

    private var simpleCalendarVm =
        ViewModelProvider(viewModelStoreOwner).get(SimpleCalendarViewModel::class.java)

    private var calendarPagedList: PagedList<DayDateItemModel>? = null
    private val calendarLayoutManager = setUpLayoutManager()
    private val calendarAdapter = setUpAdapter()

    private var prevSelectedModel: DayDateItemModel? = null
    private var prevSelectedPosition = UNKNOWN_POSITION

    init {
        logD { "SimpleCalendarController created() :$viewModelStoreOwner" }
        setUpViewModel(calendarAdapter)

        recyclerView.also {
            it.addOnScrollListener(this)
            LinearSnapHelper().attachToRecyclerView(it)
        }
    }

    private fun setUpAdapter() =
        SimpleCalendarAdapter(
            lifecycleOwner, { pos ->
                val list = calendarPagedList ?: return@SimpleCalendarAdapter
                if (pos >= list.size) return@SimpleCalendarAdapter

                simpleCalendarVm.selectedDate.postValue(list[pos]!!.id)
            })
            .also {
                it.setHasStableIds(true)
                recyclerView.adapter = it
            }

    private fun setUpLayoutManager() =
        CenterScrollLinearLayoutManager(recyclerView.context)
            .also {
                it.orientation = RecyclerView.HORIZONTAL
                it.reverseLayout = true
                recyclerView.layoutManager = it
            }

    private fun setUpViewModel(adapter: SimpleCalendarAdapter) {
        simpleCalendarVm.calendar.observe(lifecycleOwner, Observer {
            adapter.submitList(it)
            this.calendarPagedList = it
            lifecycleOwner.lifecycleScope.launch {
                delay(LIST_PREPARE_DELAY)
                logD { "Scroll to selected date !" }
                smoothScrollToDate(simpleCalendarVm.selectedDate.value!!)
                simpleCalendarVm.selectedDateDistinct.observe(lifecycleOwner, Observer {
                    logD { "Selected Date is changed : ${Date(it)}" }
                    recyclerView.post { smoothScrollToDate(it) }
                })
            }
            logD { "Paged list(${it.loadedCount}) submitted" }
        })

    }

    private fun smoothScrollToPosition(position: Int) {
        if (position < 0) return

        val list = calendarPagedList ?: return
        if (list.size <= position) return
        if (prevSelectedPosition == position) return
        prevSelectedPosition = position

        logD { "smoothScrollToPosition : $position, item:${list[position]}" }
        recyclerView.smoothScrollToPosition(position)
        list[position]?.let {
            it.isSelected.postValue(true)
            prevSelectedModel?.isSelected?.postValue(false)
            prevSelectedModel = it

            scrollWith?.invoke(it.id)
        }
    }

    private fun smoothScrollToDate(date: Long) =
        smoothScrollToPosition(calendarPagedList?.indexOfFirst { it.id == date }
            ?: UNKNOWN_POSITION)

    fun invalidate() = calendarAdapter.notifyDataSetChanged()

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == SCROLL_STATE_IDLE) {
            with(recyclerView) {
                findChildViewUnder2(width / 2f, height / 2f)?.let {
                    val position = getChildAdapterPosition(it)
                    if (position == UNKNOWN_POSITION) {
                        logE { "Unknown position for $it" }
                        return
                    }
                    simpleCalendarVm.selectedDate.postValue(calendarAdapter.getItemId(position))
                } ?: logE { "Center view is null" }
            }

        }
    }
}