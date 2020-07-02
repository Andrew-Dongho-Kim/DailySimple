package com.dd.android.dailysimple.daily.scroll

import android.view.View
import androidx.recyclerview.widget.RecyclerView

private const val ANIMATION_DURATION = 500L

class BottomBarScroll(private val bottomBar: View) : RecyclerView.OnScrollListener() {

    var isShowing: Boolean? = null

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > 0) {
            hideAnimation()
        } else {
            showAnimation()
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
    }

    private fun hideAnimation() {
        if (isShowing == false) return

        bottomBar.animate().cancel()
        bottomBar.animate().translationY(200f).setDuration(ANIMATION_DURATION).start()
        isShowing = false
    }

    private fun showAnimation() {
        if (isShowing == true) return

        bottomBar.animate().cancel()
        bottomBar.animate().translationY(0f).setDuration(ANIMATION_DURATION).start()
        isShowing = true
    }
}