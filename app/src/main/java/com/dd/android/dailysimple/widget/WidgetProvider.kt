package com.dd.android.dailysimple.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.dd.android.dailysimple.SettingManager
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.utils.DateUtils.msDateFrom
import com.dd.android.dailysimple.widget.WidgetConst.ACTION_UPDATE_SELECTED_DATE
import com.dd.android.dailysimple.widget.WidgetConst.DATA_SELECTED_DATE
import com.dd.android.dailysimple.widget.WidgetConst.SETTING_KEY_SELECTED_DATE
import com.dd.android.dailysimple.widget.remoteviews.TodayTaskRemoteViews
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "WidgetProvider"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

@AndroidEntryPoint
class WidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var settingManager: SettingManager

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_UPDATE_SELECTED_DATE -> updateSelectedDate(context, intent)
            else -> super.onReceive(context, intent)
        }
    }

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
                .setUpNextDateButton()
                .setUpPrevDateButton()
                .setUpSettingAction()
                .setUpListView(id)

            appWidgetManager.updateAppWidget(id, remoteViews)
            logD { "Update widget : $id" }
        }
    }

    private fun updateSelectedDate(context: Context, intent: Intent) {
        val selectedDate = intent.getLongExtra(DATA_SELECTED_DATE, msDateFrom())
        settingManager.putLong(SETTING_KEY_SELECTED_DATE, selectedDate)

        AppWidgetManager.getInstance(context).run {
            val appWidgetIds = getAppWidgetIds(ComponentName(context, context.packageName))
            updateAppWidget(appWidgetIds, TodayTaskRemoteViews(context).setUpTitle())

            logD { "updateSelectedDate appWidgetIds:$appWidgetIds, selectedDate:$selectedDate" }
        }
    }


}