package com.dd.android.dailysimple.common.widget.recycler

import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.dd.android.dailysimple.common.Logger

private const val TAG = "ExpandableItemDecoration"

private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class ExpandableItemDecoration : ItemDecoration() {

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {


    }

}