package com.dd.android.dailysimple.common.widget.recycler

import android.graphics.Canvas
import android.graphics.Rect
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.extensions.findChildViewUnder2


private const val TAG = "StickyHeaderItemDecoration"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)


internal class StickyHeaderItemDecoration(
    parent: RecyclerView,
    private val isHeader: (itemPosition: Int) -> Boolean
) : ItemDecoration() {

    private var currentHeader: Pair<Int, ViewHolder>? = null

    init {
        parent.adapter?.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                // clear saved header as it can be outdated now
                logD { "Data changed! DrawOverHeader:null" }
                currentHeader = null
            }
        })

        parent.doOnEachNextLayout {
            // clear saved layout as it may need layout update
            currentHeader = null
        }

        // handle click on sticky header
        parent.addOnItemTouchListener(object : SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(
                recyclerView: RecyclerView,
                motionEvent: MotionEvent
            ): Boolean {
                return if (motionEvent.action == ACTION_DOWN) {
                    motionEvent.y <= currentHeader?.second?.itemView?.bottom ?: 0
                } else false
            }
        })
    }

    override fun onDrawOver(
        c: Canvas,
        parent: RecyclerView,
        state: State
    ) {
        super.onDrawOver(c, parent, state)

        val topChild = parent.findChildViewUnder2(
            parent.paddingLeft.toFloat(),
            parent.paddingTop.toFloat()
        ) ?: return

        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == NO_POSITION) {
            logD { "Top child position : no-position" }
            return
        }

        val header = getHeaderViewForItem(topChildPosition, parent) ?: return

        val contactPoint = header.bottom + parent.paddingTop
        val childInContact = getChildInContact(parent, contactPoint) ?: return
        val childPosition = parent.getChildAdapterPosition(childInContact)

        if (childPosition < 0) return
        if (topChildPosition == childPosition) return

        if (isHeader(childPosition)) {
            moveHeader(c, header, childInContact)
            return
        }

        drawHeader(c, header, parent.paddingTop)
    }

    private fun getHeaderViewForItem(itemPosition: Int, parent: RecyclerView): View? {
        parent.adapter ?: return null

        val headerPos = getHeaderPositionForItem(itemPosition)
        if (headerPos == NO_POSITION) return null

        with(parent.adapter!!) {
            val headerType = getItemViewType(headerPos)
            // if match reuse viewHolder

            val prevHeaderPosition = currentHeader?.first
            val prevHeaderHolder = currentHeader?.second
            if (prevHeaderPosition == headerPos &&
                prevHeaderHolder?.itemViewType == headerType
            ) {
                return prevHeaderHolder.itemView
            }

            return createViewHolder(parent, headerType).let { vh ->
                vh.itemView.setTag(R.id.overDraw, true)
                onBindViewHolder(vh, headerPos)
                onMeasureAndLayout(parent, vh.itemView)
                logD {
                    "create,bind,measure,layout: " +
                            "viewType=${vh.itemViewType}, model:${(vh as? ViewHolder2<*, *>)?.model}"
                }
                // save for next draw
                currentHeader = headerPos to vh
                vh.itemView
            }
        }
    }

    private fun getHeaderPositionForItem(itemPosition: Int): Int {
        var headerPosition = NO_POSITION
        var currentPosition = itemPosition
        do {
            if (isHeader(currentPosition)) {
                headerPosition = currentPosition
                break
            }
            currentPosition -= 1
        } while (currentPosition >= 0)
        return headerPosition
    }

    /**
     * Properly measures and layouts the top sticky header.
     *
     * @param parent ViewGroup: RecyclerView in this case.
     */
    private fun onMeasureAndLayout(parent: ViewGroup, view: View) {
        // Specs for parent (RecyclerView)
        val widthSpec = MeasureSpec.makeMeasureSpec(parent.width, MeasureSpec.EXACTLY)
        val heightSpec = MeasureSpec.makeMeasureSpec(parent.height, MeasureSpec.UNSPECIFIED)

        // Specs for children (headers)
        val childWidthSpec = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight,
            view.layoutParams.width
        )
        val childHeightSpec = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            view.layoutParams.height
        )

        view.measure(childWidthSpec, childHeightSpec)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    private fun getChildInContact(parent: RecyclerView, contactPoint: Int): View? {
        var childInContact: View? = null
        val bounds = Rect()
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, bounds)
            if (bounds.bottom > contactPoint) {
                if (bounds.top <= contactPoint) {
                    // This child overlaps the contactPoint
                    childInContact = child
                    break
                }
            }
        }
        return childInContact
    }

    private fun moveHeader(
        c: Canvas,
        currentHeader: View,
        nextHeader: View
    ) {
        c.save()
        val moveTop = (nextHeader.top - currentHeader.height).toFloat()

        logD { "moveHeader : $moveTop" }
        c.translate(0f, if (moveTop > 0) 0f else moveTop /*+ paddingTop*/)
        currentHeader.draw(c)
        c.restore()
    }

    private fun drawHeader(c: Canvas, header: View, paddingTop: Int) {
        c.save()
        c.translate(0f, paddingTop.toFloat())
        header.draw(c)
        c.restore()
    }

    private inline fun View.doOnEachNextLayout(crossinline action: (view: View) -> Unit) {
        addOnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
            action(
                view
            )
        }
    }
}