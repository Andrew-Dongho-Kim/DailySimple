package com.dd.android.dailysimple.setting

import android.content.Context
import androidx.core.os.bundleOf
import com.dd.android.dailysimple.setting.SettingProvider.Companion.CONTENT_URI
import com.dd.android.dailysimple.setting.SettingProvider.Method
import com.dd.android.dailysimple.setting.SettingProvider.Method.Arg

object SettingProviderHelper {

    private fun get(context: Context, key: String): String? =
        context.contentResolver.call(CONTENT_URI, Method.GET, key, null)?.getString(Arg.VALUE)

    private fun put(context: Context, key: String, value: String) =
        context.contentResolver.call(CONTENT_URI, Method.PUT, key, bundleOf(Arg.VALUE to value))

    fun getInt(context: Context, key: String, defaultValue: Int = 0) =
        get(context, key)?.toInt() ?: defaultValue

    fun getLong(context: Context, key: String, defaultValue: Long = 0L) =
        get(context, key)?.toLong() ?: defaultValue

    fun getString(context: Context, key: String, defaultValue: String? = null) =
        get(context, key) ?: defaultValue

    fun putInt(context: Context, key: String, value: Int) =
        put(context, key, value.toString())

    fun putLong(context: Context, key: String, value: Long) =
        put(context, key, value.toString())

    fun putString(context: Context, key: String, value: String) =
        put(context, key, value)

}