package com.dd.android.dailysimple.provider.calendar

import android.content.Context
import android.database.Cursor
import android.provider.CalendarContract.*
import androidx.annotation.IntDef
import androidx.core.content.ContentResolverCompat
import androidx.core.os.CancellationSignal
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.livedata.ContentProviderLiveData
import com.dd.android.dailysimple.db.data.DailySchedule
import com.dd.android.dailysimple.provider.calendar.EventReminderMethod.Companion.ALERT
import com.dd.android.dailysimple.provider.calendar.EventReminderMethod.Companion.DEFAULT
import com.dd.android.dailysimple.provider.calendar.EventReminderMethod.Companion.EMAIL
import com.dd.android.dailysimple.provider.calendar.EventReminderMethod.Companion.SMS
import java.util.*

private const val LOG_TAG = "CalendarProviderHelper"

private inline fun logD(crossinline message: () -> String) = Logger.d(LOG_TAG, message)

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


    fun getEvents(beginTime: Long, endTime: Long): LiveData<List<DailySchedule>> {
        return liveData {
            emit(queryInstances(beginTime, endTime))
            emitSource(
                ContentProviderLiveData(
                    context,
                    Instances.CONTENT_URI
                ) {
                    queryInstances(beginTime, endTime)
                }
            )
        }
    }

    private fun queryEvents(beginTime: Long, endTime: Long): List<DailySchedule> {
        val utcBegin = beginTime + 1
        val utcEnd = endTime - 1

        val list = mutableListOf<DailySchedule>()
        val selection = "? <= ${Events.DTSTART} AND ? <= ${Events.DTEND}"
        val selectionArgs = arrayOf(utcBegin.toString(), utcEnd.toString())
        ContentResolverCompat.query(
            context.contentResolver,
            Events.CONTENT_URI,
            EVENT_PROJECTION,
            selection,
            selectionArgs,
            null,
            cancelSignal
        ).use {
            if (it.moveToFirst()) {
                do {
                    list.add(createFromEvents(it))
                } while (it.moveToNext())
            }
        }
        return list
    }

    private fun createFromEvents(cursor: Cursor) =
        DailySchedule(
            id = cursor.getLong(Events._ID),
            title = cursor.getString(Events.TITLE),
            start = cursor.getLong(Events.DTSTART),
            end = cursor.getLong(Events.DTEND),
            memo = cursor.getString(Events.DESCRIPTION),
            color = cursor.getInt(Events.DISPLAY_COLOR)
        ).also {
            logD {
                "Schedule title:${it.title}, start:${Date(it.start)}, end:${Date(
                    it.end
                )}"
            }
        }

    private fun queryInstances(beginTime: Long, endTime: Long): List<DailySchedule> {
        val utcBegin = beginTime + 1
        val utcEnd = endTime - 1

        logD { "queryInstances begin:${Date(utcBegin)}, end:${Date(utcEnd)}" }
        val list = mutableListOf<DailySchedule>()
        Instances.query(context.contentResolver, INSTANCE_PROJECTION, utcBegin, utcEnd).use {
            if (it.moveToFirst()) {
                do {
                    list.add(createFromInstances(it))
                } while (it.moveToNext())
            }
        }
        return list
    }

    private fun createFromInstances(cursor: Cursor) = DailySchedule(
        id = cursor.getLong(Instances.EVENT_ID),
        title = cursor.getString(Instances.TITLE),
        start = cursor.getLong(Instances.BEGIN),
        end = cursor.getLong(Instances.END),
        memo = cursor.getString(Instances.DESCRIPTION),
        color = cursor.getInt(Instances.DISPLAY_COLOR)
    ).also {
        logD {
            "Schedule title:${it.title}, start:${Date(it.start)}, end:${Date(
                it.end
            )}"
        }
    }

    fun getCalendar(accountName: String, accountType: String): Cursor =
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

    private fun Cursor.getInt(columnName: String) =
        getInt(getColumnIndex(columnName))

    private fun Cursor.getLong(columnName: String) =
        getLong(getColumnIndex(columnName))

    private fun Cursor.getString(columnName: String) =
        getString(getColumnIndex(columnName))


    companion object {
        private val INSTANCE_PROJECTION = arrayOf(
            Instances.EVENT_ID,
            Instances.TITLE,
            Instances.BEGIN,
            Instances.END,
            Instances.EVENT_LOCATION,
            Instances.DESCRIPTION,
            Instances.DISPLAY_COLOR
        )

        private val EVENT_PROJECTION = arrayOf(
            Events._ID,
            Events.TITLE,
            Events.DTSTART,
            Events.DTEND,
            Events.EVENT_LOCATION,
            Events.DESCRIPTION,
            Events.DISPLAY_COLOR,
            Events.ALL_DAY
        )

        private val CALENDAR_PROJECTION = arrayOf(
            Calendars._ID,
            Calendars.ACCOUNT_NAME,
            Calendars.CALENDAR_DISPLAY_NAME,
            Calendars.CALENDAR_COLOR
        )
    }
}