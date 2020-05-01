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
import com.dd.android.dailysimple.common.FabViewModel
import com.dd.android.dailysimple.common.OnDateChangedListener
import com.dd.android.dailysimple.common.utils.DateUtils.msDateOnlyFrom
import com.dd.android.dailysimple.daily.DailyConst.NO_ID
import com.dd.android.dailysimple.daily.viewmodel.DailyCalendarModel
import com.dd.android.dailysimple.daily.viewmodel.HabitViewModel
import com.dd.android.dailysimple.daily.viewmodel.TodoViewModel
import com.dd.android.dailysimple.databinding.FragmentScheduleCommonBinding
import com.dd.android.dailysimple.google.GoogleAccountViewModel
import com.dd.android.dailysimple.plan.ScheduleCardItemDecoration


class DailyFragment : BaseFragment<FragmentScheduleCommonBinding>(), OnDateChangedListener {

    private lateinit var fabVm: FabViewModel
    private lateinit var todoVm: TodoViewModel
    private lateinit var habitVm: HabitViewModel

    private lateinit var calendarModel: DailyCalendarModel

    override val layout: Int = R.layout.fragment_schedule_common

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setStatusBarColor(R.color.basic_common_background)
        setUpViewModel()
        setUpObserver()
        setUpHeader()
        setUpContent()
        setUpFab()

        setHasOptionsMenu(true)
    }

    override fun onDateChanged() {
        val date = msDateOnlyFrom()
        todoVm.selectedDate.postValue(date)
        habitVm.selectedDate.postValue(date)
    }

    private fun setUpViewModel() {
        fabVm = ViewModelProvider(activity).get(FabViewModel::class.java)
        todoVm = ViewModelProvider(activity).get(TodoViewModel::class.java)
        habitVm = ViewModelProvider(activity).get(HabitViewModel::class.java)
        calendarModel = DailyCalendarModel(activity.application)

        bind.accountViewModel = activity.viewModels<GoogleAccountViewModel>().value
        bind.fabModel = fabVm
    }

    private fun setUpObserver() {
        dateChangedObserver.addOnDateChangedListener(this)
    }

    private fun setUpHeader() {
        val recycler = bind.customToolbar.calendar

        val layoutManager = LinearLayoutManager(context)
            .apply {
                orientation = RecyclerView.HORIZONTAL
                reverseLayout = true
            }

        val adapter = DayDateAdapter2(viewLifecycleOwner)
        val observer = Observer<PagedList<DayDateItemModel>> { adapter.submitList(it) }

        recycler.adapter = adapter
        recycler.layoutManager = layoutManager

        PagerSnapHelper().attachToRecyclerView(recycler)

        calendarModel.dayDatePagedList.observe(viewLifecycleOwner, observer)
    }

    private fun setUpContent() {
        val recycler = bind.recycler

        val layoutManager = LinearLayoutManager(activity)
        val adapter = DailyAdapter(viewLifecycleOwner, this, navController)
        adapter.setHasStableIds(true)

        recycler.adapter = adapter
        recycler.layoutManager = layoutManager
        recycler.itemAnimator = DefaultItemAnimator()

        DailyItemModels(requireActivity()).data.observe(
            viewLifecycleOwner,
            Observer { list ->
                adapter.items.clear()
                adapter.items.addAll(list)
                adapter.notifyDataSetChanged()
            })

        recycler.addItemDecoration(
            ScheduleCardItemDecoration(activity)
        )

//        ItemTouchHelper(
//            DailyItemTouchAction(activity, adapter, navController)
//        ).attachToRecyclerView(recycler)

        recycler.setUpCache()
    }

    private fun setUpFab() {
        fabVm.fab1Text.postValue(getString(R.string.make_a_habit))
        fabVm.fab2Text.postValue(getString(R.string.make_a_todo))

        fabVm.onFab1Click = {
            navController.navigate(
                HomeFragmentDirections.homeToMakeAndEditHabit(NO_ID)
            )
        }
        fabVm.onFab2Click = {
            navController.navigate(
                HomeFragmentDirections.homeToMakeAndEditTodo(NO_ID)
            )
        }
        fabVm.isOpen.observe(viewLifecycleOwner, Observer { opened ->
            with(bind.fabLayout) {
                fabAdd.animate().rotation(if (opened) 45f else 0f)

                fabRoot.setOnClickListener(if (opened) ::blockTouch else null)
                fabRoot.isClickable = opened
            }
        })
    }

    private fun blockTouch(view: View) {}

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.daily_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}


