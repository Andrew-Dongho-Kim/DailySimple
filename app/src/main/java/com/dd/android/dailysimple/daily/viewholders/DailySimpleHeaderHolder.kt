package com.dd.android.dailysimple.daily.viewholders

import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.recycler.ItemModel
import com.dd.android.dailysimple.common.recycler.ViewHolder2
import com.dd.android.dailysimple.databinding.SimpleHeaderItemBinding

class DailySimpleHeaderHolder(parent: ViewGroup) :
    ViewHolder2<SimpleHeaderItemBinding, DailySimpleHeaderItem>(
        parent,
        R.layout.simple_header_item,
        BR.model
    )


data class DailySimpleHeaderItem(
    override val id: Long,
    val headerTitle: String
) : ItemModel