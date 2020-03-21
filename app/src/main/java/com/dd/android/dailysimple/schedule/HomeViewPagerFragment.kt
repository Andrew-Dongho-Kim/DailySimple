package com.dd.android.dailysimple.schedule

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.databinding.FragmentViewPagerBinding
import com.dd.android.dailysimple.schedule.google.GoogleAccountViewModel
import com.google.android.material.tabs.TabLayoutMediator

class HomeViewPagerFragment : Fragment() {

    private lateinit var bind: FragmentViewPagerBinding

    private lateinit var activity: AppCompatActivity

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity as AppCompatActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = FragmentViewPagerBinding.inflate(inflater, container, false)
        bind.accountViewModel = ViewModelProvider(activity).get(GoogleAccountViewModel::class.java)
        bind.lifecycleOwner = viewLifecycleOwner

        initToolbar()
        initViewPager()

        return bind.root
    }

    private fun initToolbar() {
        activity.setSupportActionBar(bind.toolbar)
        activity.supportActionBar?.apply {
            setDisplayShowCustomEnabled(false)
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(false)
            title = activity.getString(R.string.app_name)
        }
        setHasOptionsMenu(true)
    }

    private fun initViewPager() {
        val tabLayout = bind.tabs
        val viewPager = bind.homeViewPager

        viewPager.adapter = HomeViewPagerAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()
    }

    private fun getTabTitle(position: Int) = getString(TabTitleResId[position])

}