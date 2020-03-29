package com.dd.android.dailysimple.schedule.daily

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.databinding.FragmentGroupScheduleBinding
import com.dd.android.dailysimple.schedule.HomeViewPagerFragmentDirections
import com.dd.android.dailysimple.schedule.common.BaseFragment
import com.dd.android.dailysimple.schedule.group.ScheduleCardItemDecoration


class DailyFragment : BaseFragment<FragmentGroupScheduleBinding>() {

    override val layout: Int = R.layout.fragment_group_schedule

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setUpRecycler()
        setUpAdd(view)

        setHasOptionsMenu(true)
    }

    private fun setUpRecycler() {
        with(bind.recycler) {
            val adapter2 = DailyAdapter(viewLifecycleOwner)

            adapter = adapter2
            layoutManager = LinearLayoutManager(activity)
            itemAnimator = DefaultItemAnimator()

            DailyItemModels(this@DailyFragment).data.observe(
                viewLifecycleOwner,
                Observer { list ->
                    adapter2.items.clear()
                    adapter2.items.addAll(list)
                    adapter2.notifyDataSetChanged()
                })

            addItemDecoration(
                ScheduleCardItemDecoration(activity)
            )

            ItemTouchHelper(
                DailyItemTouchAction(requireContext(), adapter2)
            ).attachToRecyclerView(this)
        }
    }

    private fun setUpAdd(view: View) {
        bind.fabAdd.setOnClickListener {
            view.findNavController().navigate(
                HomeViewPagerFragmentDirections.homeToCreateDailyScheduleFragment(-1)
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

