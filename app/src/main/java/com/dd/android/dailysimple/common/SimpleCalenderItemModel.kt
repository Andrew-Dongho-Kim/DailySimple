package com.dd.android.dailysimple.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SimpleCalenderItemModel {
    val year = MutableLiveData<String>()

    val month = MutableLiveData<String>()
}