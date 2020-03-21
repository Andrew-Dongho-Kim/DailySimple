package com.dd.android.dailysimple.schedule

import android.app.Application
import com.dd.android.dailysimple.schedule.common.di.ApplicationComponent
import com.dd.android.dailysimple.schedule.common.di.ApplicationModule
import com.dd.android.dailysimple.schedule.common.di.DaggerApplicationComponent

class DailySimpleApplication : Application() {

    lateinit var appComponent:ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerApplicationComponent.builder()
            .appModule(ApplicationModule(this))
            .build()
    }


}