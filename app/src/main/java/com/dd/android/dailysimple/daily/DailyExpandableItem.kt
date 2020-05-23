package com.dd.android.dailysimple.daily

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.dd.android.dailysimple.common.widget.recycler.ItemModel

abstract class DailyExpandableItem(
    val children: List<ItemModel>,
    val isExpanded: MutableLiveData<Boolean>
) : ItemModel {

    private val _listLiveData = Transformations.map(isExpanded) { isExpanded ->
        if (isExpanded) children else emptyList()
    }

    val listLiveData: LiveData<List<ItemModel>> = _listLiveData
}