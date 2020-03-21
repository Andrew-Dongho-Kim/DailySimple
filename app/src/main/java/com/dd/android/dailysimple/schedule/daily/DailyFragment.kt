package com.dd.android.dailysimple.schedule.daily

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import com.dd.android.dailysimple.databinding.FragmentGroupScheduleBinding
import com.dd.android.dailysimple.schedule.common.BaseFragment
import com.dd.android.dailysimple.schedule.daily.viewmodel.HabitHeaderViewModel
import com.dd.android.dailysimple.schedule.daily.viewmodel.TodoViewModel
import com.dd.android.dailysimple.schedule.group.ScheduleCardItemDecoration


class DailyFragment : BaseFragment<FragmentGroupScheduleBinding>() {

    private val timeReceiver = TimeReceiver(this)

    override val layout: Int = com.dd.android.dailysimple.R.layout.fragment_group_schedule

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycle.addObserver(timeReceiver)

        val adapter2 = DailyScheduleAdapter(viewLifecycleOwner)
        with(bind.scheduleList) {
            layoutManager = LinearLayoutManager(activity)
            adapter = adapter2

            DailyItemModels(this@DailyFragment).data.observe(
                viewLifecycleOwner,
                Observer { list ->
                    Log.e("TEST-DH", "Loaded list : ${list.size}")
                    adapter2.items.clear()
                    adapter2.items.addAll(list)
                    adapter2.notifyDataSetChanged()
                })

            addItemDecoration(
                ScheduleCardItemDecoration(activity)
            )
        }
    }

}

private class TimeReceiver(fragment: Fragment) : LifecycleObserver, BroadcastReceiver() {
    private val context by lazy { fragment.requireContext() }

    private val habitHeaderVm by fragment.viewModels<HabitHeaderViewModel>()
    private val todayVm by fragment.viewModels<TodoViewModel>()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action in ACTIONS) {
            habitHeaderVm.refresh()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        context.registerReceiver(this, IntentFilter().apply {
            ACTIONS.forEach { addAction(it) }
        })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        context.unregisterReceiver(this)
    }

    companion object {
        private val ACTIONS = arrayOf(
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_DATE_CHANGED
        )
    }
}