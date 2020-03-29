package com.dd.android.dailysimple.schedule.daily.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dd.android.dailysimple.schedule.common.recycler.ItemModel
import com.dd.android.dailysimple.schedule.daily.DayDateDataSource
import com.dd.android.dailysimple.schedule.daily.DayDateItemModel

class HabitHeaderViewModel(application: Application) : AndroidViewModel(application) {

    val header = liveData {
        emit(HabitHeaderItemModel(application))
    }
}

data class HabitHeaderItemModel(private val app: Application, private val length: Int = 5) :
    ItemModel {
    override val id: Long = -1L

    private val _year = MutableLiveData<String>()
    val year: LiveData<String> = _year

    private val _month = MutableLiveData<String>()
    val month: LiveData<String> = _month

    val dayDatePagedList =
        LivePagedListBuilder(
            object : DataSource.Factory<Long, DayDateItemModel>() {
                override fun create() = DayDateDataSource(app)
            }, PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(14)
                .setInitialLoadSizeHint(5)
                .build()
        ).build()


    fun refresh(firstVisiblePosition: Int) {
        dayDatePagedList.value?.let { models ->
            models[firstVisiblePosition]?.let { model ->
                _year.postValue(model.year)
                _month.postValue(model.month)
            }
        }
    }
}

