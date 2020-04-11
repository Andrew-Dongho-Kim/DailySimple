package com.dd.android.dailysimple

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.dd.android.dailysimple.common.BaseFragment
import com.dd.android.dailysimple.databinding.FragmentHomeBinding

class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override val layout = R.layout.fragment_home

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpNavigation()
    }

    private fun setUpNavigation() {
        val navHostFragment = childFragmentManager
            .findFragmentByTag(getString(R.string.bottom_nav_host))
                as NavHostFragment

        NavigationUI.setupWithNavController(
            bind.bottomNavigationBar,
            navHostFragment.navController
        )
    }
}