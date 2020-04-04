package com.dd.android.dailysimple.common.di

import android.app.Application
import android.content.Context
import com.dd.android.dailysimple.db.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {

    @Singleton
    @Provides
    fun provideAppContext(): Context {
        return app
    }

    @Singleton
    @Provides
    @Inject
    fun provideAppDatabase(context: Context) = AppDatabase.getInstance(context)
}