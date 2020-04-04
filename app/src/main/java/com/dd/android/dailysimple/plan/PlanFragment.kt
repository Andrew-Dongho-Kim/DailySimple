package com.dd.android.dailysimple.plan

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.databinding.FragmentScheduleCommonBinding
import com.dd.android.dailysimple.common.BaseFragment

class PlanFragment : BaseFragment<FragmentScheduleCommonBinding>() {

    override val layout = R.layout.fragment_schedule_common

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(bind.recycler) {
            layoutManager = LinearLayoutManager(activity)
            adapter =
                GroupScheduleAdapter()
                    .apply {
                        onItemClickListener =
                            ScheduleCardItemClickListener()
                    }

            addItemDecoration(
                ScheduleCardItemDecoration(
                    activity
                )
            )
        }
    }
}