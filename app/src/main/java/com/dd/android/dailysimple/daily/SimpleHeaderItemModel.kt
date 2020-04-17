package com.dd.android.dailysimple.daily

import com.dd.android.dailysimple.common.recycler.ItemModel

data class SimpleHeaderItemModel(
    override val id: Long,
    val headerTitle: String
) : ItemModel