package com.dd.android.dailysimple.schedule.group

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.databinding.FragmentCreateGroupScheduleBinding

class CreateGroupScheduleFragment : Fragment() {

    private lateinit var bind: FragmentCreateGroupScheduleBinding

    private lateinit var activity: AppCompatActivity

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity as AppCompatActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        FragmentCreateGroupScheduleBinding.inflate(inflater, container, false).run {
            bind = this
            root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initToolbar()
    }

    private fun initToolbar() {
        activity.setSupportActionBar(bind.toolbar)
        activity.supportActionBar?.apply {
            setDisplayShowCustomEnabled(true)
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(false)
            setCustomView(R.layout.cancel_and_done_toolbar)
        }
    }
}