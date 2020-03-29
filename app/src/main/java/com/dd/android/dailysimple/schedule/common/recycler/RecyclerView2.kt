package com.dd.android.dailysimple.schedule.common.recycler

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

    override fun equals(other: Any?): Boolean
}

abstract class RecyclerViewAdapter2(
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<ViewHolder2>() {

    val items = mutableListOf<ItemModel>()

    abstract fun onCreateViewHolder2(parent: ViewGroup, viewType: Int): ViewHolder2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        onCreateViewHolder2(parent, viewType).apply {
            bind.lifecycleOwner = lifecycleOwner
        }

    override fun onBindViewHolder(holder: ViewHolder2, position: Int) {
        holder.bindTo(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

open class ViewHolder2(
    parent: ViewGroup,
    @LayoutRes layoutResId: Int,
    val variableId: Int = 0
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
) {

    var itemModel: ItemModel? = null

    val bind = DataBindingUtil.bind<ViewDataBinding>(itemView)!!

    var itemClickListener: View.OnClickListener? = null
        set(listener) {
            field = listener
            bind.root.setOnClickListener(field)
        }

    var itemLongClickListener: View.OnLongClickListener? = null
        set(listener) {
            field = listener
            bind.root.setOnLongClickListener(field)
        }

    @CallSuper
    open fun bindTo(itemModel: ItemModel) {
        bind.setVariable(variableId, itemModel)
        this.itemModel = itemModel
    }

    @Suppress("unchecked_cast")
    fun <T : ViewDataBinding> bindAs() = bind as T

    companion object {
        fun RecyclerView.ViewHolder.isHeader() =
            itemViewType < 0

        fun <V : View> RecyclerView.ViewHolder.findViewById(id: Int): V? =
            itemView.findViewById<V>(id)
    }
}


