package com.dd.android.dailysimple.schedule.provider.calendar

import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.provider.CalendarContract.*
import android.util.Log
import androidx.annotation.IntDef
import androidx.core.content.ContentResolverCompat
import androidx.core.os.CancellationSignal
import com.dd.android.dailysimple.schedule.provider.calendar.EventReminderMethod.Companion.ALERT
import com.dd.android.dailysimple.schedule.provider.calendar.EventReminderMethod.Companion.DEFAULT
import com.dd.android.dailysimple.schedule.provider.calendar.EventReminderMethod.Companion.EMAIL
import com.dd.android.dailysimple.schedule.provider.calendar.EventReminderMethod.Companion.SMS
import java.util.*

private const val LOG_TAG = "CalendarProviderHelper"

data class CalendarEvent(
    val begin: Calendar,
    val end: Calendar,
    val title: String,
    val description: String? = null,
    val location: String? = null
)

@IntDef(ALERT, DEFAULT, EMAIL, SMS)
annotation class EventReminderMethod {
    companion object {
        const val ALERT = Reminders.METHOD_ALERT
        const val DEFAULT = Reminders.METHOD_DEFAULT
        const val EMAIL = Reminders.METHOD_EMAIL
        const val SMS = Reminders.METHOD_SMS
    }
}

data class EventReminder(
    @EventReminderMethod val method: Int,
    val minutes: Int
)

class CalendarProviderHelper(
    private val context: Context
) {

    private val cancelSignal by lazy { CancellationSignal() }

    fun addEvents(activity: Activity, event: CalendarEvent) =
        activity.startActivity(
            Intent(Intent.ACTION_INSERT).apply {
                data = Events.CONTENT_URI
                putExtra(EXTRA_EVENT_BEGIN_TIME, event.begin.timeInMillis)
                putExtra(EXTRA_EVENT_END_TIME, event.end.timeInMillis)
                putExtra(Events.TITLE, event.title)
                putExtra(Events.DESCRIPTION, event.description)
                putExtra(Events.EVENT_LOCATION, event.location)
                putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY) // TODO WHAT IS IT?
            }
        )

    fun addReminder(eventId: Long, reminder: EventReminder) =
        context.contentResolver.insert(
            Reminders.CONTENT_URI,
            ContentValues().apply {
                put(Reminders.EVENT_ID, eventId)
                put(Reminders.METHOD, reminder.method)
                put(Reminders.MINUTES, reminder.minutes)
            }
        )

    fun removeEvents(eventId: Long) =
        context.contentResolver.delete(
            ContentUris.withAppendedId(Events.CONTENT_URI, eventId), null, null
        )

    fun dumpEvents() {
        val cursor = getEvents(
            Calendar.getInstance().run {
                set(2020, 1, 1)
                timeInMillis
            },
            Calendar.getInstance().run {
                set(2020, 12, 31)
                timeInMillis
            }
        )

        if (cursor.moveToFirst()) {
            do {
                Log.d(LOG_TAG, "  Title:${cursor.getString(1)}")
                Log.d(LOG_TAG, "  Begin:${cursor.getLong(2)}")
                Log.d(LOG_TAG, "  End:${cursor.getLong(3)}")
                Log.d(LOG_TAG, "  Location:${cursor.getString(4)}")
                Log.d(LOG_TAG, "  Description:${cursor.getString(5)}")
            } while (cursor.moveToNext())
        }
    }

    fun getEvents(
        beginTime: Long, endTime: Long
    ): Cursor {
        val uriBuilder = Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(uriBuilder, beginTime)
        ContentUris.appendId(uriBuilder, endTime)

        return ContentResolverCompat.query(
            context.contentResolver,
            uriBuilder.build(), INSTANCE_PROJECTION, null, null, null, cancelSignal
        )
    }

    fun dumpCalendar(accountName: String, accountType: String) {
        val cursor = getCalendar(accountName, accountType)

        if (cursor.moveToFirst()) {
            Log.d(LOG_TAG, "Calendar")
            do {
                Log.d(LOG_TAG, "-----------------------------------")
                Log.d(LOG_TAG, "  Account:${cursor.getString(1)}")
                Log.d(LOG_TAG, "  Display name:${cursor.getString(2)}")
                Log.d(LOG_TAG, "  Calendar color:${cursor.getInt(3)}")
            } while (cursor.moveToNext())
        }
    }

    fun getCalendar(accountName: String, accountType: String) =
        ContentResolverCompat.query(
            context.contentResolver,
            Calendars.CONTENT_URI,
            CALENDAR_PROJECTION,
            """(
                (${Calendars.ACCOUNT_NAME} = ?) AND 
                (${Calendars.ACCOUNT_TYPE} = ?) AND 
                (${Calendars.OWNER_ACCOUNT} = ?)
            )""",
            arrayOf(accountName, accountType, accountName),
            null,
            cancelSignal
        )


    companion object {
        private val INSTANCE_PROJECTION = arrayOf(
            Instances.EVENT_ID,
            Instances.TITLE,
            Instances.BEGIN,
            Instances.END,
            Instances.EVENT_LOCATION,
            Instances.DESCRIPTION
        )

        private val CALENDAR_PROJECTION = arrayOf(
            Calendars._ID,
            Calendars.ACCOUNT_NAME,
            Calendars.CALENDAR_DISPLAY_NAME,
            Calendars.CALENDAR_COLOR
        )
    }
}