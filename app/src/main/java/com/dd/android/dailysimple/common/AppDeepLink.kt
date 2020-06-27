package com.dd.android.dailysimple.common

import android.content.Intent
import android.net.Uri
import com.dd.android.dailysimple.BuildConfig.APPLICATION_ID
import com.dd.android.dailysimple.common.utils.DateUtils.msDateFrom
import com.dd.android.dailysimple.daily.AppConst
import com.dd.android.dailysimple.daily.edit.EditType

object AppDeepLink {

    private const val SCHEME = "dsimp"

    /**
     * @see com.dd.android.dailysimple.R.id.make_and_edit_fragment
     */
    private const val MAKE_AND_EDIT_URI = "make_and_edit"
    fun intentToEdit(@EditType type: Int, id: Long = AppConst.NO_ID) =
        Intent(
            Intent.ACTION_VIEW,
            Uri.Builder().scheme(SCHEME)
                .authority(MAKE_AND_EDIT_URI)
                .appendPath(type.toString())
                .appendPath(id.toString())
                .build()
        ).apply {
            `package` = APPLICATION_ID
        }

    /**
     * @see com.dd.android.dailysimple.R.id.daily_fragment
     */
    private const val DAILY_URI = "daily"
    fun intentToDaily(date: Long = msDateFrom()) =
        Intent(
            Intent.ACTION_VIEW,
            Uri.Builder().scheme(SCHEME)
                .authority(DAILY_URI)
                .appendPath(date.toString())
                .build()
        ).apply {
            `package` = APPLICATION_ID
        }

}