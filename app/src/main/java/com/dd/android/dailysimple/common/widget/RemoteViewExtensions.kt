package com.dd.android.dailysimple.common.widget

import android.widget.RemoteViews
import androidx.annotation.IdRes
import com.dd.android.dailysimple.R


fun RemoteViews.setImageAlpha(@IdRes id: Int, alpha: Int) = setInt(
    R.id.widget_background,
    "setImageAlpha", alpha
)