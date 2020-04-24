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
import com.dd.android.dailysimple.daily.viewmodel.DailyHabitHeader
import com.dd.android.dailysimple.databinding.FragmentScheduleCommonBinding
import com.dd.android.dailysimple.google.GoogleAccountViewModel
import com.dd.android.dailysimple.plan.ScheduleCardItemDecoration


class DailyFragment : BaseFragment<FragmentScheduleCommonBinding>() {

    private lateinit var fabModel: FabViewModel

    private lateinit var calendarModel: DailyHabitHeader

    override val layout: Int = R.layout.fragment_schedule_common

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fabModel = ViewModelProvider(activity).get(FabViewModel::class.java)
        calendarModel = DailyHabitHeader(activity.application)

        setUpViewModel()
        setUpHeader()
        setUpContent()
        setUpFab()

        setHasOptionsMenu(true)
    }

    private fun setUpViewModel() {
        bind.accountViewModel = activity.viewModels<GoogleAccountViewModel>().value
        bind.fabModel = fabModel
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
        fabModel.fab1Text.postValue(getString(R.string.make_a_habit))
        fabModel.fab2Text.postValue(getString(R.string.make_a_todo))

        fabModel.onFab1Click = {
            navController.navigate(
                HomeFragmentDirections.homeToMakeAndEditHabit(-1)
            )
        }
        fabModel.onFab2Click = {
            navController.navigate(
                HomeFragmentDirections.homeToMakeAndEditTodo(-1)
            )
        }
        fabModel.isOpen.observe(viewLifecycleOwner, Observer { opened ->
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


