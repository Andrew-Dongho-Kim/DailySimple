package com.dd.android.dailysimple.schedule.common.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Module
class ApplicationModule(private val app: Application) {
//
//    @Singleton
//    @Provides
//    fun provideApp() :

    @Singleton
    @Provides
    fun provideAppContext(): Context {
        return app
    }

//    @Provides
//    @Named("DefaultPreferences")
//    fun provideDefaultSharedPreferences(): SharedPreferences {
//        return PreferenceManager.getDefaultSharedPreferences(app)
//    }


    @Provides
    @Inject
    fun provideShared(context:Context) :SharedPreferences {
        return context.getSharedPreferences("DD", Context.MODE_PRIVATE)
    }


}