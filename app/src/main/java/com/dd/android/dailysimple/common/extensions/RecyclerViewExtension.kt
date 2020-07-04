package com.dd.android.dailysimple.common.extensions

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.findChildViewUnder2(x: Float, y: Float): View? {
    val count = childCount
    for (i in count - 1 downTo 0) {
        val child = getChildAt(i)
        val lp = child.layoutParams as RecyclerView.LayoutParams
        val insets = getItemDecoratorInsetsForChild(child)

        if (x >= (child.left - insets.left - lp.leftMargin) &&
            x <= (child.right + insets.right + lp.rightMargin) &&
            y >= (child.top - insets.top - lp.topMargin) &&
            y <= (child.bottom + insets.bottom + lp.bottomMargin)
        ) {
            return child
        }
    }
    return null
}

fun RecyclerView.getItemDecoratorInsetsForChild(child: View): Rect {
    val insets = Rect(0, 0, 0, 0)

    getChildViewHolder(child) ?: return insets
    val state = RecyclerView.State()
    val rect = Rect()

    val decorCount = itemDecorationCount
    for (i in 0 until decorCount) {
        rect.set(0, 0, 0, 0)
        getItemDecorationAt(i).getItemOffsets(rect, child, this, state)
        insets.left += rect.left
        insets.top += rect.top
        insets.right += rect.right
        insets.bottom += rect.bottom
    }
    return insets
}