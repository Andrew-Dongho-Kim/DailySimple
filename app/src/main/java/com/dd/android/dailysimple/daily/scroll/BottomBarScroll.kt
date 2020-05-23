package com.dd.android.dailysimple.daily.scroll

import android.animation.ObjectAnimator
import android.util.Log
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.recyclerview.widget.RecyclerView

private const val SHOW_DELAY = 1000L
private const val HIDE_DELAY = 0L

class BottomBarScroll(private val bottomBar: View) : RecyclerView.OnScrollListener() {

    private var isBottomBarShowing = true

    private var isHideAnimating = false


    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        Log.d("TEST-DH", "onScrolled : $dy, bottom:$isBottomBarShowing")
        if (dy > 0) {
            hideBottomBar()
        } else {
            showBottomBar()
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        Log.d("TEST-DH", "onScrollStateChanged : $newState")
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            showBottomBar()
        }
    }

    private fun hideBottomBar() {
        if (!isBottomBarShowing) return
        isBottomBarShowing = false


        isHideAnimating = true
        hideAnimation()
    }

    private fun hideAnimation() {
        with(ObjectAnimator.ofFloat(bottomBar, "translationY", 0f, bottomBar.height.toFloat())) {
            startDelay = HIDE_DELAY
            doOnEnd { executePendingAnimation() }
            start()
        }
    }

    private fun showBottomBar() {
        if (isBottomBarShowing) return
        isBottomBarShowing = true

        if (isHideAnimating) return
        showAnimation()
    }

    private fun showAnimation() {
        with(ObjectAnimator.ofFloat(bottomBar, "translationY", bottomBar.height.toFloat(), 0f)) {
            startDelay = SHOW_DELAY
            start()
        }
    }

    private fun executePendingAnimation() {
        Log.e("TEST-DH", "executePendingAnimation() :$isBottomBarShowing")
        isHideAnimating = false
        if (isBottomBarShowing) {
            showAnimation()
        }
    }

}