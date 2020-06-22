package com.dd.android.dailysimple

import android.content.Context

private const val SETTING_PREF_NAME = "settings"

class SettingManager(context: Context) {

    private val appPref = context.getSharedPreferences(SETTING_PREF_NAME, Context.MODE_PRIVATE)

    fun putInt(key: String, value: Int) =
        appPref.edit().putInt(key, value).apply()

    fun getInt(key: String, defaultValue: Int = 0) =
        appPref.getInt(key, defaultValue)

    fun putLong(key: String, value: Long) =
        appPref.edit().putLong(key, value).apply()

    fun getLong(key: String, defaultValue: Long = 0L) =
        appPref.getLong(key, defaultValue)

}