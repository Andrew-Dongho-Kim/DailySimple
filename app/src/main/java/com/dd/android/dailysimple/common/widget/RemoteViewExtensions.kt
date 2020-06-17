package com.dd.android.dailysimple.common.widget

import android.widget.RemoteViews
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.dd.android.dailysimple.common.di.appContext


fun RemoteViews.setImageViewImageAlpha(@IdRes id: Int, alpha: Int) =
    setInt(id, "setImageAlpha", alpha)

fun RemoteViews.setViewBackground(@IdRes id: Int, @ColorRes colorResId: Int) =
    setInt(id, "setBackgroundColor", colorResId)


fun RemoteViews(@LayoutRes layoutResId: Int) = RemoteViews(appContext.packageName, layoutResId)