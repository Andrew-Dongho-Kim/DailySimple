package com.dd.android.dailysimple.common

import android.util.Log
import com.dd.android.dailysimple.common.di.DependencyInjector

@PublishedApi
internal const val APP_TAG = "DailySimple"

class Logger {

    inline fun logD(tag: String, traceStack: Boolean, crossinline message: () -> String) {
        val parentStack = if (traceStack) Thread.currentThread().stackTrace[5] else ""
        Log.d("[$APP_TAG] $tag", "${message()} | $parentStack")
    }

    inline fun logI(tag: String, crossinline message: () -> String) =
        Log.i("[$APP_TAG] $tag", message())

    inline fun logW(tag: String, crossinline message: () -> String) =
        Log.w("[$APP_TAG] $tag", message())

    inline fun logE(tag: String, crossinline message: () -> String) =
        Log.e("[$APP_TAG] $tag", message())


    companion object {
        inline fun d(tag: String, crossinline message: () -> String) =
            DependencyInjector.appDependency.logger.logD(tag, false, message)

        inline fun d(tag: String, traceStack: Boolean, crossinline message: () -> String) =
            DependencyInjector.appDependency.logger.logD(tag, traceStack, message)

        inline fun i(tag: String, crossinline message: () -> String) =
            DependencyInjector.appDependency.logger.logI(tag, message)

        inline fun w(tag: String, crossinline message: () -> String) =
            DependencyInjector.appDependency.logger.logW(tag, message)

        inline fun e(tag: String, crossinline message: () -> String) =
            DependencyInjector.appDependency.logger.logE(tag, message)
    }
}