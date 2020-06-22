package com.dd.android.dailysimple.notification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.dd.android.dailysimple.daily.AppConst.UNKNOWN

@SuppressLint("WrongConstant")
fun createNotificationChannel(
    context: Context,
    channelId: String,
    channelName: String,
    channelDescription: String,
    importance: Int = UNKNOWN,
    enableLights: Boolean = true,
    enableVibration: Boolean = true
) {
    // Starting with API level 26, all notifications must be assigned to a channel.
    // If you tap and hold the app launcher icon, and tap notifications,
    // you will see a list of notification channels associated with the app.
    // Channels represent a "type" of notification
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    val channel = NotificationChannel(
        channelId,
        channelName,
        if (importance == UNKNOWN) IMPORTANCE_DEFAULT else importance
    ).apply {
        enableLights(enableLights)
        enableVibration(enableVibration)
        description = channelDescription
    }

    NotificationManagerCompat.from(context).createNotificationChannel(channel)
}