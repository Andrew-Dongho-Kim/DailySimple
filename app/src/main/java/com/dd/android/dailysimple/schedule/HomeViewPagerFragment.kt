package com.dd.android.dailysimple.schedule

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.databinding.FragmentViewPagerBinding
import com.dd.android.dailysimple.schedule.common.BaseFragment
import com.dd.android.dailysimple.schedule.google.GoogleAccountViewModel
import com.google.android.material.tabs.TabLayoutMediator

class HomeViewPagerFragment : BaseFragment<FragmentViewPagerBinding>() {

    override val layout = R.layout.fragment_view_pager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bind.accountViewModel = activity.viewModels<GoogleAccountViewModel>().value
        setUpViewPager()
    }

    private fun setUpViewPager() {
        val context = requireContext()
        val tabLayout = bind.tabs
        val viewPager = bind.homeViewPager

        viewPager.adapter = HomeViewPagerAdapter(this)
        viewPager.isUserInputEnabled = false
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = context.getString(TabTitleResId[position])
            tab.icon = context.getDrawable(TabIconResId[position])
        }.attach()
    }
}