package com.dd.android.dailysimple.common.utils

import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.TextView

fun TextView.setUnderlineText(text: String) =
    setText(SpannableString(text).apply {
        setSpan(UnderlineSpan(), 0, length, 0)
    })