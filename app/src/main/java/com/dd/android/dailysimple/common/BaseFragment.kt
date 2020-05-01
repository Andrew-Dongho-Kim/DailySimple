package com.dd.android.dailysimple.common

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.annotation.CallSuper
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.Navigation
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.utils.DateUtils
import java.util.*

abstract class BaseFragment<B : ViewDataBinding> : Fragment() {

    val dateChangedObserver: OnDateChangedObserver by lazy { DateChangeReceiver(this) }

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
                popBackStack()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setStatusBarColor(@ColorRes colorResId: Int) {
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity, colorResId)
    }

    fun popBackStack() {
        navController.popBackStack()
        hideSoftInput()
    }

    private fun hideSoftInput() {
        val imm = _activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(bind.root.windowToken, 0)
    }
}

class DateChangeReceiver(private val fragment: Fragment) :
    LifecycleObserver, OnDateChangedObserver, BroadcastReceiver() {

    private var lastDate = Date()

    private var started = false

    private var registered = false

    private val context by lazy { fragment.requireContext() }

    private val listeners = mutableSetOf<OnDateChangedListener>()

    init {
        fragment.lifecycle.addObserver(this)
    }

    override fun onReceive(context: Context, intent: Intent) {
        logD("onReceive : $intent")
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
        logD("onStart")
        started = true
        registerReceiver()
        notifyIfNeed()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        logD("onStop")
        started = false
        unregisterReceiver()
    }

    private fun registerReceiver() {
        if (registered) return
        if (listeners.size == 0) return
        if (!started) return

        logD("registerReceiver")
        context.registerReceiver(this, IntentFilter().apply {
            ACTIONS.forEach { addAction(it) }
        })
        registered = true
    }

    private fun unregisterReceiver() {
        if (!registered) return

        logD("unregisterReceiver")
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
            logD("Notify Date Changed last : $lastDay, curr : $currDay")
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