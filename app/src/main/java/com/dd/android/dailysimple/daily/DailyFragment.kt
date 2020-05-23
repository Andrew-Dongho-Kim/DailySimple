package com.dd.android.dailysimple.daily

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.dd.android.dailysimple.HomeFragmentDirections
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.BaseFragment
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.OnDateChangedListener
import com.dd.android.dailysimple.common.utils.DateUtils.msDateOnlyFrom
import com.dd.android.dailysimple.common.widget.CenterScrollLinearLayoutManager
import com.dd.android.dailysimple.common.widget.recycler.StickyHeaderItemDecoration
import com.dd.android.dailysimple.daily.viewmodel.DailyCalendarModel
import com.dd.android.dailysimple.daily.viewmodel.HabitViewModel
import com.dd.android.dailysimple.daily.viewmodel.ScheduleViewModel
import com.dd.android.dailysimple.daily.viewmodel.TodoViewModel
import com.dd.android.dailysimple.databinding.FragmentDailyBinding
import com.dd.android.dailysimple.google.GoogleAccountViewModel
import com.dd.android.dailysimple.maker.BottomSimpleDailyMaker
import com.dd.android.dailysimple.maker.FabViewModel
import com.dd.android.dailysimple.plan.ScheduleCardItemDecoration
import java.util.*

private const val TAG = "DailyFragment"

private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class DailyFragment : BaseFragment<FragmentDailyBinding>(), OnDateChangedListener {

    private lateinit var fabVm: FabViewModel
    private lateinit var todoVm: TodoViewModel
    private lateinit var habitVm: HabitViewModel
    private lateinit var scheduleVm: ScheduleViewModel
    private lateinit var calendarModel: DailyCalendarModel

    private val viewModelStoreOwner by lazy { this }

    override val layout: Int = R.layout.fragment_daily

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setStatusBarColor(R.color.basic_common_background)
        setUpViewModel()
        setUpObserver()
        setUpSimpleCalendar()
        setUpContent()
        BottomSimpleDailyMaker(
            requireContext(),
            bind.fabLayout,
            fabVm,
            viewLifecycleOwner,
            navController
        )
        setHasOptionsMenu(true)
    }

    private fun setUpViewModel() {
        fabVm = ViewModelProvider(viewModelStoreOwner).get(FabViewModel::class.java)
        todoVm = ViewModelProvider(viewModelStoreOwner).get(TodoViewModel::class.java)
        habitVm = ViewModelProvider(viewModelStoreOwner).get(HabitViewModel::class.java)
        scheduleVm = ViewModelProvider(viewModelStoreOwner).get(ScheduleViewModel::class.java)
        calendarModel = DailyCalendarModel(activity.application)

        bind.accountViewModel = activity.viewModels<GoogleAccountViewModel>().value
        bind.fabModel = fabVm
    }

    private fun setUpSimpleCalendar() {
        bind.customToolbar.ymText.setOnClickListener {
            navController.navigate(
                HomeFragmentDirections.homeToDailyCalendar()
            )
        }
        val recycler = bind.customToolbar.calendar

        val layoutManager = CenterScrollLinearLayoutManager(
            requireContext()
        )
            .apply {
                orientation = RecyclerView.HORIZONTAL
                reverseLayout = true
            }

        val adapter = DayDateAdapter2(
            viewLifecycleOwner,
            viewModelStoreOwner,
            { pos ->
                recycler.smoothScrollToPosition(pos)
            }).apply {
            setHasStableIds(true)
        }
        val observer = Observer<PagedList<DayDateItemModel>> { adapter.submitList(it) }

        recycler.adapter = adapter
        recycler.layoutManager = layoutManager


        PagerSnapHelper().attachToRecyclerView(recycler)

        calendarModel.dayDatePagedList.observe(viewLifecycleOwner, observer)
    }

    private fun setUpContent() {
        val adapter = DailyAdapter(viewLifecycleOwner, viewModelStoreOwner, navController)
            .apply {
                setHasStableIds(true)
            }

        with(bind.recycler) {
            layoutManager = LinearLayoutManager(activity)
            itemAnimator = DefaultItemAnimator()
            this.adapter = adapter

            //addOnScrollListener(BottomBarScroll(requireActivity().findViewById(R.id.bottom_navigation_bar)))
            addItemDecoration(ScheduleCardItemDecoration(activity))
            addItemDecoration(
                StickyHeaderItemDecoration(
                    this
                ) { pos -> adapter.getItemViewType(pos) < 0 }
            )
            setUpCache()
        }

        DailyItemModels(viewModelStoreOwner).data.observe(
            viewLifecycleOwner,
            Observer { list ->
                adapter.items.clear()
                adapter.items.addAll(list)
                adapter.notifyDataSetChanged()
            })

//        ItemTouchHelper(
//            DailyItemTouchAction(activity, adapter, navController)
//        ).attachToRecyclerView(recycler)
    }

    override fun onDateChanged() {
        val time = msDateOnlyFrom()
        logD { "onDateChanged : ${Date(time)}" }
        todoVm.selectedDate.postValue(time)
        habitVm.selectedDate.postValue(time)
        scheduleVm.selectedDate.postValue(time)
    }

    private fun setUpObserver() {
        dateChangedObserver.addOnDateChangedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.daily_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}


