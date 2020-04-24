package com.dd.android.dailysimple.daily

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.recycler.ItemModel


private const val TAG = "DailyMergeItem"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class DailyMergeItem(vararg liveDataArray: LiveData<out Any>) :
    MediatorLiveData<List<ItemModel>>() {

    private val models = liveDataArray

    init {
        models.forEach { liveData ->
            addSource(liveData) {
                if (ensureVm()) value = createModel()
            }
        }
    }

    private fun ensureVm(): Boolean {
        models.forEach { liveData ->
            if (liveData.value == null) return false
        }
        return true
    }

    private fun createModel(): List<ItemModel> =
        mutableListOf<ItemModel>().apply {
            models.forEach { liveData ->
                val model = liveData.value!!

                if (model is List<*>) {
                    model.forEach {
                        addItemModel(this, it)
                    }
                } else {
                    addItemModel(this, model)
                }
            }
            logD { "Daily - createModel()" }
        }

    private fun addItemModel(itemList: MutableList<ItemModel>, item: Any?) {
        if (item is DailyExpandableItem) {
            if (item.children.isNotEmpty())
                itemList.add(item)
        } else if (item is ItemModel) {
            itemList.add(item)
        }
    }
}
