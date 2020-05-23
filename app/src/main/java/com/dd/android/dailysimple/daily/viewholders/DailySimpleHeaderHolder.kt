package com.dd.android.dailysimple.daily.viewholders

import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.databinding.library.baseAdapters.BR
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.widget.recycler.ItemModel
import com.dd.android.dailysimple.common.widget.recycler.ViewHolder2
import com.dd.android.dailysimple.databinding.SimpleHeaderItemBinding

class DailySimpleHeaderHolder(parent: ViewGroup) :
    ViewHolder2<SimpleHeaderItemBinding, DailySimpleHeaderItem>(
        parent,
        R.layout.simple_header_item,
        BR.model
    ) {

    init {
        bind.root.outlineProvider = ViewOutlineProvider.BACKGROUND
        bind.root.clipToOutline = true
    }

    override fun bindTo(itemModel: ItemModel) {
        super.bindTo(itemModel)
        bind.executePendingBindings()
    }
}


data class DailySimpleHeaderItem(
    override val id: Long,
    val headerTitle: String
) : ItemModel