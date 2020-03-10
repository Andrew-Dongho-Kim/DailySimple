package com.dd.android.dailysimple.schedule.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dd.android.dailysimple.databinding.FragmentCreateGroupScheduleBinding

class CreateGroupScheduleFragment : Fragment() {

    private lateinit var bind: FragmentCreateGroupScheduleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        FragmentCreateGroupScheduleBinding.inflate(inflater, container, false).run {
            bind = this
            root
        }

}