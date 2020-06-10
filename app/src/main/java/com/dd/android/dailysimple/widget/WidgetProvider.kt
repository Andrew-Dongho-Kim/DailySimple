package com.dd.android.dailysimple.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.dd.android.dailysimple.HomeActivity
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.di.settingManager
import com.dd.android.dailysimple.widget.WidgetConst.DEFAULT_WIDGET_ALPHA
import com.dd.android.dailysimple.widget.WidgetConst.SETTING_KEY_WIDGET_ALPHA

class WidgetProvider : AppWidgetProvider() {

    private val settingManager by lazy { settingManager() }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { id ->

            val remoteViews = RemoteViews(context.packageName, R.layout.app_widget_today_task)
                .setUpBackground()
                .setUpTitleAction(context)
                .setUpSettingAction(context)
                .setUpListView(context, id)

            appWidgetManager.updateAppWidget(id, remoteViews)
        }
    }

    private fun RemoteViews.setUpSettingAction(context: Context): RemoteViews {
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, WidgetConfigActivity::class.java), 0
        )
        setOnClickPendingIntent(R.id.setting, pendingIntent)
        return this
    }

    private fun RemoteViews.setUpTitleAction(context: Context): RemoteViews {
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, HomeActivity::class.java), 0
        )
        setOnClickPendingIntent(R.id.title, pendingIntent)
        return this
    }

    private fun RemoteViews.setUpBackground(): RemoteViews {
        setInt(
            R.id.widget_background,
            "setImageAlpha",
            settingManager.getInt(SETTING_KEY_WIDGET_ALPHA, DEFAULT_WIDGET_ALPHA)
        )
        return this
    }

    private fun RemoteViews.setUpListView(context: Context, appWidgetId: Int): RemoteViews {
        val intent = Intent(context, TaskListRemoteViewsService::class.java).apply {
            putExtra(EXTRA_APPWIDGET_ID, appWidgetId)
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

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {

    }
}