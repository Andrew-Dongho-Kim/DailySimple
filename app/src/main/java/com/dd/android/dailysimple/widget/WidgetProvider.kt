package com.dd.android.dailysimple.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.widget.remoteviews.TodayTaskRemoteViews

private const val TAG = "WidgetProvider"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { id ->
            val remoteViews = TodayTaskRemoteViews(
                context
            )
                .setUpBackground()
                .setUpTitle()
                .setUpTitleAction()
                .setUpSettingAction()
                .setUpListView(id)

            appWidgetManager.updateAppWidget(id, remoteViews)
            logD { "Update widget : $id" }
        }
    }


    override fun onDeleted(context: Context, appWidgetIds: IntArray) {

    }
}