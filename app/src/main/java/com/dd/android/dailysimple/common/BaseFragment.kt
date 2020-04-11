package com.dd.android.dailysimple.common

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.utils.DateUtils
import java.util.*

abstract class BaseFragment<B : ViewDataBinding> : Fragment() {

    private val dateChangeReceiver by lazy { DateChangeReceiver(this) }
    val dateChangedObserver: OnDateChangedObserver = dateChangeReceiver

    val navController by lazy {
        Navigation.findNavController(
            activity,
            R.id.main_navigation_host
        )
    }

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
                navController.popBackStack()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun popBackStack() {
        navController.popBackStack()
    }
}

class DateChangeReceiver(private val fragment: Fragment) :
    LifecycleObserver, OnDateChangedObserver, BroadcastReceiver() {

    private var lastDate = Date()

    private var started = false

    private var registered = false

    private val context by lazy { fragment.requireContext() }

    private val listeners = mutableSetOf<OnDateChangedListener>()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action in ACTIONS) {
            notifyIfNeed()
        }
    }

    override fun addOnDateChangedListener(listener: OnDateChangedListener) {
        listeners.add(listener)
        registerReceiver()
    }

    override fun removeOnDateChangedListener(listener: OnDateChangedListener) {
        listeners.remove(listener)
        if (listeners.size == 0) {
            unregisterReceiver()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        started = true
        registerReceiver()
        notifyIfNeed()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        started = false
        unregisterReceiver()
    }

    private fun registerReceiver() {
        if (registered) {
            logD("Receiver is already registered")
            return
        }
        if (listeners.size == 0) {
            logD("Listeners for date changes size is 0")
            return
        }
        if (!started) {
            logD("Lifecyle for date changes is not active!")
            return
        }

        context.registerReceiver(this, IntentFilter().apply {
            ACTIONS.forEach { addAction(it) }
        })
        registered = true
    }

    private fun unregisterReceiver() {
        if (!registered) return

        context.unregisterReceiver(this)
        registered = false
    }

    private fun notifyIfNeed() {
        val currDate = Date()
        val lastDay = DateUtils.getDate(lastDate)
        val currDay = DateUtils.getDate(currDate)

        if (lastDay != currDay) {
            lastDate = currDate
            listeners.forEach { it.onDateChanged() }
        }
    }

    companion object {
        private const val TAG = "DateChangeReceiver"

        private val ACTIONS = arrayOf(
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_DATE_CHANGED
        )

        private fun logD(message: String) = Log.d(TAG, message)
    }
}

interface OnDateChangedObserver {
    fun addOnDateChangedListener(listener: OnDateChangedListener)

    fun removeOnDateChangedListener(listener: OnDateChangedListener)
}

interface OnDateChangedListener {
    fun onDateChanged()
}