package com.dd.android.dailysimple.common.livedata

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.dd.android.dailysimple.common.Logger


private const val TAG = "ContentProviderLiveData"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class ContentProviderLiveData<T>(
    private val context: Context,
    private val uri: Uri,
    private val queryCallback: () -> T
) : MutableLiveData<T>() {

    private val contentObserver by lazy {
        object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                queryCallback().run {
                    logD { "onChange(self:$selfChange) : $this, " }
                    postValue(this)
                }
            }
        }
    }

    override fun onActive() {
        context.contentResolver.registerContentObserver(uri, true, contentObserver)
        logD { "onActive" }
    }

    override fun onInactive() {
        context.contentResolver.unregisterContentObserver(contentObserver)
        logD { "onInActive" }
    }
}