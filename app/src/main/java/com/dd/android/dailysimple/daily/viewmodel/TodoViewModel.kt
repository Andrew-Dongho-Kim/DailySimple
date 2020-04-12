package com.dd.android.dailysimple.daily.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dd.android.dailysimple.R

class TodoViewModel(app: Application) : AndroidViewModel(app) {

    val header = SimpleHeaderItemModel(0, app.getString(R.string.todo))
}