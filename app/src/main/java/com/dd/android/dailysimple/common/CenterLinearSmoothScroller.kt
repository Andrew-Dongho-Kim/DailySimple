package com.dd.android.dailysimple.common

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller

class CenterLinearSmoothScroller(context: Context) : LinearSmoothScroller(context) {

    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
        return 50f / displayMetrics.densityDpi
    }

    override fun getHorizontalSnapPreference(): Int {
        return SNAP_TO_START
    }

    override fun calculateDxToMakeVisible(view: View, snapPreference: Int): Int {
        return super.calculateDxToMakeVisible(
            view,
            snapPreference
        ) + (layoutManager!!.width - view.width) / 2
    }
}