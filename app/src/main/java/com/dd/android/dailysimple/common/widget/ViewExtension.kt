package com.dd.android.dailysimple.common.widget

import android.view.View
import android.view.ViewGroup.LayoutParams

fun View.adjustBigScreenWidth() {
    val metrics = context.resources.displayMetrics
    val width = metrics.widthPixels
    val height = metrics.heightPixels
    val dp = (width / metrics.density).toInt()

    if (dp in 480..588) {
        layoutParams.width = LayoutParams.MATCH_PARENT
    } else if (dp in 589..959) {
        val heightDp = (height / metrics.density).toInt()
        if (heightDp > 411) {
            layoutParams.width = (width * 0.9).toInt()
        }
    } else if (dp in 960..1919) {
        layoutParams.width = (width * 0.75).toInt()
    } else if (dp >= 1920) {
        layoutParams.width = (width * 0.5).toInt()
    } else {
        layoutParams.width = LayoutParams.MATCH_PARENT
    }
    requestLayout()
}