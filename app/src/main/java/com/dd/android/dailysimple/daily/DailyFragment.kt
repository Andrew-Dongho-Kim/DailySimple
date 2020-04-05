package com.dd.android.dailysimple.daily

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
import com.dd.android.dailysimple.HomeViewPagerFragmentDirections
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.BaseFragment
import com.dd.android.dailysimple.databinding.FragmentScheduleCommonBinding
import com.dd.android.dailysimple.plan.ScheduleCardItemDecoration


class DailyFragment : BaseFragment<FragmentScheduleCommonBinding>() {

    override val layout: Int = R.layout.fragment_schedule_common

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setUpRecycler()
        setUpFab(view)

        setHasOptionsMenu(true)
    }

    private fun setUpRecycler() {
        with(bind.recycler) {
            val lm = LinearLayoutManager(activity)
            val adt = DailyAdapter(viewLifecycleOwner, lm)

            adapter = adt
            layoutManager = lm
            itemAnimator = DefaultItemAnimator()

            DailyItemModels(requireActivity()).data.observe(
                viewLifecycleOwner,
                Observer { list ->
                    adt.items.clear()
                    adt.items.addAll(list)
                    adt.notifyDataSetChanged()
                })

            addItemDecoration(
                ScheduleCardItemDecoration(
                    activity
                )
            )

            ItemTouchHelper(
                DailyItemTouchAction(requireContext(), adt)
            ).attachToRecyclerView(this)
        }
    }

    private fun setUpFab(view: View) {
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

