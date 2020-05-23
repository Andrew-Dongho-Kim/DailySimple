package com.dd.android.dailysimple.common.utils


fun htmlTextColor(text: String, color: Int) =
    "<font color=\"#${String.format("%X", color).substring(2)}\">$text</font>"

fun htmlBold(text: String) = "<b>${text}</b>"