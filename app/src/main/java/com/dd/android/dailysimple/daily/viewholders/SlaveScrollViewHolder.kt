package com.dd.android.dailysimple.daily.viewholders

import android.view.MotionEvent
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dd.android.dailysimple.common.widget.recycler.ItemModel
import com.dd.android.dailysimple.common.widget.recycler.ViewHolder2


data class SharedScrollStatus(
    var absoluteScrollDx: Int = 0,
    var absoluteScrollDy: Int = 0
)

abstract class SlaveScrollViewHolder<B : ViewDataBinding, M : ItemModel>(
    parent: ViewGroup,
    @LayoutRes layoutResId: Int,
    @IdRes recyclerId: Int,
    variableId: Int = 0,
    private val sharedScrollStatus: SharedScrollStatus
) : ViewHolder2<B, M>(parent, layoutResId, variableId) {

    val layoutManager = object : LinearLayoutManager(parent.context) {
//        override fun canScrollHorizontally(): Boolean {
//            return isMasterScrolling
//        }
    }

    val recycler: RecyclerView = bind.root.findViewById<RecyclerView>(recyclerId)
        .also {
            it.layoutManager = layoutManager
        }

//        set(value) {
//            field?.removeOnItemTouchListener(onBlockTouchWhenScroll)
//            field = value
//            field?.addOnItemTouchListener(onBlockTouchWhenScroll)
//        }


    var isMasterScrolling: Boolean = false

    @CallSuper
    override fun bindTo(itemModel: ItemModel) {
        super.bindTo(itemModel)
        with(sharedScrollStatus) {
            recycler.scrollTo(absoluteScrollDx, absoluteScrollDy)
        }
    }

    private val onBlockTouchWhenScroll = object : RecyclerView.OnItemTouchListener {
        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            return isMasterScrolling
        }
    }
}