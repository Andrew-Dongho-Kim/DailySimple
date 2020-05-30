package com.dd.android.dailysimple.daily.simplecalendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dd.android.dailysimple.common.utils.DateUtils.month
import com.dd.android.dailysimple.common.utils.DateUtils.msDateOnlyFrom
import com.dd.android.dailysimple.common.utils.DateUtils.year
import com.dd.android.dailysimple.daily.DayDateDataSource
import com.dd.android.dailysimple.daily.DayDateItemModel

class SimpleCalendarViewModel(application: Application) : AndroidViewModel(application) {

    val selectedDate = liveData {
        emit(msDateOnlyFrom())
    } as MutableLiveData<Long>

    val selectedDateDistinct = Transformations.distinctUntilChanged(selectedDate)

    val year = Transformations.map(selectedDate) {
        year(it)
    }

    val month = Transformations.map(selectedDate) {
        month(it)
    }

    val calendar =
        LivePagedListBuilder(
            object : DataSource.Factory<Long, DayDateItemModel>() {
                override fun create() = DayDateDataSource(application)
            }, PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(14)
                .setInitialLoadSizeHint(5)
                .build()
        ).build()

}
