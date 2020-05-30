package com.dd.android.dailysimple.daily

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.dd.android.dailysimple.common.widget.recycler.ItemModel

interface ChildItemModel : ItemModel {
    var hasParent: Boolean
}

abstract class DailyExpandableItem(
    val children: List<ChildItemModel>,
    val isExpanded: MutableLiveData<Boolean>
) : ItemModel {

    init {
        children.forEach { it.hasParent = true }
    }

    private val _listLiveData = Transformations.map(isExpanded) { isExpanded ->
        if (isExpanded) children else emptyList()
    }

    val listLiveData: LiveData<List<ChildItemModel>> = _listLiveData
}