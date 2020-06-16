package com.dd.android.dailysimple.widget.remoteviews

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.dd.android.dailysimple.HomeActivity
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.CalendarConst
import com.dd.android.dailysimple.common.di.settingManager
import com.dd.android.dailysimple.common.widget.setImageAlpha
import com.dd.android.dailysimple.widget.TaskListRemoteViewsService
import com.dd.android.dailysimple.widget.WidgetConfigActivity
import com.dd.android.dailysimple.widget.WidgetConst
import java.util.*

class TodayTaskRemoteViews(private val context: Context) :
    RemoteViews(context.packageName, R.layout.app_widget_today_task) {

    private val settingManager by lazy { settingManager() }

    fun setUpSettingAction(): TodayTaskRemoteViews {
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, WidgetConfigActivity::class.java), 0
        )
        setOnClickPendingIntent(R.id.add, pendingIntent)
        return this
    }

    fun setUpTitle(): TodayTaskRemoteViews {
        val calendar = Calendar.getInstance()
        setTextViewText(
            R.id.title,
            context.getString(
                R.string.year_and_month_date,
                context.getString(R.string.year, calendar.get(Calendar.YEAR)),
                context.getString(CalendarConst.MONTHS[calendar.get(Calendar.MONTH)]),
                context.getString(R.string.date, calendar.get(Calendar.DATE))
            )
        )
        return this
    }

    fun setUpTitleAction(): TodayTaskRemoteViews {
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, HomeActivity::class.java), 0
        )
        setOnClickPendingIntent(R.id.title, pendingIntent)
        return this
    }

    fun setUpBackground(): TodayTaskRemoteViews {
        setImageAlpha(
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
        return this
    }
}