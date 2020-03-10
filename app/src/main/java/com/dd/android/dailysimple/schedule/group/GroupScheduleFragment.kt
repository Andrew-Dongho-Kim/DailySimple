package com.dd.android.dailysimple.schedule.group

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dd.android.dailysimple.databinding.FragmentGroupScheduleBinding

class GroupScheduleFragment : Fragment() {

    private lateinit var activity:Activity

    private lateinit var bind: FragmentGroupScheduleBinding

    override fun onAttach(activity:Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = FragmentGroupScheduleBinding.inflate(
            inflater, container, false
        )
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(bind.scheduleList) {
            layoutManager = LinearLayoutManager(activity)
            adapter =
                GroupScheduleAdapter()
                    .apply {
                        onItemClickListener = ScheduleCardItemClickListener()
                    }

            addItemDecoration(
                ScheduleCardItemDecoration(activity)
            )
        }
    }
}