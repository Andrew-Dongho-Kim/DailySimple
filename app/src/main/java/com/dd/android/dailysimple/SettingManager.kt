package com.dd.android.dailysimple

import android.content.Context

private val SETTING_PREF_NAME = "settings"

class SettingManager(context: Context) {

    private val appPref = context.getSharedPreferences(SETTING_PREF_NAME, Context.MODE_PRIVATE)


    fun putInt(key: String, value: Int) {
        appPref.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int) =
        appPref.getInt(key, defaultValue)

}