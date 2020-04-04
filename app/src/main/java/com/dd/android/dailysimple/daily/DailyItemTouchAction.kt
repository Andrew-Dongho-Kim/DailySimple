package com.dd.android.dailysimple.daily

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import androidx.cardview.widget.CardView
import androidx.core.graphics.ColorUtils
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.schedule.HomeViewPagerFragmentDirections
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.recycler.ViewHolder2
import com.dd.android.dailysimple.common.recycler.ViewHolder2.Companion.findViewById
import com.dd.android.dailysimple.common.recycler.ViewHolder2.Companion.isHeader
import kotlin.math.abs

private const val TAG = "DailyItemTouchAction"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class DailyItemTouchAction(
    private val context: Context,
    private val adapter: DailyAdapter
) : ItemTouchHelper.SimpleCallback(
    0,
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
) {

    private var swiped = false

    private val screenWidth =
        (context.getSystemService(Context.WINDOW_SERVICE)
                as WindowManager).run {
            val point = Point()
            defaultDisplay.getSize(point)
            point.x
        }

    private val background = ColorDrawable()

    private val editColor = ColorUtils.setAlphaComponent(
        context.getColor(R.color.orange), 0XCC
    )
    private val doneColor = ColorUtils.setAlphaComponent(
        context.getColor(R.color.pineapple), 0XCC
    )

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun getSwipeDirs(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        swiped = false
        return if (viewHolder.isHeader()) {
            0
        } else {
            super.getSwipeDirs(recyclerView, viewHolder)
        }
    }

    override fun onSwiped(viewHodler: RecyclerView.ViewHolder, direction: Int) {
        val vh = viewHodler as ViewHolder2
        val itemRoot = vh.findViewById<View>(R.id.item_root)
        val swipeRoot = vh.findViewById<CardView>(R.id.swipe_root)

        itemRoot ?: return
        swipeRoot ?: return

        swipeRoot.visibility = View.GONE
        itemRoot.translationX = 0f
        itemRoot.translationY = 0f
        itemRoot.alpha = 1.0f

        swiped = true

        if (direction == ItemTouchHelper.LEFT) {
            vh.itemView.findNavController().navigate(
                HomeViewPagerFragmentDirections.homeToCreateDailyScheduleFragment(
                    vh.itemModel?.id ?: -1
                )
            )
        }
        adapter.notifyItemChanged(vh.adapterPosition)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        vh: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (swiped) return

        val itemRoot = vh.findViewById<View>(R.id.item_root)
        val swipeRoot = vh.findViewById<CardView>(R.id.swipe_root)

        itemRoot ?: return
        swipeRoot ?: return

        with(itemRoot) {
            translationX = dX
            translationY = dY
            alpha = 1f - (abs(dX) / screenWidth)
        }

        val rightSwipe = dX > 0
        with(swipeRoot) {
            visibility = if (dX == 0f) View.GONE else View.VISIBLE
            setChildVisibility(
                R.id.edit_icon, R.id.edit_text,
                visibility = if (rightSwipe) View.GONE else View.VISIBLE
            )
            setChildVisibility(
                R.id.done_icon, R.id.done_text,
                visibility = if (!rightSwipe) View.GONE else View.VISIBLE
            )
            setCardBackgroundColor(if (rightSwipe) doneColor else editColor)
        }
        logD {
            """dX : $dX, dY: $dY, 
                actionState:$actionState, 
                isActive:$isCurrentlyActive, alpha:${screenWidth}"""
        }
    }

    private fun View.setChildVisibility(vararg ids: Int, visibility: Int) {
        ids.forEach { id ->
            findViewById<View>(id)?.visibility = visibility
        }
    }
}