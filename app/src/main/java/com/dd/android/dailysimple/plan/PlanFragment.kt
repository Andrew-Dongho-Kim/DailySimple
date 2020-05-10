package com.dd.android.dailysimple.plan

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.BaseFragment
import com.dd.android.dailysimple.databinding.FragmentDailyBinding
import com.dd.android.dailysimple.google.GoogleAccountViewModel

class PlanFragment : BaseFragment<FragmentDailyBinding>() {

    override val layout = R.layout.fragment_daily

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bind.accountViewModel = activity.viewModels<GoogleAccountViewModel>().value
        with(bind.recycler) {
            layoutManager = LinearLayoutManager(activity)
            adapter =
                GroupScheduleAdapter()
                    .apply {
                    }

            addItemDecoration(
                ScheduleCardItemDecoration(
                    activity
                )
            )
        }
    }
}