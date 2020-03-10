package com.dd.android.dailysimple.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.databinding.FragmentViewPagerBinding
import com.google.android.material.tabs.TabLayoutMediator

private val TabTitleResId = arrayOf(
    R.string.group_schedule,
    R.string.daily_schedule
)

class HomeViewPagerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bind = FragmentViewPagerBinding.inflate(inflater, container, false)
        val tabLayout = bind.tabs
        val viewPager = bind.homeViewPager

        viewPager.adapter = HomeViewPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

        (activity as AppCompatActivity).setSupportActionBar(bind.toolbar)
        return bind.root
    }

    private fun getTabTitle(position: Int) = getString(TabTitleResId[position])

}