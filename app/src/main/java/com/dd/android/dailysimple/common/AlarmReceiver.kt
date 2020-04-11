package com.dd.android.dailysimple.common

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

    }


}


class AlarmHelper(context: Context) {

    private val alarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    fun reserveAlarm(context: Context, triggerTime: Long, alarmId: Int) {
        /**
         * Like AlarmManager.set(int, long, PendingIntent), but this alarm will be allowed
         * to execute even when the system is in low-power idle modes.
         * This type of alarm must only be used for situations where it is actually required
         * that the alarm go off while in idle -- a reasonable example would be for a calendar
         * notification that should make a sound so the user is aware of it.
         *
         * When the alarm is dispatched, the app will also be added to the system's temporary
         * whitelist for approximately 10 seconds to allow that application to acquire
         * further wake locks in which to complete its work
         *
         * These alarms can significantly impact the power use of the device when idle(and
         * thus cause significant battery blame to the app scheduling them), so they should be
         * used with care. To reduce abuse, there are restrictions on how frequently these alarms
         * will go off for a particular application.
         * Under normal system operation, it will not dispatch these alarms more than about every
         * minute (at which point every such pending alarm is dispatched);
         * when in low-power idle modes this duration may be significantly longer, such as 15 minutes.
         *
         * Unlike other alarms, the system is free to reschedule this type of alarm to happen our
         * of order with any other alarms, even those from the same app.
         * This will clearly happen when the device is idle
         */
        AlarmManagerCompat.setAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            PendingIntent.getBroadcast(
                context,
                alarmId,
                Intent(ALARM_ACTION),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }

    fun cancelAlarm(context: Context, alarmId: Int) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alarmId,
                Intent(ALARM_ACTION),
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
    }

    companion object {

        private const val ALARM_ACTION = "com.dd.android.dailysimple.ALARM"

        private const val PERMISSION = "com.dd.android.dailysimple.permission.ALARM"
    }
}