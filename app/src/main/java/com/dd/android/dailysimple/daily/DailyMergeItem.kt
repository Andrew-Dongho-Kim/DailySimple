package com.dd.android.dailysimple.daily

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.widget.recycler.ItemModel

private const val TAG = "DailyMergeItem"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class DailyMergeItem(vararg liveDataArray: LiveData<out Any>) :
    MediatorLiveData<List<ItemModel>>() {

    private val models = liveDataArray
    private var waitUpdates = mutableListOf<LiveData<*>>(*liveDataArray)

    init {
        models.forEach { liveData ->
            addSource(liveData) {
                dumpWho(it)
                if (needUpdates(liveData)) {
                    value = createModel()
                }
            }
        }
    }

    private fun dumpWho(obj: Any) {
        val clazz = if (obj is List<*>) {
            if (obj.size > 0) obj[0]?.javaClass?.simpleName else obj.javaClass.simpleName
        } else {
            obj.javaClass.simpleName
        }
        logD { "updated : $clazz" }
    }

    private fun needUpdates(source: LiveData<*>): Boolean {
        waitUpdates.remove(source)
        return waitUpdates.size == 0
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
            logD { "createModel()" }
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
