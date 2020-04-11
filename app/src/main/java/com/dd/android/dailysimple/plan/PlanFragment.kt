package com.dd.android.dailysimple.plan

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dd.android.dailysimple.HomeFragmentDirections
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.databinding.FragmentScheduleCommonBinding
import com.dd.android.dailysimple.common.BaseFragment
import com.dd.android.dailysimple.google.GoogleAccountViewModel

class PlanFragment : BaseFragment<FragmentScheduleCommonBinding>() {

    override val layout = R.layout.fragment_schedule_common

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bind.accountViewModel = activity.viewModels<GoogleAccountViewModel>().value
        with(bind.recycler) {
            layoutManager = LinearLayoutManager(activity)
            adapter =
                GroupScheduleAdapter()
                    .apply {
                        onItemClickListener = View.OnClickListener {
                            navController.navigate(
                                HomeFragmentDirections.homeToPlanDetailFragment(0)
                            )
                        }
                    }

            addItemDecoration(
                ScheduleCardItemDecoration(
                    activity
                )
            )
        }
    }
}