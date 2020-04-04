package com.dd.android.dailysimple.common.di

import com.dd.android.dailysimple.common.Logger
import dagger.Module
import dagger.Provides

@Module
class LogModule {

    @Provides
    fun provideLogger(): Logger = Logger()
}