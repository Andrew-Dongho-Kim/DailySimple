package com.dd.android.dailysimple.common.widget.recycler

import android.view.*
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

interface ItemModel {
    val id: Long

    val selected: MutableLiveData<Boolean>
}

abstract class RecyclerViewAdapter2(
    private val lifecycleOwner: LifecycleOwner,
    private val actionMode: RecyclerViewActionMode? = null
) : RecyclerView.Adapter<ViewHolder2<out ViewDataBinding, out ItemModel>>() {

    val items = mutableListOf<ItemModel>()

    abstract fun onCreateViewHolder2(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder2<out ViewDataBinding, out ItemModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        onCreateViewHolder2(parent, viewType).apply {
            bind.lifecycleOwner = lifecycleOwner
            this@apply.actionMode = this@RecyclerViewAdapter2.actionMode
        }

    override fun onBindViewHolder(
        holder: ViewHolder2<out ViewDataBinding, out ItemModel>,
        position: Int
    ) {
        holder.bindTo(items[position])
    }

    override fun getItemId(position: Int): Long {
        return items[position].id
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(list: List<ItemModel>) {
        val diffResult = DiffUtil.calculateDiff(ItemModelDiffCallback(items, list))

        items.clear()
        items.addAll(list)

        diffResult.dispatchUpdatesTo(this)
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

    val bind: B = DataBindingUtil.bind<B>(itemView)!!.apply {
        root.setOnClickListener { view ->
            // Header item is not related to action mode item.
            // This is constraints for this class
            when {
                isHeader() -> itemClickListener?.invoke(view)
                isActionMode() -> actionMode?.selectItem(model)
                else -> itemClickListener?.invoke(view)
            }
        }
    }

    var actionMode: RecyclerViewActionMode? = null
        set(value) {
            field = value
            field?.let { callback ->
                itemLongClickListener = View.OnLongClickListener {
                    callback.startActionMode(model)
                    return@OnLongClickListener true
                }
            }
        }

    var itemClickListener: ((View) -> Unit)? = null

    var itemLongClickListener: View.OnLongClickListener? = null
        set(listener) {
            field = listener
            bind.root.setOnLongClickListener(field)
        }

    fun isActionMode() = actionMode?.isActionMode == true

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

class ItemModelDiffCallback(
    private val oldList: List<ItemModel>,
    private val newList: List<ItemModel>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }
}

class RecyclerViewActionMode(
    private val activity: AppCompatActivity,
    private val recyclerView: RecyclerView,
    private val callback: Callback
) : ActionMode(), ActionMode.Callback {

    private val selected = mutableSetOf<ItemModel>()
    private var actionMode: ActionMode? = null

    val isActionMode get() = actionMode != null

    fun startActionMode(item: ItemModel?) {
        selected.clear()
        selectItem(item)

        activity.startSupportActionMode(this)
    }

    fun selectItem(item: ItemModel?) {
        item ?: return

        if (selected.contains(item)) {
            selected.remove(item)
            item.selected.postValue(false)
        } else {
            selected.add(item)
            item.selected.postValue(true)
        }
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        actionMode = mode
        return callback.onCreateActionMode(this, menu, selected)
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        return callback.onPrepareActionMode(this, menu, selected)
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        return callback.onActionItemClicked(this, item, selected)
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        actionMode = null
        selected.clear()
        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun getMenu() = actionMode?.menu

    override fun getMenuInflater() = actionMode?.menuInflater

    override fun getCustomView() = actionMode?.customView

    override fun setCustomView(view: View) {
        actionMode?.customView = view
    }

    override fun getTitle() = actionMode?.title

    override fun setTitle(title: CharSequence?) {
        actionMode?.title = title
    }

    override fun setTitle(resId: Int) {
        actionMode?.setTitle(resId)
    }

    override fun getSubtitle() = actionMode?.subtitle

    override fun setSubtitle(subtitle: CharSequence?) {
        actionMode?.subtitle = subtitle
    }

    override fun setSubtitle(resId: Int) {
        actionMode?.setSubtitle(resId)
    }

    override fun invalidate() {
        actionMode?.invalidate()
    }

    override fun finish() {
        actionMode?.finish()
    }

    interface Callback {
        fun onCreateActionMode(
            mode: RecyclerViewActionMode,
            menu: Menu,
            selected: Set<ItemModel>
        ): Boolean

        fun onPrepareActionMode(
            mode: RecyclerViewActionMode,
            menu: Menu,
            selected: Set<ItemModel>
        ): Boolean

        fun onActionItemClicked(
            mode: RecyclerViewActionMode,
            item: MenuItem,
            selected: Set<ItemModel>
        ): Boolean
    }
}