package com.dd.android.dailysimple.common.di

import android.app.Application
import android.content.Context
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.db.AppDatabase
import javax.inject.Inject

open class AppDependencyInjector {

    // @formatter:off
    @Inject lateinit var logger: Logger
    @Inject lateinit var appContext:Context
    @Inject lateinit var appDb: AppDatabase

    // @formatter:on

    fun provideAppContext(): Context = appContext

    fun provideAppDb(): AppDatabase = appDb

    fun inject(app: Application) {
        DaggerApplicationComponent.builder()
            .moduleApp(AppModule(app))
            .moduleLogger(LogModule())
            .build()
            .inject(this)
    }
}

object DependencyInjector : AppDependencyInjector()


fun appContext() = DependencyInjector.provideAppContext()

fun appDb() = DependencyInjector.provideAppDb()