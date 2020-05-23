package com.dd.android.dailysimple.common.widget

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class CenterScrollLinearLayoutManager(private val context: Context) : LinearLayoutManager(context) {

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView?,
        state: RecyclerView.State?,
        position: Int
    ) {
        startSmoothScroll(
            CenterLinearSmoothScroller(
                context
            ).apply {
                targetPosition = position
            })
    }
}

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