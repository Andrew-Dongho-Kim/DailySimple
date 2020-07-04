package com.dd.android.dailysimple.common.extensions

import android.widget.RemoteViews
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.dd.android.dailysimple.common.di.appPackageName


fun RemoteViews.setImageViewImageAlpha(@IdRes id: Int, alpha: Int) =
    setInt(id, "setImageAlpha", alpha)

fun RemoteViews.setViewBackground(@IdRes id: Int, @ColorRes colorResId: Int) =
    setInt(id, "setBackgroundColor", colorResId)

fun createRemoteViews(@LayoutRes layoutResId: Int) = RemoteViews(appPackageName(), layoutResId)