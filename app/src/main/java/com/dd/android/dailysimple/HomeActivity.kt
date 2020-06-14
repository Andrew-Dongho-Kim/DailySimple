package com.dd.android.dailysimple

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
import androidx.lifecycle.Observer
import com.dd.android.dailysimple.common.BaseActivity
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.google.GoogleAccountController
import com.dd.android.dailysimple.google.GoogleAccountViewModel
import com.dd.android.dailysimple.maker.FabViewModel
import com.dd.android.dailysimple.notification.createNotificationChannel

private const val TAG = "HomeActivity"

private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class HomeActivity : BaseActivity() {
    private val accountController = GoogleAccountController(this)
    private val accountViewModel by viewModels<GoogleAccountViewModel>()
    private val fabViewModel by viewModels<FabViewModel>()

    private var isFabOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        accountController.signInOutResult.observe(this,
            Observer {
                it.result?.let { account ->
                    accountViewModel.update(account)
                    logD { "Account profile: ${account.photoUrl}" }
                }
            })

        fabViewModel.isOpen.observe(this, Observer { opened ->
            isFabOpen = opened
        })
        testNotification()
    }

    override fun onBackPressed() {
        if (isFabOpen) {
            fabViewModel.toggle()
            return
        }
        super.onBackPressed()
    }

    private fun testNotification() {
        createNotificationChannel(
            applicationContext,
            getString(R.string.channel_id_todo),
            getString(R.string.todo),
            "Test Notification"
        )
        val notificationId = 0
        val builder =
            NotificationCompat.Builder(applicationContext, getString(R.string.channel_id_todo))
                .setContentIntent(
                    PendingIntent.getActivity(
                        applicationContext,
                        notificationId,
                        Intent(applicationContext, HomeActivity::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
                .setPriority(PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Test title")
                .setContentText("I am test notification~!")

        getSystemService(NotificationManager::class.java)?.notify(notificationId, builder.build())
    }

}