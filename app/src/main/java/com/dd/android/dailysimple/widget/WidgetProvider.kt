package com.dd.android.dailysimple.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.di.settingManager
import com.dd.android.dailysimple.common.utils.DateUtils.msDateFrom
import com.dd.android.dailysimple.common.utils.DateUtils.msFrom
import com.dd.android.dailysimple.widget.WidgetConst.ACTION_TO_NEXT_DATE
import com.dd.android.dailysimple.widget.WidgetConst.ACTION_TO_PREV_DATE
import com.dd.android.dailysimple.widget.WidgetConst.DEFAULT_WIDGET_ALPHA
import com.dd.android.dailysimple.widget.WidgetConst.SETTING_KEY_WIDGET_ALPHA
import com.dd.android.dailysimple.widget.WidgetConst.SETTING_KEY_WIDGET_SELECTED_DATE
import com.dd.android.dailysimple.widget.remoteviews.TodayTaskRemoteViews
import java.util.*

private const val TAG = "${WidgetConst.TAG}-Task-Provider"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class WidgetProvider : AppWidgetProvider() {

    private val settingManager by lazy { settingManager() }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_TO_NEXT_DATE -> updateSelectedDate(context, 1)
            ACTION_TO_PREV_DATE -> updateSelectedDate(context, -1)
            else -> super.onReceive(context, intent)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        val alpha = settingManager.getInt(SETTING_KEY_WIDGET_ALPHA, DEFAULT_WIDGET_ALPHA)
        val currDate = settingManager.getLong(SETTING_KEY_WIDGET_SELECTED_DATE, msDateFrom())

        appWidgetIds.forEach { id ->
            val remoteViews = TodayTaskRemoteViews(context)
                .setUpBackground(alpha)
                .setUpTitle(currDate)
                .setUpNextDateButton()
                .setUpPrevDateButton()
                .setUpSettingAction()
                .setUpListView(id)

            appWidgetManager.updateAppWidget(id, remoteViews)
            logD { "Update widget : $id" }
        }
    }

    private fun updateSelectedDate(context: Context, date: Int) {
        val updatedDate = msFrom(
            settingManager.getLong(SETTING_KEY_WIDGET_SELECTED_DATE, msDateFrom()),
            dates = date
        )
        settingManager.putLong(SETTING_KEY_WIDGET_SELECTED_DATE, updatedDate)

        AppWidgetManager.getInstance(context).run {
            val appWidgetIds = getAppWidgetIds(ComponentName(context, WidgetProvider::class.java))

            updateAppWidget(appWidgetIds, TodayTaskRemoteViews(context).setUpTitle(updatedDate))
            notifyAppWidgetViewDataChanged(appWidgetIds, R.id.task_list)

            logD { "updateSelectedDate date:${Date(updatedDate)}" }
        }
    }


}