package com.dd.android.dailysimple.schedule

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.schedule.common.BaseActivity
import com.dd.android.dailysimple.schedule.google.GoogleAccountController
import com.dd.android.dailysimple.schedule.google.GoogleAccountViewModel
import com.dd.android.dailysimple.schedule.google.SignInOutResult
import com.dd.android.dailysimple.schedule.provider.calendar.CalendarProviderHelper
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes

private const val REQUEST_GOOGLE_PLAY_SERVICES = 1001


class HomeActivity : BaseActivity() {

    private val accountController = GoogleAccountController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val viewModel = ViewModelProvider(this).get(GoogleAccountViewModel::class.java)
        accountController.signInOutResult.observe(this,
            Observer {
                it.result?.signInAccount?.let { account ->
                    viewModel.update(account)
                    Log.d("TEST-DH", "ViewModel:$viewModel, ${account.displayName}, ${account.email}")
                }
            })

        test()
    }


    fun test() {
        val calendarHelper = CalendarProviderHelper(applicationContext)
        calendarHelper.dumpCalendar("nazcazon5638@gmail.com", "com.google")
        calendarHelper.dumpEvents()
    }

}