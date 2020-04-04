package com.dd.android.dailysimple.google

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.dd.android.dailysimple.R
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class GoogleAccountViewModel(application: Application) : AndroidViewModel(application) {

    private val _name = MutableLiveData<String>()
    val name = liveData<String> {
        emit(application.getString(R.string.guest))
        emitSource(_name)
    }

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _thumbnailUrl = MutableLiveData<String>()
    val thumbnailUrl: LiveData<String> = _thumbnailUrl

    fun update(signInAccount: GoogleSignInAccount) {
        with(signInAccount) {
            _name.postValue(displayName)
            _email.postValue(email)
            _thumbnailUrl.postValue(photoUrl.toString())
        }
    }
}