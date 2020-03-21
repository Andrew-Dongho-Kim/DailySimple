package com.dd.android.dailysimple.schedule.common

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController

abstract class BaseFragment<B : ViewDataBinding> : Fragment() {

    private val naviController by lazy { view?.findNavController()!! }

    private lateinit var _activity: AppCompatActivity
    protected val activity: AppCompatActivity
        get() = _activity

    private lateinit var _bind: B
    protected val bind: B
        get() = _bind

    @get:LayoutRes
    abstract val layout: Int

    @CallSuper
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        _activity = activity as AppCompatActivity
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bind = DataBindingUtil.inflate(inflater, layout, container, false)
        _bind.lifecycleOwner = viewLifecycleOwner
        return bind.root
    }

    @CallSuper
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                naviController.popBackStack()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun popBackStack() {
        naviController.popBackStack()
    }
}