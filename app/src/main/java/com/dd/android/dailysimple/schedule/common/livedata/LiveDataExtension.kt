package com.dd.android.dailysimple.schedule.common.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.PagedList

//fun <T> LiveData<T>.merge(
//    liveData: LiveData<T>
//): LiveData<List<T>> {
//    val result = MediatorLiveData<List<T>>()
//    result.addSource(this) {
//        result.value = listOf(this.value, liveData.value)
//    }
//    result.addSource(liveData) {
//        result.value = listOf(this.value, liveData.value)
//    }
//    return result
//}

fun <T> LiveData<PagedList<T>>.merge(
    liveData: LiveData<T>
): LiveData<PagedList<T>> {
    val result = MediatorLiveData<PagedList<T>>()

    result.addSource(this) { list->
        liveData.value?.let { list.add(it) }
        result.value = list
    }
    result.addSource(liveData) {
        this.value!!.add(it)
        result.value = this.value
    }
    return result
}


private fun <T> listOf(vararg items: T?): List<T> =
    mutableListOf<T>().apply {
        items.forEach { it?.let { add(it) } }
    }