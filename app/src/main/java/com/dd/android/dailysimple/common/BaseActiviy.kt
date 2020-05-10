package com.dd.android.dailysimple.common

import android.content.Intent
import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity

interface OnActivityResultListener {
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}

interface OnActivityResultOwner {
    fun addOnActivityResultListener(listener: OnActivityResultListener): Boolean

    fun removeOnActivityResultListener(listener: OnActivityResultListener): Boolean
}

interface OnRequestPermissionResultListener {
    fun onRequestPermissionResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
}

interface OnRequestPermissionResultOwner {
    fun addOnRequestPermissionResultListener(listener: OnRequestPermissionResultListener): Boolean

    fun removeOnRequestPermissionResultListener(listener: OnRequestPermissionResultListener): Boolean
}

abstract class BaseActivity : AppCompatActivity(),
    OnActivityResultOwner,
    OnRequestPermissionResultOwner {

    private val onActivityResultListeners = hashSetOf<OnActivityResultListener>()

    private val onRequestPermissionResultListeners = hashSetOf<OnRequestPermissionResultListener>()

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
        onRequestPermissionResultListeners.forEach { listener ->
            listener.onRequestPermissionResult(requestCode, permissions, grantResults)
        }
    }

    override fun addOnRequestPermissionResultListener(listener: OnRequestPermissionResultListener) =
        onRequestPermissionResultListeners.add(listener)

    override fun removeOnRequestPermissionResultListener(listener: OnRequestPermissionResultListener) =
        onRequestPermissionResultListeners.remove(listener)

}