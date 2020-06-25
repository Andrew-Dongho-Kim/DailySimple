package com.dd.android.dailysimple.setting

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.core.content.edit
import androidx.core.os.bundleOf
import com.dd.android.dailysimple.BuildConfig
import com.dd.android.dailysimple.common.Logger


private const val TAG = "SettingProvider"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class SettingProvider : ContentProvider() {

    private val settingPref by lazy {
        context!!.getSharedPreferences(SETTING_PREF, Context.MODE_PRIVATE)
    }


    override fun onCreate(): Boolean {
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException("insert() is not supported by $this")
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        throw UnsupportedOperationException("query() is not supported by $this")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw UnsupportedOperationException("update() is not supported by $this")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException("delete() is not supported by $this")
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        when (method) {
            Method.GET -> {
                val key = arg ?: throw IllegalStateException("get() key is null")
                val value = settingPref.getString(key, null)
                return bundleOf(Method.Arg.VALUE to value)
            }
            Method.PUT -> {
                val key = arg ?: throw IllegalStateException("put() key is null")
                val value = extras?.getString(Method.Arg.VALUE)
                settingPref.edit { putString(key, value) }
            }
            else -> throw java.lang.IllegalStateException("unknown method type:$method")
        }
        return null
    }

    override fun getType(uri: Uri): String {
        return "vnd.${AUTHORITY}.dir.setting"
    }

    object Method {
        const val PUT = "put"
        const val GET = "get"

        object Arg {
            const val VALUE = "value"
        }
    }


    companion object {

        private const val SETTING_PREF = "setting_pref"

        private const val AUTHORITY = "${BuildConfig.APPLICATION_ID}.setting"

        val CONTENT_URI = Uri.parse("content://${AUTHORITY}/setting")
    }
}