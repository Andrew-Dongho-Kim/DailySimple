package com.dd.android.dailysimple.daily.viewmodel

import android.util.LruCache
import androidx.lifecycle.*
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.di.appDb
import com.dd.android.dailysimple.daily.CheckStatusDataSource
import com.dd.android.dailysimple.daily.Key
import com.dd.android.dailysimple.db.data.CheckStatus


private const val CACHE_SIZE = 100

private const val TAG = "CheckStatusVm"

private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class HabitCheckStatusViewModel : ViewModel() {

    private val checkStatusDao = appDb().checkStatusDao()

    private val observersMap = mutableMapOf<Long, MutableSet<LifecycleOwner>>()

    private val cacheLiveData =
        object : LruCache<Long, LiveData<List<CheckStatus>>>(CACHE_SIZE) {
            override fun entryRemoved(
                evicted: Boolean,
                key: Long,
                oldValue: LiveData<List<CheckStatus>>,
                newValue: LiveData<List<CheckStatus>>
            ) {
                observersMap[key]?.forEach {
                    oldValue.removeObservers(it)
                }
            }
        }

    private val cachePagedList =
        object : LruCache<Long, LiveData<PagedList<CheckStatus>>>(CACHE_SIZE) {
            override fun entryRemoved(
                evicted: Boolean,
                key: Long,
                oldValue: LiveData<PagedList<CheckStatus>>,
                newValue: LiveData<PagedList<CheckStatus>>
            ) {
                observersMap[key]?.forEach {
                    oldValue.removeObservers(it)
                }
            }
        }

    class PagedListObserver(
        private val habitId: Long,
        private val result: MediatorLiveData<PagedList<CheckStatus>>
    ) : Observer<List<CheckStatus>> {

        private var liveDataSource: LiveData<PagedList<CheckStatus>>? = null

        override fun onChanged(checkedList: List<CheckStatus>) {
            logD { "Check status updated! :$habitId" }

            // @formatter:off
            liveDataSource?.let { result.removeSource(it)}
            liveDataSource = LivePagedListBuilder(
                    object : DataSource.Factory<Key, CheckStatus>() {
                        override fun create() = CheckStatusDataSource(habitId, checkedList)
                    },
                    PagedList.Config.Builder()
                    .setPageSize(2)
                    .setInitialLoadSizeHint(2)
                    .build()
                )
                .build()
            // @formatter:off

            result.addSource(liveDataSource!!) { result.postValue(it) }
        }
    }

    private fun getCheckedStatus(
        habitId: Long
    ): LiveData<List<CheckStatus>> {
        return cacheLiveData[habitId]
            ?: checkStatusDao.getAllChecked(habitId).also {
                cacheLiveData.put(habitId, it)
            }
    }

    fun getCheckedStatusPagedList(
        lifecycleOwner: LifecycleOwner,
        habitId: Long
    ): LiveData<PagedList<CheckStatus>> {
        val cached = cachePagedList[habitId]
        if (cached != null) {
            return cached
        }
        val liveData = getCheckedStatus(habitId)
        var list = observersMap[habitId]
        if (list == null) {
            list = mutableSetOf()
            observersMap[habitId] = list
        }
        list.add(lifecycleOwner)

        val result = MediatorLiveData<PagedList<CheckStatus>>().also {
            cachePagedList.put(habitId, it)
        }

        liveData.observe(lifecycleOwner, PagedListObserver(habitId, result))
        return result
    }

    fun dispose() {
        cacheLiveData.evictAll()
        cachePagedList.evictAll()
    }


}