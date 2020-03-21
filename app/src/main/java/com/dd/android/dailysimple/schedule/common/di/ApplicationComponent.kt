package com.dd.android.dailysimple.schedule.common.di

import android.app.Application
import com.dd.android.dailysimple.schedule.HomeActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun inject(application:Application)

    fun inject(activity: HomeActivity)

    @Component.Builder
    interface Builder {

        fun appModule(appModule: ApplicationModule): Builder

        fun build(): ApplicationComponent
    }
}