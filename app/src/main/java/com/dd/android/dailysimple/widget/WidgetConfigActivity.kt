package com.dd.android.dailysimple.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.BaseActivity
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.utils.DateUtils.msDateFrom
import com.dd.android.dailysimple.databinding.ActivityAppWidigetConfigBinding
import com.dd.android.dailysimple.setting.SettingManager
import com.dd.android.dailysimple.widget.WidgetConst.ALPHA_MAX
import com.dd.android.dailysimple.widget.WidgetConst.DEFAULT_WIDGET_ALPHA
import com.dd.android.dailysimple.widget.WidgetConst.SETTING_KEY_WIDGET_ALPHA
import com.dd.android.dailysimple.widget.WidgetConst.SETTING_KEY_WIDGET_SELECTED_DATE
import com.dd.android.dailysimple.widget.remoteviews.TodayTaskRemoteViews

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

private const val TAG = "DailyWidgetConfig"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)
private inline fun logE(crossinline message: () -> String) = Logger.e(TAG, message)

class WidgetConfigActivity : BaseActivity() {
    private lateinit var bind: ActivityAppWidigetConfigBinding

    private val settingManager by lazy {
        SettingManager(this)
    }
    private val appWidgetManager by lazy { AppWidgetManager.getInstance(this) }
    private var appWidgetId = INVALID_APPWIDGET_ID

    private val previewWidgetBackground by lazy { bind.widgetPreview.findViewById<ImageView>(R.id.widget_background) }

    private var alpha: Int = DEFAULT_WIDGET_ALPHA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the result to CANCELED. This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
//        setResult(RESULT_CANCELED)
        bind = DataBindingUtil.setContentView(this, R.layout.activity_app_widiget_config)

        setUpAppWidgetId()
        setUpToolbar()
        setUpAlphaSeekBar()
        setUpTheme()

        updateSetting()
        updateWidget()
    }

    private fun setUpToolbar() {
        setSupportActionBar(bind.toolbar)
        supportActionBar?.let {
            it.title = "Widget Settings"
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setUpAppWidgetId() {
        // Find the widget id from the intent.
        appWidgetId = intent?.extras?.getInt(
            EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID
        ) ?: INVALID_APPWIDGET_ID

        if (appWidgetId == INVALID_APPWIDGET_ID) {
            finish()
            logE { "Invalid app widget id : $appWidgetId" }
        }
    }

    private fun setUpAlphaSeekBar() {
        setAlpha(settingManager.getInt(SETTING_KEY_WIDGET_ALPHA, DEFAULT_WIDGET_ALPHA))
        bind.alphaSeekbar.progress = alpha

        bind.alphaSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                setAlpha(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun setUpTheme() {

    }

    private fun setAlpha(alpha: Int) {
        this.alpha = alpha
        previewWidgetBackground.imageAlpha = alpha
        bind.alphaText.text =
            getString(R.string.percent, (alpha / ALPHA_MAX * 100).toInt())
    }

    private fun updateSetting() {
        settingManager.putInt(SETTING_KEY_WIDGET_ALPHA, alpha)
        settingManager.putLong(SETTING_KEY_WIDGET_SELECTED_DATE, msDateFrom())
    }

    private fun updateWidget() {
        TodayTaskRemoteViews(this)
            .setUpBackground(alpha)
            .setUpTitle(msDateFrom())
            .setUpNextDateButton()
            .setUpPrevDateButton()
            .setUpTodayButton()
            .setUpListView(appWidgetId).also {
                appWidgetManager.updateAppWidget(appWidgetId, it)
            }
        setResult(RESULT_OK, Intent().apply { putExtra(EXTRA_APPWIDGET_ID, appWidgetId) })
    }

    override fun onPause() {
        super.onPause()
    }

}