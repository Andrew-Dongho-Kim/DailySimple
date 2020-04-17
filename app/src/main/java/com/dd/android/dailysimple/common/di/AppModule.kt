package com.dd.android.dailysimple.common.di

import android.app.Application
import android.content.Context
import android.os.Build
import com.dd.android.dailysimple.db.AppDatabase
import dagger.Module
import dagger.Provides
import java.util.*
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

    @Provides
    @Suppress("deprecation")
    fun provideLocale(): Locale {
        return if (Build.VERSION_CODES.N <= Build.VERSION.SDK_INT) {
            app.resources.configuration.locales[0]
        } else {
            app.resources.configuration.locale
        }
    }

}