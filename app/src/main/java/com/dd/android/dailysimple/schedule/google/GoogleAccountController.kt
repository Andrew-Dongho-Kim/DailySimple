package com.dd.android.dailysimple.schedule.google

import android.content.Intent
import android.util.Log
import androidx.lifecycle.*
import com.dd.android.dailysimple.schedule.common.BaseActivity
import com.dd.android.dailysimple.schedule.common.OnActivityResultListener
import com.dd.android.dailysimple.schedule.google.SignedState.SIGNED_IN
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.api.GoogleApiClient


private const val REQUEST_GOOGLE_SIGN_IN = 1005
private const val LOG_TAG = "GoogleAccount"

enum class SignedState {
    SIGNED_IN,
    SIGNED_OUT,
    SIGNED_FAIL
}

data class SignInOutResult(
    val state: SignedState,
    val result: GoogleSignInResult? = null
) {
    companion object {
        val SIGNED_OUT = SignInOutResult(SignedState.SIGNED_OUT)
        val SIGNED_FAIL = SignInOutResult(SignedState.SIGNED_FAIL)
    }
}

// http://www.androidhive.info/2014/02/android-login-with-google-plus-account-1/
class GoogleAccountController(
    private val activity: BaseActivity,
    useSilentSignIn: Boolean = true
) : LifecycleObserver {

    private val signInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
    }

    private val apiClient: GoogleApiClient by lazy {
        GoogleApiClient.Builder(activity)
            .enableAutoManage(activity) {
                // Connection failed
                _signInOutResult.postValue(SignInOutResult.SIGNED_FAIL)
            }
            .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
            .build()
    }

    private val onActivityResult = object : OnActivityResultListener {
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (requestCode == REQUEST_GOOGLE_SIGN_IN) {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)

                _signInOutResult.postValue(
                    if (result.isSuccess) {
                        SignInOutResult(
                            SIGNED_IN,
                            result
                        )
                    } else {
                        SignInOutResult.SIGNED_FAIL
                    }
                )

            }
        }
    }

    private val _signInOutResult = MutableLiveData<SignInOutResult>()
    val signInOutResult: LiveData<SignInOutResult> = _signInOutResult

    init {
        if (useSilentSignIn) activity.lifecycle.addObserver(this)
        else activity.lifecycle.removeObserver(this)

        activity.addOnActivityResultListener(onActivityResult)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onStart() {
        val pendingResult = Auth.GoogleSignInApi.silentSignIn(apiClient)
        if (pendingResult.isDone) {
            Log.d(LOG_TAG, "Silent Sign In to Google : Success")
            _signInOutResult.postValue(SignInOutResult(SIGNED_IN, pendingResult.get()))
        } else {
            pendingResult.setResultCallback { result ->
                _signInOutResult.postValue(SignInOutResult(SIGNED_IN, result))
                Log.d(
                    LOG_TAG,
                    "Silent Sign In to Google Success : ${result.isSuccess}, ${result.status}"
                )
            }
        }
    }

    fun signInToGoogle() =
        activity.startActivityForResult(
            Auth.GoogleSignInApi.getSignInIntent(apiClient),
            REQUEST_GOOGLE_SIGN_IN
        )

    fun signOutToGoogle() =
        Auth.GoogleSignInApi.signOut(apiClient).setResultCallback { status ->
            // update sign out status
            _signInOutResult.postValue(SignInOutResult.SIGNED_OUT)
        }
}