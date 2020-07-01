package com.dd.android.dailysimple.daily.simplecalendar

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.utils.DateUtils.msDateFrom
import com.dd.android.dailysimple.common.widget.CenterScrollLinearLayoutManager
import com.dd.android.dailysimple.common.widget.recycler.findChildViewUnder2
import com.dd.android.dailysimple.daily.DayDateItemModel
import java.util.*


private const val TAG = "SimpleCalendarHelper"
private const val UNKNOWN_POSITION = -1

private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)
private inline fun logE(crossinline message: () -> String) = Logger.e(TAG, message)

class SimpleCalendarHelper(
    private val recyclerView: RecyclerView,
    private val lifecycleOwner: LifecycleOwner,
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val scrollWith: ((selectedDate: Long) -> Unit)? = null
) : RecyclerView.OnScrollListener() {

    private var simpleCalendarVm =
        ViewModelProvider(viewModelStoreOwner).get(SimpleCalendarViewModel::class.java)

    private var calendarPagedList: PagedList<DayDateItemModel>? = null
    private val calendarAdapter by lazy { setUpAdapter() }

    private var prevSelectedModel: DayDateItemModel? = null
    private var prevSelectedDate = 0L


    init {
        logD { "SimpleCalendarController created() :$viewModelStoreOwner" }
        setUpViewModel(calendarAdapter)
        setUpLayoutManager()

        recyclerView.addOnScrollListener(this)
    }

    private fun setUpAdapter() =
        SimpleCalendarAdapter(lifecycleOwner, viewModelStoreOwner)
            .also {
                it.setHasStableIds(true)
                recyclerView.adapter = it
            }

    private fun setUpLayoutManager() =
        CenterScrollLinearLayoutManager(recyclerView.context)
            .apply {
                orientation = RecyclerView.HORIZONTAL
                reverseLayout = true
            }
            .also {
                recyclerView.layoutManager = it
            }

    private fun setUpViewModel(adapter: SimpleCalendarAdapter) {
        simpleCalendarVm.calendar.observe(
            lifecycleOwner,
            Observer {
                logD { "Paged list(${it.loadedCount}) submitted" }
                calendarPagedList = it
                adapter.submitList(it)

                recyclerView.postDelayed({
                    smoothScrollToDate(simpleCalendarVm.selectedDate.value ?: msDateFrom())
                }, 500L)
            })

        simpleCalendarVm.selectedDateDistinct.observe(lifecycleOwner, Observer {
            recyclerView.post {
                smoothScrollToDate(it)
                logD { "Selected Date is changed : ${Date(it)}" }
            }
        })
    }

    private fun smoothScrollToPosition(position: Int) {
        if (position <= UNKNOWN_POSITION) {
            logE { "smoothScrollToPosition with unknown position:$position" }
            return
        }

        val list = calendarPagedList ?: return
        if (list.size <= position) return

        recyclerView.smoothScrollToPosition(position)
        list[position]?.let {
            it.isSelected.postValue(true)
            prevSelectedModel?.isSelected?.postValue(false)
            prevSelectedModel = it

            scrollWith?.invoke(it.id)
        }
        logD { "smoothScrollToPosition : $position, item:${Date(list[position]!!.id)}" }
    }

    private fun smoothScrollToDate(date: Long) {
        if (prevSelectedDate == date) return

        prevSelectedDate = date
        smoothScrollToPosition(findPositionByDate(date))
    }

    private fun findPositionByDate(date: Long): Int {
        return calendarPagedList?.indexOfFirst { it.id == date } ?: UNKNOWN_POSITION
    }

    private fun highlightPosition(position: Int) {
        simpleCalendarVm.selectedDate.value = calendarAdapter.getItemId(position)
    }

    /**
     * When scroll is finished, this function will make center view highlight
     */
    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == SCROLL_STATE_IDLE) {
            with(recyclerView) {
                val centerView = findChildViewUnder2(width / 2f, height / 2f)
                if (centerView == null) {
                    logE { "Can't find center view for this recycler" }
                    return
                }

                val position = getChildAdapterPosition(centerView)
                if (position == UNKNOWN_POSITION) {
                    logE { "Unknown position for $position" }
                    return
                }
                highlightPosition(position)
            }
        }
    }

    fun invalidate() = calendarAdapter.notifyDataSetChanged()
}