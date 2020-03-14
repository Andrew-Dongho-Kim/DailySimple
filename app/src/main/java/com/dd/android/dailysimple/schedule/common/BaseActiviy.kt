package com.dd.android.dailysimple.schedule.common

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

interface OnActivityResultListener {
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}

interface OnActivityResultOwner {
    fun addOnActivityResultListener(listener: OnActivityResultListener): Boolean

    fun removeOnActivityResultListener(listener: OnActivityResultListener): Boolean
}

abstract class BaseActivity : AppCompatActivity(), OnActivityResultOwner {
    private val onActivityResultListeners = hashSetOf<OnActivityResultListener>()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onActivityResultListeners.forEach { listener ->
            listener.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun addOnActivityResultListener(listener: OnActivityResultListener) =
        onActivityResultListeners.add(listener)

    override fun removeOnActivityResultListener(listener: OnActivityResultListener) =
        onActivityResultListeners.remove(listener)


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}