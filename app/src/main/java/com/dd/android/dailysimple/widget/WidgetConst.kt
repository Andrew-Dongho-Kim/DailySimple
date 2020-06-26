package com.dd.android.dailysimple.widget

object WidgetConst {

    const val TAG = "Widget:"

    const val SETTING_KEY_WIDGET_ALPHA = "widget_alpha"
    const val SETTING_KEY_WIDGET_SELECTED_DATE = "widget_selected_date"

    private const val PREFIX_ACTION = "com.dd.android.dailysimple"
    const val ACTION_TO_NEXT_DATE = "${PREFIX_ACTION}.ACTION_TO_NEXT_DATE"
    const val ACTION_TO_PREV_DATE = "${PREFIX_ACTION}.ACTION_TO_PREV_DATE"
    const val ACTION_TO_TODAY = "${PREFIX_ACTION}.ACTION_TO_TODAY"

    const val ALPHA_MAX = 255f
    const val DEFAULT_WIDGET_ALPHA = 200

}