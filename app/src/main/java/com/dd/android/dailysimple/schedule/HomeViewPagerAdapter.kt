package com.dd.android.dailysimple.schedule

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dd.android.dailysimple.schedule.daily.DailyScheduleFragment
import com.dd.android.dailysimple.schedule.group.CreateGroupScheduleFragment
import com.dd.android.dailysimple.schedule.group.GroupScheduleFragment

const val GROUP_SCHEDULE_PAGE_INDEX = 0
const val DAILY_SCHEDULE_PAGE_INDEX = 1

class HomeViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragmentCreators: Map<Int, () -> Fragment> = mapOf(
        GROUP_SCHEDULE_PAGE_INDEX to { GroupScheduleFragment() },
        DAILY_SCHEDULE_PAGE_INDEX to { DailyScheduleFragment() }
    )

    override fun getItemCount() = fragmentCreators.size

    override fun createFragment(position: Int) =
        fragmentCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
}