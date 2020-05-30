package com.dd.android.dailysimple

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.dd.android.dailysimple.common.BaseActivity
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.google.GoogleAccountController
import com.dd.android.dailysimple.google.GoogleAccountViewModel
import com.dd.android.dailysimple.maker.FabViewModel

private const val TAG = "HomeActivity"

private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class HomeActivity : BaseActivity() {

    private val accountViewModel by viewModels<GoogleAccountViewModel>()

    private val fabViewModel by viewModels<FabViewModel>()

    private val accountController = GoogleAccountController(this)

    private var isFabOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        accountController.signInOutResult.observe(this,
            Observer {
                it.result?.let { account ->
                    accountViewModel.update(account)
                    logD { "Account profile: ${account.photoUrl}" }
                }
            })

        fabViewModel.isOpen.observe(this, Observer { opened ->
            isFabOpen = opened
        })
    }

    override fun onBackPressed() {
        if (isFabOpen) {
            fabViewModel.toggle()
            return
        }
        super.onBackPressed()
    }

}