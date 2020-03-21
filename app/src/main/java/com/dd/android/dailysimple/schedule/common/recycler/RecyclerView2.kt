package com.dd.android.dailysimple.schedule.common.recycler

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class RecyclerView2(
    context: Context, attrs: AttributeSet, defStyleAttr: Int
) : RecyclerView(context, attrs, defStyleAttr) {


}

abstract class RecyclerViewAdapter2(
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<ViewHolder2>() {
//    PagedListAdapter<RecyclerViewItemModel, ViewHolder2>(diffCallback) {

    val items = mutableListOf<ItemModel>()

    abstract fun onCreateViewHolder2(parent: ViewGroup, viewType: Int): ViewHolder2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        onCreateViewHolder2(parent, viewType).apply {
            bind.lifecycleOwner = lifecycleOwner
        }

    override fun onBindViewHolder(holder: ViewHolder2, position: Int) {
        holder.bindVariable(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    companion object {
        /**
         * This diff callback informs the PagedListAdapter how to compute list differences when new
         * PagedLists arrive.
         * <p>
         * When you add a Cheese with the 'Add' button, the PagedListAdapter uses diffCallback to
         * detect there's only a single item difference from before, so it only needs to animate and
         * rebind a single view.
         *
         * @see DiffUtil
         */
        private val diffCallback = object : DiffUtil.ItemCallback<ItemModel>() {
            override fun areItemsTheSame(
                oldItem: ItemModel,
                newItem: ItemModel
            ): Boolean =
                oldItem::class == newItem::class && oldItem.id == newItem.id

            /**
             * Note that in kotlin, == checking on data classes compares all contents, but in Java,
             * typically you'll implement Object#equals, and use it to compare object contents.
             */
            override fun areContentsTheSame(
                oldItem: ItemModel,
                newItem: ItemModel
            ): Boolean =
                oldItem == newItem
        }
    }
}

interface ItemModel {
    val id: Long

    override fun equals(other: Any?): Boolean
}

open class ViewHolder2(
    parent: ViewGroup,
    @LayoutRes layoutResId: Int,
    val variableId: Int = 0
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
) {

    val bind = DataBindingUtil.bind<ViewDataBinding>(itemView)!!

    var itemClickListener: View.OnClickListener? = null
        set(listener) {
            field = listener
            field?.let { bind.root.setOnClickListener(it) }
        }

    fun bindVariable(item: ItemModel) = bind.setVariable(variableId, item)
}


