package com.dd.android.dailysimple.setting

import android.content.Context

class SettingManager(private val context: Context) {

    private val providerHelper by lazy { SettingProviderHelper }

    fun putInt(key: String, value: Int) = providerHelper.putInt(context, key, value)

    fun putLong(key: String, value: Long) = providerHelper.putLong(context, key, value)

    fun putString(key: String, value: String) = providerHelper.putString(context, key, value)

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return providerHelper.getInt(context, key, defaultValue)
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return providerHelper.getLong(context, key, defaultValue)
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return providerHelper.getString(context, key, defaultValue)
    }

}