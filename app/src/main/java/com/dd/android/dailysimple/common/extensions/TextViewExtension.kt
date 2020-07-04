package com.dd.android.dailysimple.common.extensions

import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.TextView

fun htmlTextColor(text: String, color: Int) =
    "<font color=\"#${String.format("%X", color).substring(2)}\">$text</font>"

fun htmlBold(text: String) = "<b>${text}</b>"

fun TextView.setUnderlineText(text: String) =
    setText(SpannableString(text).apply {
        setSpan(UnderlineSpan(), 0, length, 0)
    })