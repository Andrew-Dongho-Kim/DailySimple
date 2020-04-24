package com.dd.android.dailysimple.google

import android.content.Intent
import androidx.lifecycle.*
import com.dd.android.dailysimple.common.BaseActivity
import com.dd.android.dailysimple.common.OnActivityResultListener
import com.dd.android.dailysimple.google.SignedState.SIGNED_IN
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task


private const val RC_SIGN_IN = 1
private const val LOG_TAG = "GoogleAccount"

private const val SIGN_IN_REQUIRED = 4

enum class SignedState {
    SIGNED_IN,
    SIGNED_OUT,
    SIGNED_FAIL
}

data class SignInOutResult(
    val state: SignedState,
    val result: GoogleSignInAccount? = null
) {
    companion object {
        val SIGNED_OUT = SignInOutResult(SignedState.SIGNED_OUT)
        val SIGNED_FAIL = SignInOutResult(SignedState.SIGNED_FAIL)
    }
}

// https://developers.google.com/identity/sign-in/android/sign-in?hl=ko
// https://firebase.google.com/docs/auth/android/google-signin?hl=ko
class GoogleAccountController(
    private val activity: BaseActivity,
    useSilentSignIn: Boolean = true
) : LifecycleObserver {

    private val signInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestProfile()
            .requestEmail()
            .build()
    }

    private val signInClient by lazy {
        GoogleSignIn.getClient(activity, signInOptions)
    }

    private val onActivityResult = object : OnActivityResultListener {
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (requestCode == RC_SIGN_IN) {
                handleResult(GoogleSignIn.getSignedInAccountFromIntent(data))
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

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        handleResult(if (task.isSuccessful) task.result else null)
    }

    private fun handleResult(account: GoogleSignInAccount?) {
        _signInOutResult.postValue(
            if (account == null) {
                SignInOutResult.SIGNED_FAIL
            } else {
                SignInOutResult(SIGNED_IN, account)
            }
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onStart() {
        val account = GoogleSignIn.getLastSignedInAccount(activity)
        if (account == null) {
            signInToGoogle()
        } else {
            handleResult(account)
        }
    }

    fun signInToGoogle() =
        activity.startActivityForResult(signInClient.signInIntent, RC_SIGN_IN)

    fun signOutToGoogle() =
        signInClient.signOut().also {
            _signInOutResult.postValue(SignInOutResult.SIGNED_OUT)
        }
}