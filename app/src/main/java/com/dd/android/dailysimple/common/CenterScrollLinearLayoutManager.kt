package com.dd.android.dailysimple.common

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CenterScrollLinearLayoutManager(private val context: Context) : LinearLayoutManager(context) {

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView?,
        state: RecyclerView.State?,
        position: Int
    ) {
        startSmoothScroll(CenterLinearSmoothScroller(context).apply {
            targetPosition = position
        })
    }
}