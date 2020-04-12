package com.dd.android.dailysimple.daily

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.dd.android.dailysimple.HomeFragmentDirections
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.BaseFragment
import com.dd.android.dailysimple.common.FabViewModel
import com.dd.android.dailysimple.databinding.FragmentScheduleCommonBinding
import com.dd.android.dailysimple.google.GoogleAccountViewModel
import com.dd.android.dailysimple.plan.ScheduleCardItemDecoration


class DailyFragment : BaseFragment<FragmentScheduleCommonBinding>() {

    private lateinit var fabModel: FabViewModel

    override val layout: Int = R.layout.fragment_schedule_common

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpViewModel()
        setUpRecycler()
        setUpFab()

        setHasOptionsMenu(true)
    }

    private fun setUpViewModel() {
        fabModel = ViewModelProvider(activity).get(FabViewModel::class.java)
        bind.accountViewModel = activity.viewModels<GoogleAccountViewModel>().value
        bind.fabModel = fabModel
    }

    private fun setUpRecycler() {
        val recycler = bind.recycler

        val layoutManager = LinearLayoutManager(activity)
        val adapter = DailyAdapter(viewLifecycleOwner, layoutManager)

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

        ItemTouchHelper(
            DailyItemTouchAction(activity, adapter, navController)
        ).attachToRecyclerView(recycler)

        recycler.setUpCache()
    }

    private fun setUpFab() {
        fabModel.fab1Text.postValue(getString(R.string.make_a_habit))
        fabModel.fab2Text.postValue(getString(R.string.make_a_todo))

        fabModel.onFab1Click = {
            navController.navigate(
                HomeFragmentDirections.homeToMakeAndEditHabitFragment(-1)
            )
        }
        fabModel.onFab2Click = {
            navController.navigate(
                HomeFragmentDirections.homeToMakeAndEditTodoFragment(-1)
            )
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.daily_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}


