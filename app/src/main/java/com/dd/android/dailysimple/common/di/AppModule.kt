package com.dd.android.dailysimple.common.di

import android.content.Context
import android.os.Build
import com.dd.android.dailysimple.SettingManager
import com.dd.android.dailysimple.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Singleton

/**
 * In cases where you need Hilt to provide different implementation of the same type
 * as dependencies, you must provide Hilt with multiple bindings.
 * You can define multiple bindings for the same type with qualifiers.
 *
 * A qualifier is an annotation that you use to identify a specific binding for a type
 * when that type has multiple bindings defined.
 *
 * <code>
 * @Qualifier
 * @Retention(AnnotationRetention.BINARY)
 * annotation class AuthInterceptorOkHttpClient
 *
 * @Qualifier
 * @Retention(AnnotationRetention.BINARY)
 * annotation class OtherInterceptorOkHttpClient
 *
 * </code>
 */
@Module
@InstallIn(ApplicationComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) = AppDatabase.getInstance(context)

    @Singleton
    @Provides
    @Suppress("deprecation")
    fun provideLocale(@ApplicationContext context: Context): Locale {
        return if (Build.VERSION_CODES.N <= Build.VERSION.SDK_INT) {
            context.resources.configuration.locales[0]
        } else {
            context.resources.configuration.locale
        }
    }

    @Singleton
    @Provides
    fun provideSettingManager(@ApplicationContext context: Context) = SettingManager(context)
}