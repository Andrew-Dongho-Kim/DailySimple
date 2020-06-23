package com.dd.android.dailysimple.common.di

import com.dd.android.dailysimple.common.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
class LogModule {

    @Provides
    fun provideLogger(): Logger = Logger()
}