package com.dd.android.dailysimple.common

import android.content.Intent
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity

private const val TAG = "BaseActivity"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logD { "$this - onCreate() : $savedInstanceState" }
    }

    override fun onStart() {
        super.onStart()
        logD { "$this - onStart()" }
    }

    override fun onResume() {
        super.onResume()
        logD { "$this - onResume()" }
    }

    override fun onPause() {
        super.onPause()
        logD { "$this - onPause()" }
    }

    override fun onStop() {
        super.onStop()
        logD { "$this - onStop()" }
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        logD { "$this - onActivityResult() requestCode:$requestCode, resultCode:$resultCode, intent:$intent" }
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