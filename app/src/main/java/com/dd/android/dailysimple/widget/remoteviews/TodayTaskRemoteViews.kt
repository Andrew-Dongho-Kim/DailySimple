package com.dd.android.dailysimple.widget.remoteviews

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.dd.android.dailysimple.HomeActivity
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.CalendarConst
import com.dd.android.dailysimple.common.di.settingManager
import com.dd.android.dailysimple.common.utils.DateUtils.msDateFrom
import com.dd.android.dailysimple.common.utils.DateUtils.msFrom
import com.dd.android.dailysimple.common.widget.setImageViewImageAlpha
import com.dd.android.dailysimple.widget.TaskListRemoteViewsService
import com.dd.android.dailysimple.widget.WidgetConfigActivity
import com.dd.android.dailysimple.widget.WidgetConst
import com.dd.android.dailysimple.widget.WidgetConst.ACTION_UPDATE_SELECTED_DATE
import com.dd.android.dailysimple.widget.WidgetConst.DATA_SELECTED_DATE
import com.dd.android.dailysimple.widget.WidgetConst.SETTING_KEY_SELECTED_DATE
import java.util.*

class TodayTaskRemoteViews(private val context: Context) :
    RemoteViews(context.packageName, R.layout.app_widget_today_task) {

    private val settingManager by lazy { settingManager() }

    private fun msSelectedDate() =
        settingManager.getLong(SETTING_KEY_SELECTED_DATE, msDateFrom())

    fun setUpSettingAction(): TodayTaskRemoteViews {
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(
                context,
                WidgetConfigActivity::class.java
            ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }, PendingIntent.FLAG_UPDATE_CURRENT
        )
        setOnClickPendingIntent(R.id.add, pendingIntent)
        return this
    }

    fun setUpTitle(): TodayTaskRemoteViews {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = msSelectedDate()
        }
        setTextViewText(
            R.id.title,
            context.getString(
                R.string.year_and_month_date,
                context.getString(R.string.year, calendar.get(Calendar.YEAR)),
                context.getString(CalendarConst.MONTHS[calendar.get(Calendar.MONTH)]),
                context.getString(R.string.date, calendar.get(Calendar.DATE))
            )
        )
        setOnClickPendingIntent(
            R.id.title, PendingIntent.getActivity(
                context,
                0,
                Intent(context, HomeActivity::class.java), 0
            )
        )
        return this
    }


    fun setUpNextDateButton(): TodayTaskRemoteViews {
        setOnClickPendingIntent(
            R.id.next,
            PendingIntent.getBroadcast(context, 0, Intent(ACTION_UPDATE_SELECTED_DATE).apply {
                `package` = context.packageName
                putExtra(DATA_SELECTED_DATE, msFrom(msSelectedDate(), dates = 1))
            }, FLAG_UPDATE_CURRENT)
        )
        return this
    }

    fun setUpPrevDateButton(): TodayTaskRemoteViews {
        setOnClickPendingIntent(
            R.id.prev,
            PendingIntent.getBroadcast(context, 0, Intent(ACTION_UPDATE_SELECTED_DATE).apply {
                `package` = context.packageName
                putExtra(DATA_SELECTED_DATE, msFrom(msSelectedDate(), dates = -1))
            }, FLAG_UPDATE_CURRENT)
        )
        return this
    }

    fun setUpBackground(): TodayTaskRemoteViews {
        setImageViewImageAlpha(
            R.id.widget_background,
            settingManager.getInt(
                WidgetConst.SETTING_KEY_WIDGET_ALPHA,
                WidgetConst.DEFAULT_WIDGET_ALPHA
            )
        )
        return this
    }

    fun setUpListView(appWidgetId: Int): TodayTaskRemoteViews {
        val intent = Intent(context, TaskListRemoteViewsService::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
        }

        // Set up the RemoteViews object to use a RemoteViews adapter.
        // This adapter connects to ad RemoteViewsService through the specified intent.
        // This is how you populate the data.
        setRemoteAdapter(R.id.task_list, intent)

        // The empty view is displayed when the collection has no items.
        // It should be in the same layout used to instantiate the RemoteViews
        // object above.
        setEmptyView(R.id.task_list, R.id.empty_view)

        setPendingIntentTemplate(
            R.id.task_list, PendingIntent.getActivity(
                context, 0, Intent(), 0
            )
        )
        return this
    }
}