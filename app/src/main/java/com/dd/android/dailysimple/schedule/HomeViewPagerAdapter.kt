package com.dd.android.dailysimple.schedule

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.schedule.daily.DailyFragment
import com.dd.android.dailysimple.schedule.group.CreateGroupScheduleFragment
import com.dd.android.dailysimple.schedule.group.GroupScheduleFragment

const val DAILY_SCHEDULE_PAGE_INDEX = 0
const val GROUP_SCHEDULE_PAGE_INDEX = 1
const val SETTINGS_PAGE_INDEX = 2

val TabTitleResId = arrayOf(
    R.string.daily_schedule,
    R.string.group_schedule,
    R.string.setting
)

class HomeViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragmentCreators: Map<Int, () -> Fragment> = mapOf(
        DAILY_SCHEDULE_PAGE_INDEX to { DailyFragment() },
        GROUP_SCHEDULE_PAGE_INDEX to { GroupScheduleFragment() },
        SETTINGS_PAGE_INDEX to { SettingFragment() }
    )

    override fun getItemCount() = fragmentCreators.size

    override fun createFragment(position: Int) =
        fragmentCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
}