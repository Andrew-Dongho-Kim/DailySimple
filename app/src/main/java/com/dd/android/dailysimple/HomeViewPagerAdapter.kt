package com.dd.android.dailysimple

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dd.android.dailysimple.daily.DailyFragment
import com.dd.android.dailysimple.plan.PlanFragment

const val DAILY_SCHEDULE_PAGE_INDEX = 0
const val GROUP_SCHEDULE_PAGE_INDEX = 1
const val SETTINGS_PAGE_INDEX = 2

val TabTitleResId = arrayOf(
    R.string.daily,
    R.string.plan,
    R.string.setting
)

val TabIconResId = arrayOf(
    R.drawable.ic_daily,
    R.drawable.ic_plan,
    R.drawable.ic_settings
)

class HomeViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragmentCreators: Map<Int, () -> Fragment> = mapOf(
        DAILY_SCHEDULE_PAGE_INDEX to { DailyFragment() },
        GROUP_SCHEDULE_PAGE_INDEX to { PlanFragment() },
        SETTINGS_PAGE_INDEX to { SettingFragment() }
    )

    override fun getItemCount() = fragmentCreators.size

    override fun createFragment(position: Int) =
        fragmentCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
}