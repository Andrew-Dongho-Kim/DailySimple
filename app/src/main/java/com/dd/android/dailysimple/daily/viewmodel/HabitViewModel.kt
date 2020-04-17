package com.dd.android.dailysimple.daily.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.dd.android.dailysimple.common.di.appDb
import com.dd.android.dailysimple.common.recycler.ItemModel
import com.dd.android.dailysimple.daily.DayDateDataSource
import com.dd.android.dailysimple.daily.DayDateItemModel
import com.dd.android.dailysimple.db.DailyHabitRepository
import com.dd.android.dailysimple.db.data.DailyHabit
import kotlinx.coroutines.launch

class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository by lazy {
        val db = appDb()
        DailyHabitRepository(
            db.dailyScheduleDao(),
            db.checkStatusDao()
        )
    }

    val header = liveData {
        emit(HabitHeaderItemModel(application))
    }

    val allHabits = repository.allHabits

    fun getHabit(habitId: Long) = repository.getHabit(habitId)

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on
     * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called
     * viewModelScope which we can use here.
     */
    fun insert(habit: DailyHabit) = viewModelScope.launch {
        repository.insert(habit)
    }

    fun delete(habitId: Long) = viewModelScope.launch {
        repository.delete(habitId)
    }
}

data class HabitHeaderItemModel(private val app: Application) : ItemModel {
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


