package com.dd.android.dailysimple.common.recycler

import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

interface ItemModel {
    val id: Long
}

abstract class RecyclerViewAdapter2(
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<ViewHolder2<out ViewDataBinding, out ItemModel>>() {

    val items = mutableListOf<ItemModel>()

    abstract fun onCreateViewHolder2(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder2<out ViewDataBinding, out ItemModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        onCreateViewHolder2(parent, viewType).apply {
            bind.lifecycleOwner = lifecycleOwner
        }

    override fun onBindViewHolder(
        holder: ViewHolder2<out ViewDataBinding, out ItemModel>,
        position: Int
    ) {
        holder.bindTo(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

open class ViewHolder2<B : ViewDataBinding, M : ItemModel>(
    parent: ViewGroup,
    @LayoutRes layoutResId: Int,
    val variableId: Int = 0
) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
    ) {

    var model: M? = null

    val bind: B = DataBindingUtil.bind<B>(itemView)!!

    var itemClickListener: ((View) -> Unit)? = null
        set(listener) {
            field = listener
            bind.root.setOnClickListener(field)
        }

    var itemLongClickListener: View.OnLongClickListener? = null
        set(listener) {
            field = listener
            bind.root.setOnLongClickListener(field)
        }

    @Suppress("unchecked_cast")
    @CallSuper
    open fun bindTo(itemModel: ItemModel) {
        bind.setVariable(variableId, itemModel)
        this.model = itemModel as M
    }

    companion object {
        fun RecyclerView.ViewHolder.isHeader() =
            itemViewType < 0

        fun <V : View> RecyclerView.ViewHolder.findViewById(id: Int): V? =
            itemView.findViewById<V>(id)
    }
}


interface StickyHeaderCallback {
    fun getHeaderPositionForItem(pos: Int): Int

    fun isHeader(pos: Int): Boolean
}

/**
https://github.com/timehop/sticky-headers-recyclerview/tree/master/library/src/main/java/com/timehop/stickyheadersrecyclerview
 */
class StickyHeaderDecoration(
    private val callback: StickyHeaderCallback
) : RecyclerView.ItemDecoration() {

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val topChild = parent.getChildAt(0)
        topChild ?: return

        val posTopChild = parent.getChildAdapterPosition(topChild)
        if (posTopChild == RecyclerView.NO_POSITION) {
            return
        }

        val headerPos = callback.getHeaderPositionForItem(posTopChild)
        val currentHeader = getHeaderViewForItem(headerPos, parent)
        fixLayoutSize(parent, currentHeader)

        val contactPoint = currentHeader.bottom
        val childInContact = getChildInContact(parent, contactPoint, headerPos)

        if (childInContact != null &&
            callback.isHeader(parent.getChildAdapterPosition(childInContact))
        ) {
            moveHeader(c, currentHeader, childInContact)
            return
        }
    }

    private fun getHeaderViewForItem(headerPos: Int, parent: RecyclerView): View {
        TODO("Implement")
    }

    private fun fixLayoutSize(parent: RecyclerView, header: View) {
        TODO("Implement")
    }

    private fun getChildInContact(parent: RecyclerView, contactPoint: Int, headerPos: Int): View? {
        TODO("Implement")
    }

    private fun moveHeader(c: Canvas, currentHeader: View, childInContact: View) {
        TODO("Implement")
    }

    private fun drawHeader(c: Canvas, currentHeader: View) {
        TODO("Implement")
    }
}
