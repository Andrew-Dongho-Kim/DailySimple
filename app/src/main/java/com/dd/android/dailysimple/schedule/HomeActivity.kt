package com.dd.android.dailysimple.schedule

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.LOG
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.schedule.common.BaseActivity
import com.dd.android.dailysimple.schedule.common.DateUtils
import com.dd.android.dailysimple.schedule.google.GoogleAccountController
import com.dd.android.dailysimple.schedule.google.GoogleAccountViewModel
import com.dd.android.dailysimple.schedule.google.SignedState
import com.dd.android.dailysimple.schedule.provider.calendar.CalendarProviderHelper
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class HomeActivity : BaseActivity() {

    private val accountController = GoogleAccountController(this)

    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val viewModel = ViewModelProvider(this).get(GoogleAccountViewModel::class.java)
        accountController.signInOutResult.observe(this,
            Observer {
                it.result?.signInAccount?.let { account ->
                    viewModel.update(account)
                }
            })

        (application as DailySimpleApplication).appComponent.inject(this)

        test()
    }


    fun test() {
        val calendarHelper = CalendarProviderHelper(applicationContext)
        calendarHelper.getTodayEvents(

        ).observe(this, Observer {
            Log.e("TEST-DH", "Calendar: ${it}")
        })



        Log.d("TEST-DH", "Calendar1:${Date(DateUtils.today())}")
        Log.d("TEST-DH", "Calendar2:${Date(DateUtils.todayAfter(1))}")
    }

}