package com.dd.android.dailysimple.common.di

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.dd.android.dailysimple.DailySimpleApplication
import com.dd.android.dailysimple.SettingManager
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.db.AppDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

open class AppDependency @Inject constructor(
    @ApplicationContext val appContext: Context,
    val logger: Logger,
    val appDb: AppDatabase,
    val locale: Locale,
    val settingManager: SettingManager
)


object DependencyInjector {
    lateinit var application: DailySimpleApplication

    val appDependency by lazy { application.appDependency }
}

fun appContext() = DependencyInjector.appDependency.appContext

fun appResources() = appContext().resources

fun appPackageName() = appContext().packageName

fun getString(@StringRes strResId: Int) = appContext().getString(strResId)

fun getString(@StringRes strResId: Int, vararg args: Any) = appContext().getString(strResId, *args)

fun getColor(@ColorRes colorResId: Int) = ContextCompat.getColor(appContext(), colorResId)

fun systemLocale(): Locale = DependencyInjector.appDependency.locale

fun appDb(): AppDatabase = DependencyInjector.appDependency.appDb

fun settingManager() = DependencyInjector.appDependency.settingManager

