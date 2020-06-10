package com.dd.android.dailysimple.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.BaseActivity
import com.dd.android.dailysimple.common.di.appContext

/**
 * The App Widget host calls the configuration Activity and
 * the configuration Activity should always return a result.
 * The result should include the App Widget ID passed by the
 * Intent that launched the Activity(saved in the intent extras
 * as EXTRA_APPWIDGET_ID)
 *
 * The onUpdate() method will not be called when the App Widget
 * is created (the system will not send the ACTION_APPWIDGET_UPDATE
 * broadcast when a configuration Activity is launched).
 * It is the responsibility of the configuration Activity to request an
 * update from the AppWidgetManager when the App Widget is first created.
 * However, onUpdate() will be called for subsequent updates - it is
 * only skipped the first time
 */
class WidgetConfigActivity : BaseActivity() {

    private val appWidgetManager by lazy { AppWidgetManager.getInstance(this) }

    private var appWidgetId = INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the result to CANCELED. This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
//        setResult(RESULT_CANCELED)
        setContentView(R.layout.activity_app_widiget_config)

        // Find the widget id from the intent.
        appWidgetId = intent?.extras?.getInt(
            EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID
        ) ?: INVALID_APPWIDGET_ID

        if (appWidgetId == INVALID_APPWIDGET_ID) finish()

        update()
    }

    fun update() {
        RemoteViews(packageName, R.layout.app_widget_today_task).also {
            appWidgetManager.updateAppWidget(appWidgetId, it)
        }
        setResult(RESULT_OK, Intent().apply { putExtra(EXTRA_APPWIDGET_ID, appWidgetId) })
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}