package com.dd.android.dailysimple.daily.calendar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.BaseFragment
import com.dd.android.dailysimple.databinding.FragmentDailyCalendarBinding

private const val SPAN_COUNT = 7

class DailyCalendarFragment : BaseFragment<FragmentDailyCalendarBinding>() {

    private val calendarVm by viewModels<DailyCalendarViewModel>()

    override val layout = R.layout.fragment_daily_calendar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind.model = calendarVm
        setUpCalendar()
    }

    private fun setUpCalendar() {
        val data = build(requireContext())
        with(bind.calendar) {
            layoutManager = GridLayoutManager(context, SPAN_COUNT)
            adapter = DailyCalendarAdapter(viewLifecycleOwner).apply {
                items.addAll(data)
                notifyDataSetChanged()
            }
        }

    }
}