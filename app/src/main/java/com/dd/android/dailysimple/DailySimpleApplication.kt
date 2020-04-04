package com.dd.android.dailysimple

import android.app.Application
import com.dd.android.dailysimple.common.di.DependencyInjector

class DailySimpleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DependencyInjector.inject(this)
    }
}