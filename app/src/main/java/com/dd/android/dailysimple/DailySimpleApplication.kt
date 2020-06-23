package com.dd.android.dailysimple

import android.app.Application
import com.dd.android.dailysimple.common.di.AppDependency
import com.dd.android.dailysimple.common.di.DependencyInjector
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Hilt only supports activities that extends ComponentActivity, such as AppCompatActivity
 * Hilt only supports fragments that extends androidx.Fragment
 * Hilt does not support retained fragments
 */
@HiltAndroidApp
class DailySimpleApplication : Application() {
    @Inject
    lateinit var appDependency: AppDependency

    override fun onCreate() {
        super.onCreate()
        DependencyInjector.application = this
    }
}