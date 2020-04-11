package com.dd.android.dailysimple.common.di

import android.app.Application
import android.content.Context
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.db.AppDatabase
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

open class AppDependencyInjector {

    // @formatter:off
    @Inject lateinit var providerLocale : Provider<Locale>
    
    @Inject lateinit var logger: Logger
    @Inject lateinit var appContext:Context
    @Inject lateinit var appDb: AppDatabase

    // @formatter:on

    fun provideLocale(): Locale = providerLocale.get()

    fun inject(app: Application) {
        DaggerApplicationComponent.builder()
            .moduleApp(AppModule(app))
            .moduleLogger(LogModule())
            .build()
            .inject(this)
    }
}

object DependencyInjector : AppDependencyInjector()


fun appContext(): Context = DependencyInjector.appContext

fun systemLocale(): Locale = DependencyInjector.provideLocale()

fun appDb(): AppDatabase = DependencyInjector.appDb